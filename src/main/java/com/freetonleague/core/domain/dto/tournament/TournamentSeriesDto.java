package com.freetonleague.core.domain.dto.tournament;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.TournamentSeriesBracketType;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TournamentSeriesDto {

    private Long id;

    private String name;

    @ApiModelProperty(required = true)
    @NotNull
    private Long tournamentRoundId;

    @ApiModelProperty(required = true)
    @NotNull
    private TournamentSeriesBracketType type;

    @ApiModelProperty(required = true)
    @NotNull
    private TournamentStatusType status;

    private Boolean isIncomplete = false;

    private LocalDateTime startPlannedDate;

    private LocalDateTime startDate;

    @ApiModelProperty(readOnly = true, notes = "The field is set automatically")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime finishedDate;

    @ApiModelProperty(required = true)
    private List<TournamentSeriesParentDto> parentSeriesList;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<TournamentMatchDto> matchList;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<TournamentTeamProposalDto> teamProposalList;

    @ApiModelProperty(notes = "Required for set winners of series and finish series")
    private List<TournamentSeriesRivalDto> seriesRivalList;

    private TournamentSeriesRivalDto seriesWinner;

    @ApiModelProperty(notes = "Sign if series ends with a double AFK. If false and status=Finished then seriesWinner should be set.")
    @NotNull
    private Boolean hasNoWinner = false;

    @ApiModelProperty(readOnly = true, notes = "The field is set automatically")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private TournamentTeamProposalDto teamProposalWinner;
}
