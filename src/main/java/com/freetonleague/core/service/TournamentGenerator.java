package com.freetonleague.core.service;


import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.domain.model.TournamentSeries;

import java.util.List;

public interface TournamentGenerator {

    /**
     * Generate tournament series list with embedded list of match prototypes for specified tournament and it's settings.
     *
     * @param tournament to generate series for
     * @return tournament series list with embedded list of match prototypes
     */
    List<TournamentSeries> generateSeriesForTournament(Tournament tournament);
}
