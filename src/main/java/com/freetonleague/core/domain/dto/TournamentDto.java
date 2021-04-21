package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties("seriesList")
    private List<TournamentRoundDto> tournamentRoundList;

    private List<TournamentWinnerDto> winnerTeamProposal;
}
