package com.freetonleague.core.service;


import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.domain.model.TournamentSeries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TournamentSeriesService {

    /**
     * Returns founded tournament series by id
     *
     * @param id of tournament series to search
     * @return tournament series entity or NULL of not found
     */
    TournamentSeries getSeries(long id);

    /**
     * Returns list of all tournament series filtered by requested params
     *
     * @param pageable   filtered params to search tournament series
     * @param tournament specified tournament to search suitable tournament series
     * @return list of tournament series entities
     */
    Page<TournamentSeries> getSeriesList(Pageable pageable, Tournament tournament);

    /**
     * Returns current active series for tournament
     *
     * @return active tournament series entity or NULL of not found
     */
    TournamentSeries getActiveSeriesForTournament(Tournament tournament);

    /**
     * Add new tournament series to DB.
     *
     * @param tournamentSeries to be added
     * @return Added tournament series
     */
    TournamentSeries addSeries(TournamentSeries tournamentSeries);

    /**
     * Edit tournament series in DB.
     *
     * @param tournamentSeries to be edited
     * @return Edited tournament series
     */
    TournamentSeries editSeries(TournamentSeries tournamentSeries);

    /**
     * Mark 'deleted' tournament series in DB.
     *
     * @param tournamentSeries to be deleted
     * @return tournament series with updated fields and deleted status
     */
    TournamentSeries deleteTournament(TournamentSeries tournamentSeries);

    /**
     * Returns sign of tournament series existence for specified id.
     *
     * @param id for which tournament series will be find
     * @return true is tournament series exists, false - if not
     */
    boolean isExistsTournamentSeriesById(long id);
}
