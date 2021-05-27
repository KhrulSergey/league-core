package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.TournamentWinnerPlaceType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TournamentWinnerDto {
    @ApiModelProperty(readOnly = true, notes = "no need to specify tournamentId")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long tournamentId;

    @ApiModelProperty(required = true)
    @NotNull
    private Long teamProposalId;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private TeamDto team;

    @ApiModelProperty(required = true)
    @NotNull
    private TournamentWinnerPlaceType winnerPlaceType;
}
