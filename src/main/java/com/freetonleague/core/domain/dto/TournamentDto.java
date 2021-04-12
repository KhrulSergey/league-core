package com.freetonleague.core.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TournamentDto extends TournamentBaseDto {

    /**
     * Prototype for ref to Bank-Account entity for current tournament
     */
    private Long fundAccountId;
}
