package com.freetonleague.core.domain.dto.tournament;

import com.freetonleague.core.domain.enums.tournament.TournamentMatchRivalParticipantStatusType;
import com.freetonleague.core.domain.enums.tournament.TournamentWinnerPlaceType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class TournamentSeriesRivalDto {

    private Long id;

    private Long tournamentSeriesId;

    @ApiModelProperty(required = true)
    @NotNull
    private Long teamProposalId;

    private Long parentTournamentSeriesId;

    @ApiModelProperty(required = true)
    @NotNull
    private TournamentMatchRivalParticipantStatusType status;

    private List<GameDisciplineIndicatorDto> seriesIndicatorList;

    private TournamentWinnerPlaceType wonPlaceInSeries;
}
