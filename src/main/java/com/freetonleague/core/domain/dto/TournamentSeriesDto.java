package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.TournamentSeriesType;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TournamentSeriesDto {

    private Long id;

    private String name;

    private Long tournamentId;

    @ApiModelProperty(required = false, readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<TournamentMatchDto> matchesDtoList;

    @ApiModelProperty(required = true, allowableValues = "range[1, infinity]")
    @NotNull
    @Min(1)
    private Integer seriesSequencePosition;

    @ApiModelProperty(required = true)
    @NotNull
    private TournamentStatusType status;

    @ApiModelProperty(required = true)
    @NotNull
    private TournamentSeriesType type;

    @ApiModelProperty(required = true, allowableValues = "range[1, infinity]")
    @NotNull
    @Min(1)
    private Integer goalMatchCount;

    @ApiModelProperty(required = true, allowableValues = "range[1, infinity]")
    @NotNull
    @Min(1)
    private Integer goalMatchRivalCount;

    private LocalDateTime startPlannedDate;

    private LocalDateTime startDate;

    @ApiModelProperty(required = false, readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime finishedDate;
}
