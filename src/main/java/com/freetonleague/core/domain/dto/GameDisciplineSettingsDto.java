package com.freetonleague.core.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class GameDisciplineSettingsDto {

    private Long id;

    @ApiModelProperty(required = true)
    @NotBlank
    private String name;

    private boolean isPrimary;

    @ApiModelProperty(required = true)
    @NotNull
    private Long gameDisciplineId;

    @ApiModelProperty(required = true)
    @NotNull
    @NotEmpty
    private List<GameDisciplineIndicatorDto> gameOptimalIndicators;

    @ApiModelProperty(required = true)
    @NotNull
    private Integer matchRivalCount = 2;

    /**
     * Count of rivals to be kicked off (drop out) from series. Default Value.
     */
    @ApiModelProperty(required = true, notes = "Count of rivals to be kicked off (drop out) from series. Default Value.")
    @NotNull
    private Integer seriesRivalKickOffDefaultCount = 1;
}
