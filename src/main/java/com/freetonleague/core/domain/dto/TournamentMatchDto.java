package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.TournamentMatchSeriesType;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class TournamentMatchDto {

    private Long id;

    private String name;

    @ApiModelProperty(required = true)
    @NotNull
    private Long tournamentSeriesId;

    private Set<TournamentMatchRivalDto> rivals;

    @ApiModelProperty(required = true)
    @NotNull
    private TournamentStatusType status;

    @ApiModelProperty(required = true)
    @NotNull
    private TournamentMatchSeriesType typeForSeries;

    @ApiModelProperty(required = true)
    @NotNull
    private LocalDateTime startPlannedDate;

    @ApiModelProperty(required = true)
    @NotNull
    private LocalDateTime startDate;

    @ApiModelProperty(required = false, readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime finishedDate;

    private TournamentMatchRivalDto matchWinner;
}

