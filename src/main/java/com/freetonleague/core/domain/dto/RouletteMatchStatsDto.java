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
public class RouletteMatchStatsDto {

    private Long betAmount;
    private RouletteBetDto winnerBet;

    private List<RouletteBetDto> betList;

}
