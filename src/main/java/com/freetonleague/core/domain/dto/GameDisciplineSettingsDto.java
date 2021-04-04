package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.GameIndicatorType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
public class GameDisciplineSettingsDto {

    private Long id;

    @NotBlank
    private String name;

    private boolean isPrimary;

    @NotNull
    private Long gameDisciplineId;

    private Map<GameIndicatorType, Object> gameOptimalIndicators;
}
