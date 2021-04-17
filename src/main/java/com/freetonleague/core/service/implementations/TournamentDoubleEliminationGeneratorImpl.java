package com.freetonleague.core.service.implementations;


import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.domain.model.TournamentSeries;
import com.freetonleague.core.service.TournamentGenerator;
import com.freetonleague.core.service.TournamentService;
import com.freetonleague.core.service.TournamentTeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component("doubleEliminationGenerator")
public class TournamentDoubleEliminationGeneratorImpl implements TournamentGenerator {

    private final TournamentTeamService tournamentTeamService;

    @Lazy
    @Autowired
    private TournamentService tournamentService;

    /**
     * Generate tournament series list with embedded list of match prototypes for specified tournament and it's settings.
     */
    @Override
    public List<TournamentSeries> generateSeriesForTournament(Tournament tournament) {
        return null;
    }
}
