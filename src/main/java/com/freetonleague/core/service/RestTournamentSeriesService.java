package com.freetonleague.core.service;


import com.freetonleague.core.domain.dto.TournamentSeriesDto;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestTournamentSeriesService {

    /**
     * Returns founded tournament series by id
     *
     * @param id   of tournament series to search
     * @param user current user from Session
     * @return tournament series entity or NULL of not found
     */
    TournamentSeriesDto getSeries(long id, User user);

    /**
     * Returns list of all tournament series filtered by requested params
     *
     * @param pageable     filtered params to search tournament series
     * @param tournamentId specified tournament to search suitable tournament series
     * @param user         current user from Session
     * @return list of tournament series entities
     */
    Page<TournamentSeriesDto> getSeriesList(Pageable pageable, long tournamentId, User user);

    /**
     * Returns current active series for tournament
     *
     * @param user current user from Session
     * @return active tournament series entity or NULL of not found
     */
    TournamentSeriesDto getActiveSeriesForTournament(long tournamentId, User user);

    /**
     * Add new tournament series to DB.
     *
     * @param tournamentSeriesDto to be added
     * @param user                current user from Session
     * @return Added tournament series
     */
    TournamentSeriesDto addSeries(TournamentSeriesDto tournamentSeriesDto, User user);

    /**
     * Generate next active series for tournament.
     *
     * @param tournamentId specified tournament to generate new tournament series
     * @param user         current user from Session
     * @return Generated tournament series or NULL if all series was formed
     */
    TournamentSeriesDto generateSeriesForTournament(long tournamentId, User user);

    /**
     * Edit tournament series in DB.
     *
     * @param id                  Identity of a series
     * @param tournamentSeriesDto data to be edited
     * @param user                current user from Session
     * @return Edited tournament series
     */
    TournamentSeriesDto editSeries(long id, TournamentSeriesDto tournamentSeriesDto, User user);

    /**
     * Mark 'deleted' tournament series in DB.
     *
     * @param id   identify series to be deleted
     * @param user current user from Session
     * @return tournament series with updated fields and deleted status
     */
    TournamentSeriesDto deleteSeries(long id, User user);
}
