package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class GameDisciplineDto {

    private Long id;

    @NotBlank
    private String name;

    private String description;

    private String logoFileName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    List<GameDisciplineSettingsDto> gameDisciplineSettings;
}
