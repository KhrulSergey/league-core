package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TournamentMatchDto {

    private Long id;

    private String name;

    @ApiModelProperty(required = true)
    @NotNull
    private Long tournamentSeriesId;

    @ApiModelProperty(required = true)
    @NotNull
    private TournamentStatusType status;

    @ApiModelProperty(required = true)
    @NotNull
    @Min(1)
    private Integer matchNumberInSeries;

    private List<TournamentMatchRivalDto> matchRivalList;

    private TournamentMatchRivalDto matchWinner;

    @ApiModelProperty(required = true)
    @NotNull
    private LocalDateTime startPlannedDate;

    @ApiModelProperty(required = true)
    @NotNull
    private LocalDateTime startDate;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime finishedDate;

    private List<MatchPropertyDto> matchPropertyList;
}

