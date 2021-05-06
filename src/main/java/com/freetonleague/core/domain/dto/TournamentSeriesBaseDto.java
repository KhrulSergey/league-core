package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.TournamentSeriesBracketType;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TournamentSeriesBaseDto {

    private Long id;

    private String name;

    private Long tournamentRoundId;

    @ApiModelProperty(required = true)
    @NotNull
    private TournamentSeriesBracketType type;

    @ApiModelProperty(required = true)
    @NotNull
    private TournamentStatusType status;

    private LocalDateTime startPlannedDate;

    private LocalDateTime startDate;

    @ApiModelProperty(required = false, readOnly = true, notes = "The field is set automatically")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime finishedDate;

    @ApiModelProperty(required = true)
    private List<TournamentSeriesParentDto> parentSeriesList;
}
