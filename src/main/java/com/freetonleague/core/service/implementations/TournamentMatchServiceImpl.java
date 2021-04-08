package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.model.TournamentMatch;
import com.freetonleague.core.domain.model.TournamentSeries;
import com.freetonleague.core.repository.TournamentMatchRepository;
import com.freetonleague.core.service.TournamentMatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.Validator;

@Slf4j
@RequiredArgsConstructor
@Service
public class TournamentMatchServiceImpl implements TournamentMatchService {

    private final TournamentMatchRepository tournamentMatchRepository;
    private final Validator validator;

    /**
     * Returns founded tournament match by id
     *
     * @param id of tournament match to search
     * @return tournament match entity or NULL of not found
     */
    @Override
    public TournamentMatch getMatch(long id) {
        return null;
    }

    /**
     * Returns list of all tournament matches filtered by requested params
     *
     * @param pageable         filtered params to search tournament series
     * @param tournamentSeries specified series to search suitable tournament matches
     * @return list of tournament series entities
     */
    @Override
    public Page<TournamentMatch> getMatchList(Pageable pageable, TournamentSeries tournamentSeries) {
        return null;
    }

    /**
     * Add new tournament series to DB.
     *
     * @param tournamentMatch to be added
     * @return Added tournament series
     */
    @Override
    public TournamentMatch addMatch(TournamentMatch tournamentMatch) {
        return null;
    }

    /**
     * Edit tournament series in DB.
     *
     * @param tournamentMatch to be edited
     * @return Edited tournament series
     */
    @Override
    public TournamentMatch editMatch(TournamentMatch tournamentMatch) {
        return null;
    }

    /**
     * Mark 'deleted' tournament series in DB.
     *
     * @param tournamentMatch to be deleted
     * @return tournament series with updated fields and deleted status
     */
    @Override
    public TournamentMatch deleteMatch(TournamentMatch tournamentMatch) {
        return null;
    }

    /**
     * Returns sign of tournament series existence for specified id.
     *
     * @param id for which tournament series will be find
     * @return true is tournament series exists, false - if not
     */
    @Override
    public boolean isExistsTournamentMatchById(long id) {
        return false;
    }

    /**
     * Verify tournament match info with validation and business check
     */
    @Override
    public boolean verifyTournamentMatch(TournamentMatch tournamentMatch) {
        return false;
    }
}
