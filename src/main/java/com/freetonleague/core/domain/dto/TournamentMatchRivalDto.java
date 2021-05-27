package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.TournamentWinnerPlaceType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class TournamentMatchRivalDto {

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Set<TournamentMatchRivalParticipantDto> rivalParticipantList;
    @ApiModelProperty(required = true)
    private Long id;
    @ApiModelProperty(required = true)
    private Long tournamentMatchId;
    @ApiModelProperty(readOnly = true, notes = "No need to set team proposal id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long teamProposalId;
    @ApiModelProperty(readOnly = true, notes = "No need to set team id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long teamlId;

    private List<GameDisciplineIndicatorDto> matchIndicator;

    private TournamentWinnerPlaceType wonPlaceInMatch;
}
