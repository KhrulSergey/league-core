package com.freetonleague.core.service.implementations;


import com.freetonleague.core.domain.model.*;
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
    public List<TournamentRound> initiateTournamentBracketsWithRounds(Tournament tournament) {
        return null;
    }

    /**
     * Compose additional tournament settings based upon tournament template.
     * e.g. Generate default round settings from embedded tournament settings
     */
    @Override
    public TournamentSettings composeAdditionalTournamentSettings(Tournament tournament) {
        //no need to compose additional settings
        return tournament.getTournamentSettings();
    }

    /**
     * Compose series, matches and rivals for next opened tournament round for specified tournament.
     * Look to parents for series in opened tournamentRound and compose rivals.
     * Tournament should have opened Round or available to create new open one.
     */
    @Override
    public TournamentRound composeNextRoundForTournament(Tournament tournament) {
        return null;
    }

    /**
     * Compose rival for next series (child) by get winner of parent-specified series. If next series is full of rivals then compose matches.
     * Look to specified series - find it's winner X. Look to specified series - find it's child series Y. Compose rival od series Y as winner X.
     *
     * @param tournamentSeries to get winner and child series
     * @return tournament series with composed rival and matches
     */
    @Override
    public TournamentSeries composeRivalForChildTournamentSeries(TournamentSeries tournamentSeries) {
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
