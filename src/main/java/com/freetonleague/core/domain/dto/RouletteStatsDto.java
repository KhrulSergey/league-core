package com.freetonleague.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouletteStatsDto {

    private Integer gamesPlayedToday;
    private Double tonAmountForToday;
    private Double tonAmountForAllTime;

    private Double minBetAmount;
    private Double maxBetAmount;
    private Double startBetAmount;

    private Integer currentBetAmount;
    private LocalDateTime startTime;

    private List<RouletteBetDto> betList;

    private boolean started;

    private RouletteBetDto winnerBet;
    private Double winAmount;

}
