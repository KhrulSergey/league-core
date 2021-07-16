package com.freetonleague.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchHistoryItemDto {

    private Long id;
    private String randomOrgId;
    private Long winnerUserId;
    private Long betSum;

}
