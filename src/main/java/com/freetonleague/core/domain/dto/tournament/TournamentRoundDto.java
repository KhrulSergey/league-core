package com.freetonleague.core.domain.dto.tournament;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.tournament.GameIndicatorType;
import com.freetonleague.core.domain.enums.tournament.TournamentRoundType;
import com.freetonleague.core.domain.enums.tournament.TournamentStatusType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class TournamentRoundDto {

    private Long id;

    private String name;

    @ApiModelProperty(required = true)
    @NotNull
    private Long tournamentId;

    @ApiModelProperty(readOnly = true, notes = "allowed to add only next round to tournament. No need to specify number")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer roundNumber;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<TournamentSeriesDto> seriesList;

    @ApiModelProperty(required = true)
    @NotNull
    private TournamentStatusType status;

    @ApiModelProperty(required = true)
    @NotNull
    private TournamentRoundType type;

    @ApiModelProperty(required = true)
    @NotNull
    private Boolean isLast = false;

    private LocalDateTime startPlannedDate;

    private LocalDateTime startDate;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime finishedDate;

    private Map<GameIndicatorType, Double> gameIndicatorMultipliersMap;
}
