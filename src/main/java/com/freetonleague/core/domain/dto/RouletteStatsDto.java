package com.freetonleague.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouletteStatsDto {

    private Integer gamesPlayedToday;
    private Long tonAmountForToday;
    private Long tonAmountForAllTime;

    private Long minBetAmount;
    private Long maxBetAmount;
    private Long startBetAmount;

    private Long currentBetAmount;

    private List<RouletteBetDto> betList;

}
