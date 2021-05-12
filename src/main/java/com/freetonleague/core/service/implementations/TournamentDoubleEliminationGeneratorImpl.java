package com.freetonleague.core.service.implementations;


import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.domain.model.TournamentMatch;
import com.freetonleague.core.domain.model.TournamentRound;
import com.freetonleague.core.domain.model.TournamentSeries;
import com.freetonleague.core.service.TournamentGenerator;
import com.freetonleague.core.service.TournamentProposalService;
import com.freetonleague.core.service.TournamentService;
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

    private final TournamentProposalService tournamentProposalService;

    @Lazy
    @Autowired
    private TournamentService tournamentService;

    /**
     * Generate tournament round list with embedded list of series & matches prototypes for specified tournament and it's settings.
     */
    @Override
    public List<TournamentRound> generateRoundsForTournament(Tournament tournament) {
        return null;
    }

    /**
     * Compose series, matches and rivals for tournament round. Look to parents for series in specified tournamentRound and compose rivals.
     */
    @Override
    public TournamentRound composeNextRoundForTournament(TournamentRound tournamentRound) {
        return null;
    }

    /**
     * Generate new match (OMT) for series.
     *
     * @param tournamentSeries to compose series for
     * @return tournament match with the same settings as others in series
     */
    @Override
    public TournamentMatch generateOmtForSeries(TournamentSeries tournamentSeries) {
        return null;
    }
}
