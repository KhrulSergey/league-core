package com.freetonleague.core.domain.dto.tournament;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@SuperBuilder
@NoArgsConstructor
@Data
public class TournamentRoundSettingDto implements Serializable {

    private static final long serialVersionUID = -7824689319258629088L;

    @ApiModelProperty(required = true)
    @NotNull
    @Min(1)
    private Integer roundNumber;

    /**
     * Count of rivals to be kicked off (drop out) from series.
     */
    @ApiModelProperty(required = true, notes = "Count of rivals to be kicked off (drop out) from series.")
    @NotNull
    @Min(1)
    private Integer seriesRivalKickOffCount;
}
