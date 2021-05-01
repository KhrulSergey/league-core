package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.TournamentRoundType;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TournamentRoundDto {

    private Long id;

    private String name;

    private Long tournamentId;

    @ApiModelProperty(readOnly = true, notes = "allowed to add only next round to tournament. No need to specify number")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer roundNumber;

    @ApiModelProperty(required = false, readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<TournamentSeriesBaseDto> seriesList;

    @ApiModelProperty(required = true)
    @NotNull
    private TournamentStatusType status;

    @ApiModelProperty(required = true)
    @NotNull
    private TournamentRoundType type;

    private LocalDateTime startPlannedDate;

    private LocalDateTime startDate;

    @ApiModelProperty(required = false, readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime finishedDate;
}