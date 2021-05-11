package com.freetonleague.core.service;


import com.freetonleague.core.domain.model.TournamentRound;
import com.freetonleague.core.domain.model.TournamentSeries;
import com.freetonleague.core.domain.model.TournamentSeriesRival;
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
     * @param pageable        filtered params to search tournament series
     * @param tournamentRound specified tournament round to search suitable tournament series
     * @return list of tournament series entities
     */
    Page<TournamentSeries> getSeriesList(Pageable pageable, TournamentRound tournamentRound);

//    /**
//     * Returns current active series for tournament
//     *
//     * @return active tournament series entity or NULL of not found
//     */
//    TournamentSeries getActiveSeriesForTournament(Tournament tournament);

    /**
     * Add new tournament series to DB.
     *
     * @param tournamentSeries to be added
     * @return Added tournament series
     */
    TournamentSeries addSeries(TournamentSeries tournamentSeries);

//    /**
//     * Generate tournament series list for specified tournament and save to DB.
//     *
//     * @param tournament to generate series for
//     * @return Sign of series list was created
//     */
//    boolean generateSeriesForTournament(Tournament tournament);

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
    TournamentSeries deleteSeries(TournamentSeries tournamentSeries);

    /**
     * Returns sign of tournament series existence for specified id.
     *
     * @param id for which tournament series will be find
     * @return true is tournament series exists, false - if not
     */
    boolean isExistsTournamentSeriesById(long id);

    /**
     * Returns sign of all series for round was finished.
     */
    boolean isAllSeriesFinishedByRound(TournamentRound tournamentRound);

    /**
     * Returns founded tournament series rival by id
     *
     * @param id of tournament series rival to search
     * @return tournament series rival entity or NULL of not found
     */
    TournamentSeriesRival getSeriesRival(long id);
}
