package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<TournamentMatchRivalDto> matchRivalList;

    private TournamentMatchRivalDto matchWinner;
}

