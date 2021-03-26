package com.freetonleague.core.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TeamBaseDto {

    private Long id;

    @NotBlank(message = "team name must be not blank")
    private String name;

    @NotBlank(message = "team logo must be not empty")
    private String teamLogoFileName;
}
