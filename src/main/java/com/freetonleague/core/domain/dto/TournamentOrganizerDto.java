package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.TournamentOrganizerStatusType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class TournamentOrganizerDto {

    private Long id;

    private Long tournamentId;

    @NotBlank
    private String userLeagueId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UserDto user;

    @NotNull
    private TournamentOrganizerStatusType status;

    private Set<String> privilegeList;
}
