package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TournamentDto extends TournamentBaseDto {

    /**
     * Prototype for ref to Bank-Account entity for current tournament
     */
    @ApiModelProperty(readOnly = true)
    private Long fundAccountId;

    //Detailed settings
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties("seriesList")
    private List<TournamentRoundDto> tournamentRoundList;

    @ApiModelProperty(notes = "need to set at least one element with 'force' finishing tournament")
    private List<TournamentWinnerDto> tournamentWinnerList;

    @ApiModelProperty(notes = "need to set true with 'force' finishing tournament")
    private Boolean isForcedFinished = false;
}
