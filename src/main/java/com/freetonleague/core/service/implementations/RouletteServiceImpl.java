package com.freetonleague.core.service.implementations;

import com.freetonleague.core.config.properties.AppRouletteProperties;
import com.freetonleague.core.domain.dto.RouletteMatchHistoryItemDto;
import com.freetonleague.core.domain.dto.RandomLongDto;
import com.freetonleague.core.domain.dto.RouletteBetDto;
import com.freetonleague.core.domain.dto.RouletteStatsDto;
import com.freetonleague.core.domain.dto.finance.AccountInfoDto;
import com.freetonleague.core.domain.entity.RouletteBetEntity;
import com.freetonleague.core.domain.entity.RouletteMatchEntity;
import com.freetonleague.core.domain.enums.AccountHolderType;
import com.freetonleague.core.domain.enums.AccountTransactionStatusType;
import com.freetonleague.core.domain.enums.TransactionTemplateType;
import com.freetonleague.core.domain.enums.TransactionType;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.domain.model.finance.Account;
import com.freetonleague.core.domain.model.finance.AccountTransaction;
import com.freetonleague.core.mapper.RouletteMapper;
import com.freetonleague.core.repository.RouletteBetRepository;
import com.freetonleague.core.repository.RouletteMatchRepository;
import com.freetonleague.core.service.RandomService;
import com.freetonleague.core.service.RouletteService;
import com.freetonleague.core.service.UserService;
import com.freetonleague.core.service.financeUnit.FinancialUnitService;
import com.freetonleague.core.service.financeUnit.RestFinancialUnitFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouletteServiceImpl implements RouletteService {

    private final AppRouletteProperties rouletteProperties;
    private final RandomService randomService;
    private final UserService userService;
    private final RouletteBetRepository rouletteBetRepository;
    private final RouletteMatchRepository rouletteMatchRepository;
    private final RouletteMapper rouletteMapper;
    private final RestFinancialUnitFacade restFinancialUnitFacade;
    private final FinancialUnitService financialUnitService;

    @Override
    @Scheduled(fixedDelay = 3000L)
    @Transactional
    public void update() {
        RouletteMatchEntity currentMatchLocked = getCurrentMatchLocked();

        if (currentMatchLocked == null) {
            createNewMatch();
            return;
        }

        if (isReadyToStart(currentMatchLocked)) {
            draw(currentMatchLocked);
            createNewMatch();
        }

    }

    private void createNewMatch() {
        rouletteMatchRepository.save(
                RouletteMatchEntity.builder()
                        .finished(false)
                        .build()
        );
    }

    @Override
    @Transactional
    public void makeBet(User authUser, Long betAmount) {
        User user = userService.findByUsername(authUser.getUsername());

        RouletteMatchEntity currentMatch = getCurrentMatchLocked();

        List<RouletteBetEntity> bets = currentMatch.getBets();

        long ticketNumberFrom = currentMatch.getLastTicketNumber() + 1;
        long ticketNumberTo = ticketNumberFrom + betAmount;

        bets.add(RouletteBetEntity.builder()
                .tonAmount(betAmount)
                .ticketNumberFrom(ticketNumberFrom)
                .ticketNumberTo(ticketNumberTo)
                .user(user)
                .build());

        currentMatch.setLastTicketNumber(ticketNumberTo);

        AccountInfoDto bankAccount = restFinancialUnitFacade.findAccountByExternalAddress("ROULETTE_BANK");

        Account userAccount = financialUnitService.getAccountByHolderExternalGUIDAndType(
                user.getLeagueId(), AccountHolderType.USER);

        AccountTransaction accountTransaction = AccountTransaction.builder()
                .amount(betAmount.doubleValue())
                .sourceAccount(userAccount)
                .targetAccount(financialUnitService.getAccountByGUID(UUID.fromString(bankAccount.getGUID())))
                .transactionType(TransactionType.PAYMENT)
                .transactionTemplateType(TransactionTemplateType.PRODUCT_PURCHASE)
                .status(AccountTransactionStatusType.FINISHED)
                .build();

        financialUnitService.createTransaction(accountTransaction);
    }

    @Override
    public RouletteStatsDto getStats() {
        Long sumForToday = 0L;// = rouletteMatchRepository.betSumForToday();
        Long sumForAllTime = 0L;// = rouletteMatchRepository.betSumForAllTime();

        List<RouletteBetEntity> bets = getCurrentMatch().getBets();

        long sum = bets.stream()
                .mapToLong(RouletteBetEntity::getTonAmount)
                .sum();

        List<RouletteBetDto> betList = bets.stream()
                .map(entity -> rouletteMapper.toBetDto(entity, (double) (entity.getTonAmount() * 100 / sum)))
                .collect(Collectors.toList());

        return RouletteStatsDto.builder()
                .gamesPlayedToday(rouletteMatchRepository.count())
                .tonAmountForToday(sumForToday)
                .tonAmountForAllTime(sumForAllTime)
                .minBetAmount(rouletteProperties.getMinBetAmount())
                .maxBetAmount(rouletteProperties.getMaxBetAmount())
                .startBetAmount(rouletteProperties.getStartBetAmount())
                .currentBetAmount(sum)
                .betList(betList)
                .build();
    }

    @Override
    public List<RouletteMatchHistoryItemDto> getMatchHistory() {
        return rouletteMapper.toMatchHistoryList(
                rouletteMatchRepository.findAllByFinishedTrueOrderByCreatedAt()
        );
    }

    private RouletteMatchEntity getCurrentMatchLocked() {
        return rouletteMatchRepository.findLockedByFinishedFalse();
    }

    private RouletteMatchEntity getCurrentMatch() {
        return rouletteMatchRepository.findByFinishedFalse();
    }

    private boolean isReadyToStart(RouletteMatchEntity match) {
        List<RouletteBetEntity> bets = match.getBets();

        int playersCount = bets.size();


        if (rouletteProperties.getMinPlayersCount() < playersCount) {
            return false;
        }

        if (rouletteProperties.getMaxPlayersCount() >= playersCount) {
            return true;
        }

        if (LocalDateTime.now().isAfter(match.getShouldStartedAfter())) {
            return true;
        }

        double betSum = bets.stream()
                .mapToDouble(RouletteBetEntity::getTonAmount)
                .sum();

        return betSum >= rouletteProperties.getStartBetAmount();
    }

    private void draw(RouletteMatchEntity match) {
        List<RouletteBetEntity> bets = match.getBets();

        long betSum = bets.stream()
                .mapToLong(RouletteBetEntity::getTonAmount)
                .sum();

        RandomLongDto winnerTicket = randomService.getRandomLong(betSum);

        RouletteBetEntity winnerBet = getWinner(bets, winnerTicket.getValue());

        match.setFinished(true);
        match.setRandomOrgId(winnerTicket.getRandomizeId());
        match.setBetSum(betSum);
        match.setFinishedAt(LocalDateTime.now());
        match.setWinnerBet(winnerBet);

        AccountInfoDto bankAccount = restFinancialUnitFacade.findAccountByExternalAddress("ROULETTE_BANK");

        Account winnerAccount = financialUnitService.getAccountByHolderExternalGUIDAndType(
                winnerBet.getUser().getLeagueId(), AccountHolderType.USER);

        double winAmount = betSum * rouletteProperties.getCommissionFactor();

        AccountTransaction accountTransaction = AccountTransaction.builder()
                .amount(winAmount)
                .sourceAccount(financialUnitService.getAccountByGUID(UUID.fromString(bankAccount.getGUID())))
                .targetAccount(winnerAccount)
                .transactionType(TransactionType.PAYMENT)
                .transactionTemplateType(TransactionTemplateType.PRODUCT_PURCHASE)
                .status(AccountTransactionStatusType.FINISHED)
                .build();

        financialUnitService.createTransaction(accountTransaction);

    }

    private RouletteBetEntity getWinner(List<RouletteBetEntity> bets, Long winnerTicketNumber) {
        List<RouletteBetEntity> winnerBetsList = bets.stream()
                .filter(bet -> isWinner(bet, winnerTicketNumber))
                .collect(Collectors.toList());

        if (winnerBetsList.size() != 1) {
            throw new IllegalStateException();
        }

        return winnerBetsList.get(0);
    }

    private boolean isWinner(RouletteBetEntity bet, Long winnerTicketNumber) {
        Long numberFrom = bet.getTicketNumberFrom();
        Long numberTo = bet.getTicketNumberTo();

        return winnerTicketNumber > numberFrom && winnerTicketNumber < numberTo;
    }

}
