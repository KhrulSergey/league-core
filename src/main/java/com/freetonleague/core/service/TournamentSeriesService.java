package com.freetonleague.core.service;


import com.freetonleague.core.domain.model.TournamentRound;
import com.freetonleague.core.domain.model.TournamentSeries;
import com.freetonleague.core.domain.model.TournamentSeriesRival;
import com.freetonleague.core.domain.model.User;
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

    /**
     * Generate tournament match (OMT) for specified series and returns updated series.
     *
     * @param tournamentSeries to generate OMT (match) for
     * @return updated series with OMT match
     */
    TournamentSeries generateOmtForSeries(TournamentSeries tournamentSeries);

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

    /**
     * Returns sign if user is tournament series rival participant
     *
     * @param tournamentSeries to check participation
     * @param user             to check participation in series
     * @return true if user is participant of series or false - if not
     */
    boolean isUserSeriesRivalParticipant(TournamentSeries tournamentSeries, User user);

    /**
     * Returns sign if tournament series can be modified by series rival participant
     *
     * @param tournamentSeries to check settings
     * @return true if series can be modified or false - if not
     */
    Boolean isSeriesModifiableByRival(TournamentSeries tournamentSeries);

    /**
     * Delete tournament series rival by id
     *
     * @param tournamentSeriesRival data to delete
     * @return true if delete successfully or false - if some error occurred
     */
    boolean deleteSeriesRival(TournamentSeriesRival tournamentSeriesRival);
}
