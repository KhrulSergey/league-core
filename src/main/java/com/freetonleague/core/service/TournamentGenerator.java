package com.freetonleague.core.service;


import com.freetonleague.core.domain.model.*;

import java.util.List;

public interface TournamentGenerator {

    /**
     * Generate tournament round list with embedded list of series & matches prototypes for specified tournament and it's settings.
     * Tournament should be with empty series list.
     *
     * @param tournament to generate series for
     * @return tournament series list with embedded list of match prototypes
     */
    List<TournamentRound> initiateTournamentBracketsWithRounds(Tournament tournament);

    /**
     * Compose additional tournament settings based upon tournament template.
     * e.g. Generate default round settings from embedded tournament settings
     *
     * @param tournament to compose settings
     * @return supplemented settings for specified tournament
     */
    TournamentSettings composeAdditionalTournamentSettings(Tournament tournament);

    /**
     * Compose series, matches and rivals for next opened tournament round for specified tournament.
     * Look to parents for series in opened tournamentRound and compose rivals.
     * Tournament should have opened Round or available to create new open one.
     *
     * @param tournament to compose new opened round with series
     * @return tournament round with embedded list of series and matches
     */
    TournamentRound composeNextRoundForTournament(Tournament tournament);

    /**
     * Generate new match (OMT) for series.
     *
     * @param tournamentSeries to compose series for
     * @return tournament match with the same settings as others in series
     */
    TournamentMatch generateOmtForSeries(TournamentSeries tournamentSeries);
}
