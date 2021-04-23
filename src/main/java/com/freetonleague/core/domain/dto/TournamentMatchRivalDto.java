package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.TournamentWinnerPlaceType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class TournamentMatchRivalDto extends TournamentMatchRivalBaseDto {

    @ApiModelProperty(required = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Set<TournamentMatchRivalParticipantDto> rivalParticipantList;

    private List<GameDisciplineIndicatorDto> matchIndicator;

    @ApiModelProperty(required = false)
    private TournamentWinnerPlaceType placeInMatch;
}
