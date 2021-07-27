package com.freetonleague.core.service.implementations;

import com.freetonleague.core.config.properties.AppRouletteProperties;
import com.freetonleague.core.domain.dto.RandomLongDto;
import com.freetonleague.core.domain.dto.RouletteBetDto;
import com.freetonleague.core.domain.dto.RouletteMatchHistoryItemDto;
import com.freetonleague.core.domain.dto.RouletteMatchStatsDto;
import com.freetonleague.core.domain.dto.RouletteStatsDto;
import com.freetonleague.core.domain.dto.finance.AccountInfoDto;
import com.freetonleague.core.domain.entity.RouletteBetEntity;
import com.freetonleague.core.domain.entity.RouletteMatchEntity;
import com.freetonleague.core.domain.enums.finance.AccountHolderType;
import com.freetonleague.core.domain.enums.finance.AccountTransactionStatusType;
import com.freetonleague.core.domain.enums.finance.AccountTransactionTemplateType;
import com.freetonleague.core.domain.enums.finance.AccountTransactionType;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    public RouletteStatsDto makeBet(User authUser, Long betAmount) {
        User user = userService.findByUsername(authUser.getUsername());

        if (betAmount > rouletteProperties.getMaxBetAmount()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }

        if (betAmount < rouletteProperties.getMinBetAmount()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }

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
                .transactionType(AccountTransactionType.PAYMENT)
                .transactionTemplateType(AccountTransactionTemplateType.PRODUCT_PURCHASE)
                .status(AccountTransactionStatusType.FINISHED)
                .build();

        financialUnitService.createTransaction(accountTransaction);

        return getStatsForMatch(currentMatch);
    }

    @Override
    public RouletteStatsDto getStats() {
        return getStatsForMatch(getCurrentMatch());
    }

    private RouletteStatsDto getStatsForMatch(RouletteMatchEntity match) {
        Long sumForToday = rouletteBetRepository.sumForAllTime();
        Long sumForAllTime = rouletteBetRepository.sumForPeriod(
                LocalDateTime.of(LocalDate.now(), LocalTime.MIN),
                LocalDateTime.of(LocalDate.now(), LocalTime.MAX)
        );

        List<RouletteBetEntity> bets = match.getBets();

        long sum = bets.stream()
                .mapToLong(RouletteBetEntity::getTonAmount)
                .sum();

        List<RouletteBetDto> betList = bets.stream()
                .map(entity -> rouletteMapper.toBetDto(entity, entity.getTonAmount() * 100D / sum))
                .collect(Collectors.toList());

        return RouletteStatsDto.builder()
                .id(match.getId())
                .gamesPlayedToday(rouletteMatchRepository.count())
                .tonAmountForToday(sumForToday)
                .tonAmountForAllTime(sumForAllTime)
                .minBetAmount(rouletteProperties.getMinBetAmount())
                .maxBetAmount(rouletteProperties.getMaxBetAmount())
                .startBetAmount(rouletteProperties.getStartBetAmount())
                .currentBetAmount(sum)
                .shouldStartedAfter(match.getShouldStartedAfter())
                .betList(betList)
                .build();
    }

    @Override
    public Page<RouletteMatchHistoryItemDto> getMatchHistory(Pageable pageable) {
        return rouletteMapper.toMatchHistoryPage(
                rouletteMatchRepository.findAllByFinishedTrueOrderByCreatedAt(pageable)
        );
    }

    @Override
    public RouletteMatchStatsDto getMatchStatsById(Long matchId) {
        RouletteMatchEntity matchEntity = rouletteMatchRepository.findById(matchId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        List<RouletteBetEntity> bets = matchEntity.getBets();

        long sum = bets.stream()
                .mapToLong(RouletteBetEntity::getTonAmount)
                .sum();

        List<RouletteBetDto> betList = bets.stream()
                .map(entity -> rouletteMapper.toBetDto(entity, entity.getTonAmount() * 100D / sum))
                .collect(Collectors.toList());

        return RouletteMatchStatsDto.builder()
                .winnerBet(
                        rouletteMapper.toBetDto(matchEntity.getWinnerBet(),
                                matchEntity.getWinnerBet().getTonAmount() * 100D / sum)
                )
                .betAmount(sum)
                .betList(betList)
                .build();
    }

    private RouletteMatchEntity getCurrentMatchLocked() {
        return rouletteMatchRepository.findLockedByFinishedFalse();
    }

    private RouletteMatchEntity getCurrentMatch() {
        return rouletteMatchRepository.findByFinishedFalse();
    }

    public boolean isReadyToStart(RouletteMatchEntity match) {
        List<RouletteBetEntity> bets = match.getBets();

        int playersCount = bets.size();

        if (rouletteProperties.getMinPlayersCount() > playersCount) {
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
                .transactionType(AccountTransactionType.PAYMENT)
                .transactionTemplateType(AccountTransactionTemplateType.PRODUCT_PURCHASE)
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
