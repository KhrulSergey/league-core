package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.TournamentMatchRivalParticipantStatusType;
import com.freetonleague.core.domain.enums.TournamentWinnerPlaceType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Data
public class TournamentMatchRivalDto {

    private Long id;

    private Long tournamentMatchId;

    @ApiModelProperty(notes = "system add all participant (member) from 'main' tournamentTeamProposal by default")
    Set<TournamentMatchRivalParticipantDto> rivalParticipantList;
    @ApiModelProperty(notes = "Required for creation.")
    private Long teamProposalId;
    @ApiModelProperty(readOnly = true, notes = "No need to set team id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long teamId;

    private List<GameDisciplineIndicatorDto> matchIndicator;

    private TournamentWinnerPlaceType wonPlaceInMatch;
    @ApiModelProperty(required = true)
    @NotNull
    private TournamentMatchRivalParticipantStatusType status;
}
