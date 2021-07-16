package com.freetonleague.core.domain.dto.tournament;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Model-view for list of tournament with discord data about room and user-participant Id
 */
@SuperBuilder
@Data
@NoArgsConstructor
public class TournamentDiscordInfoListDto {

    /**
     * List of tournament with discord data about room and user-participant Id
     */
    private List<TournamentDiscordChannelDto> rooms;
}
