package com.freetonleague.core.service;


import com.freetonleague.core.domain.model.TournamentMatch;
import com.freetonleague.core.domain.model.TournamentSeries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TournamentMatchService {

    /**
     * Returns founded tournament match by id
     *
     * @param id of tournament match to search
     * @return tournament match entity or NULL of not found
     */
    TournamentMatch getMatch(long id);

    /**
     * Returns list of all tournament matches filtered by requested params
     *
     * @param pageable         filtered params to search tournament matches
     * @param tournamentSeries specified series to search suitable tournament matches
     * @return list of tournament matches entities
     */
    Page<TournamentMatch> getMatchList(Pageable pageable, TournamentSeries tournamentSeries);

    /**
     * Add new tournament match to DB.
     *
     * @param tournamentMatch to be added
     * @return Added tournament series
     */
    TournamentMatch addMatch(TournamentMatch tournamentMatch);

    /**
     * Add tournament match list to DB.
     *
     * @param tournamentMatchList to be added
     * @return Added tournament series
     */
    List<TournamentMatch> addMatchList(List<TournamentMatch> tournamentMatchList);

    /**
     * Edit tournament matches in DB.
     *
     * @param tournamentMatch to be edited
     * @return Edited tournament matches
     */
    TournamentMatch editMatch(TournamentMatch tournamentMatch);

    /**
     * Mark 'deleted' tournament matches in DB.
     *
     * @param tournamentMatch to be deleted
     * @return tournament matches with updated fields and deleted status
     */
    TournamentMatch deleteMatch(TournamentMatch tournamentMatch);

    /**
     * Returns sign of tournament matches existence for specified id.
     *
     * @param id for which tournament matches will be find
     * @return true is tournament matches exists, false - if not
     */
    boolean isExistsTournamentMatchById(long id);

    /**
     * Returns sign of all match for series was finished.
     */
    boolean isAllMatchesFinishedBySeries(TournamentSeries tournamentSeries);

    /**
     * Verify tournament match info with validation and business check
     */
    boolean verifyTournamentMatch(TournamentMatch tournamentMatch);
}
