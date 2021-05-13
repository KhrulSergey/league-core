package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TournamentSeriesDto extends TournamentSeriesBaseDto {

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<TournamentMatchDto> matchList;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<TournamentTeamProposalDto> teamProposalList;

    @ApiModelProperty(notes = "Required for set winners of series and finish series")
    private List<TournamentSeriesRivalDto> seriesRivalList;

    @ApiModelProperty(required = false, readOnly = true, notes = "The field is set automatically")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private TournamentTeamProposalBaseDto teamProposalWinner;
}
