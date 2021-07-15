package com.freetonleague.core.domain.dto.tournament;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

/**
 * Model-view of tournament with discord data about room and user-participant Id
 */
@SuperBuilder
@Data
@NoArgsConstructor
public class TournamentDiscordChannelDto {

    @JsonProperty(value = "room_id")
    private String discordChannelId;

    private String tournamentGUID;

    @JsonProperty(value = "game")
    private String gameDisciplineName;

    @JsonProperty(value = "players")
    private Set<String> tournamentInvolvedUsersDiscordIdList;
}
