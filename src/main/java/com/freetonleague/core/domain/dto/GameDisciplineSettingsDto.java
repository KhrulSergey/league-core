package com.freetonleague.core.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class GameDisciplineSettingsDto {

    private Long id;

    @NotBlank
    private String name;

    private boolean isPrimary;

    @NotNull
    private Long gameDisciplineId;

    @NotNull
    @NotEmpty
    private List<GameDisciplineIndicatorDto> gameOptimalIndicators;
}
