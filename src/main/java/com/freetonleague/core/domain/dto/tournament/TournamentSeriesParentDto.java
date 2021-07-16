package com.freetonleague.core.domain.dto.tournament;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.TournamentSeriesBracketType;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class TournamentSeriesParentDto {

    private Long id;

    private String name;

    private Long tournamentRoundId;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private TournamentTeamProposalDto teamProposalWinner;

    @ApiModelProperty(required = true)
    @NotNull
    private TournamentSeriesBracketType type;

    @ApiModelProperty(required = true)
    @NotNull
    private TournamentStatusType status;

    private LocalDateTime startPlannedDate;

    private LocalDateTime startDate;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime finishedDate;
}
