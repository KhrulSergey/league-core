package com.freetonleague.core.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TournamentDto extends TournamentBaseDto {

    /**
     * Prototype for ref to Bank-Account entity for current tournament
     */
    private Long fundAccountId;


    //Detailed settings
    private List<TournamentSeriesDto> tournamentSeriesList;

    private List<TournamentWinnerDto> winnerTeamProposal;
}
