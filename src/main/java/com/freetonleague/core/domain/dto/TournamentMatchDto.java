package com.freetonleague.core.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class TournamentMatchDto extends TournamentMatchBaseDto {

    @ApiModelProperty(required = true)
    @NotNull
    private Long tournamentSeriesId;

    private Set<TournamentMatchRivalDto> rivalList;

    private TournamentMatchRivalDto matchWinner;
}

