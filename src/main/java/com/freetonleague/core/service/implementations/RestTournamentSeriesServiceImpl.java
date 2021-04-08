package com.freetonleague.core.service.implementations;


import com.freetonleague.core.domain.dto.TournamentSeriesDto;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.mapper.TournamentSeriesMapper;
import com.freetonleague.core.service.RestTournamentMatchService;
import com.freetonleague.core.service.RestTournamentSeriesService;
import com.freetonleague.core.service.TournamentSeriesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.Validator;

/**
 * Service-facade for managing tournament series
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RestTournamentSeriesServiceImpl implements RestTournamentSeriesService {

    private final TournamentSeriesService tournamentSeriesService;
    private final TournamentSeriesMapper tournamentSeriesMapper;
    private final RestTournamentMatchService restTournamentMatchService;
    private final Validator validator;

    /**
     * Returns founded tournament series by id
     *
     * @param id   of tournament series to search
     * @param user current user from Session
     * @return tournament series entity or NULL of not found
     */
    @Override
    public TournamentSeriesDto getSeries(long id, User user) {
        return null;
    }

    /**
     * Returns list of all tournament series filtered by requested params
     *
     * @param pageable     filtered params to search tournament series
     * @param tournamentId specified tournament to search suitable tournament series
     * @param user         current user from Session
     * @return list of tournament series entities
     */
    @Override
    public Page<TournamentSeriesDto> getSeriesList(Pageable pageable, long tournamentId, User user) {
        return null;
    }

    /**
     * Returns current active series for tournament
     *
     * @param tournamentId
     * @param user         current user from Session
     * @return active tournament series entity or NULL of not found
     */
    @Override
    public TournamentSeriesDto getActiveSeriesForTournament(long tournamentId, User user) {
        return null;
    }

    /**
     * Add new tournament series to DB.
     *
     * @param tournamentSeriesDto to be added
     * @param user                current user from Session
     * @return Added tournament series
     */
    @Override
    public TournamentSeriesDto addSeries(TournamentSeriesDto tournamentSeriesDto, User user) {
        return null;
    }

    /**
     * Generate next active series for tournament.
     *
     * @param tournamentId specified tournament to generate new tournament series
     * @param user         current user from Session
     * @return Generated tournament series or NULL if all series was formed
     */
    @Override
    public TournamentSeriesDto generateSeriesForTournament(long tournamentId, User user) {
        return null;
    }

    /**
     * Edit tournament series in DB.
     *
     * @param id                  Identity of a series
     * @param tournamentSeriesDto data to be edited
     * @param user                current user from Session
     * @return Edited tournament series
     */
    @Override
    public TournamentSeriesDto editSeries(long id, TournamentSeriesDto tournamentSeriesDto, User user) {
        return null;
    }

    /**
     * Mark 'deleted' tournament series in DB.
     *
     * @param id   identify series to be deleted
     * @param user current user from Session
     * @return tournament series with updated fields and deleted status
     */
    @Override
    public TournamentSeriesDto deleteSeries(long id, User user) {
        return null;
    }
}
