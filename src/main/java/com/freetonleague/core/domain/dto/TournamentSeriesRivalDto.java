package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.TournamentStatusType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class TournamentSeriesRivalDto {

    private Long id;

    private Long tournamentSeriesId;

    private Long teamProposalId;

    private Long parentTournamentSeriesId;

    @ApiModelProperty(required = true)
    @NotNull
    private TournamentStatusType status;

    private List<GameDisciplineIndicatorDto> seriesIndicatorList;

    private Integer wonPlaceInSeries;
}
