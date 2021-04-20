package com.freetonleague.core.service;


import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.domain.model.TournamentRound;

import java.util.List;

public interface TournamentGenerator {

    /**
     * Generate tournament round list with embedded list of series & matches prototypes for specified tournament and it's settings.
     * Tournament should be with empty series list.
     *
     * @param tournament to generate series for
     * @return tournament series list with embedded list of match prototypes
     */
    List<TournamentRound> generateRoundsForTournament(Tournament tournament);
}
