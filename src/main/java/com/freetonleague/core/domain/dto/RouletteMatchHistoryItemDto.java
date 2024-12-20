package com.freetonleague.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouletteMatchHistoryItemDto {

    private Long id;
    private String randomOrgId;
    private UUID winnerUserLeagueId;
    private Double winnerChance;
    private Long winnerTicketNumberFrom;
    private Long winnerTicketNumberTo;
    private Long winnerBetAmount;
    private Long betSum;

}
