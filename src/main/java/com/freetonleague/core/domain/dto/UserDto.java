package com.freetonleague.core.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
public class UserDto {

    @NotNull(message = "leagueID must be not null")
    private UUID leagueId;

    @Size(max = 25)
    private String username;

    private String avatarFileName;

    private String discordId;
}
