package com.freetonleague.core.service.implementations;

import com.freetonleague.core.config.properties.AppRouletteProperties;
import com.freetonleague.core.domain.dto.RandomLongDto;
import com.freetonleague.core.domain.entity.RouletteBetEntity;
import com.freetonleague.core.domain.entity.RouletteMatchEntity;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.repository.RouletteBetRepository;
import com.freetonleague.core.repository.RouletteMatchRepository;
import com.freetonleague.core.service.RandomService;
import com.freetonleague.core.service.RouletteService;
import com.freetonleague.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouletteServiceImpl implements RouletteService {

    private final AppRouletteProperties rouletteProperties;
    private final RandomService randomService;
    private final UserService userService;
    private final RouletteBetRepository rouletteBetRepository;
    private final RouletteMatchRepository rouletteMatchRepository;

    @Transactional
    public void makeBet(User authUser, Long betAmount) {
        User user = userService.findByUsername(authUser.getUsername());

        RouletteMatchEntity currentMatch = getCurrentMatch();

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
    }

    private RouletteMatchEntity getCurrentMatch() {
        return rouletteMatchRepository.findLockedByFinishedFalse();
    }

    private void checkState(RouletteMatchEntity match) {
        List<RouletteBetEntity> bets = match.getBets();

        double betSum = bets.stream()
                .mapToDouble(RouletteBetEntity::getTonAmount)
                .sum();

        if (betSum >= rouletteProperties.getStartBetAmount()) {

        }
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
        match.setWinnerBet(winnerBet);

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
