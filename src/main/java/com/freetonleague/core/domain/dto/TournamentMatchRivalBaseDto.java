package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TournamentMatchRivalBaseDto {

    @ApiModelProperty(required = true)
    private Long id;

    @ApiModelProperty(required = true)
    private Long tournamentMatchId;

    @ApiModelProperty(required = false, notes = "No need to set team proposal id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long teamProposalId;
}
