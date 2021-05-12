package com.freetonleague.core.service;


import com.freetonleague.core.domain.dto.TournamentSeriesDto;
import com.freetonleague.core.domain.model.TournamentSeries;
import com.freetonleague.core.domain.model.User;

public interface RestTournamentSeriesFacade {

    /**
     * Returns founded tournament series by id
     *
     * @param id   of tournament series to search
     * @param user current user from Session
     * @return tournament series entity or NULL of not found
     */
    TournamentSeriesDto getSeries(long id, User user);

//    /**
//     * Returns list of all tournament series filtered by requested params
//     *
//     * @param pageable     filtered params to search tournament series
//     * @param tournamentId specified tournament to search suitable tournament series
//     * @param user         current user from Session
//     * @return list of tournament series entities
//     */
//    Page<TournamentSeriesDto> getSeriesList(Pageable pageable, long tournamentId, User user);

//    /**
//     * Returns current active series for tournament
//     *
//     * @param user current user from Session
//     * @return active tournament series entity or NULL of not found
//     */
//    TournamentSeriesDto getActiveSeriesForTournament(long tournamentId, User user);

    /**
     * Add new tournament series.
     *
     * @param tournamentSeriesDto to be added
     * @param user                current user from Session
     * @return Added tournament series
     */
    TournamentSeriesDto addSeries(TournamentSeriesDto tournamentSeriesDto, User user);

    /**
     * Generate OMT (match) for specified series.
     *
     * @param id   of tournament series to generate OMT (match)
     * @param user current user from Session
     */
    TournamentSeriesDto generateOmtForSeries(long id, User user);

    /**
     * Edit tournament series.
     *
     * @param id                  Identity of a series
     * @param tournamentSeriesDto data to be edited
     * @param user                current user from Session
     * @return Edited tournament series
     */
    TournamentSeriesDto editSeries(long id, TournamentSeriesDto tournamentSeriesDto, User user);

    /**
     * Mark 'deleted' tournament series.
     *
     * @param id   identify series to be deleted
     * @param user current user from Session
     * @return tournament series with updated fields and deleted status
     */
    TournamentSeriesDto deleteSeries(long id, User user);

    /**
     * Returns tournament series by id and user with privacy check
     */
    TournamentSeries getVerifiedSeriesById(long id);

    /**
     * Getting tournament settings by DTO with privacy check
     */
    TournamentSeries getVerifiedSeriesByDto(TournamentSeriesDto tournamentSeriesDto);
}
