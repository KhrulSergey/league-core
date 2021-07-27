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

    private Long id;

    private Long gamesPlayedToday;
    private Long tonAmountForToday;
    private Long tonAmountForAllTime;

    private Long minBetAmount;
    private Long maxBetAmount;
    private Long startBetAmount;

    private Long currentBetAmount;

    private LocalDateTime shouldStartedAfter;

    private List<RouletteBetDto> betList;

}
