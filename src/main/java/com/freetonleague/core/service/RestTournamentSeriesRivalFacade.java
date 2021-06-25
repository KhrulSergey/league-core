package com.freetonleague.core.service;


import com.freetonleague.core.domain.dto.TournamentSeriesRivalDto;
import com.freetonleague.core.domain.model.TournamentSeriesRival;
import com.freetonleague.core.domain.model.User;

public interface RestTournamentSeriesRivalFacade {

    /**
     * Delete tournament series rival.
     *
     * @param id   identify of series rival to be deleted
     * @param user current user from Session
     */
    void deleteSeriesRival(long id, User user);

    /**
     * Returns tournament series rival by id with privacy check
     */
    TournamentSeriesRival getVerifiedSeriesRivalById(long id);

    /**
     * Returns tournament series rival by dto with privacy check (modify Status, WonPlaceInMatch, Indicators for existed rival)
     */
    TournamentSeriesRival getVerifiedSeriesRivalByDto(TournamentSeriesRivalDto seriesRivalDto);

    /**
     * Returns tournament series rival by dto with privacy check for modifying by rivals
     */
    TournamentSeriesRival getVerifiedSeriesRivalByDtoForRival(TournamentSeriesRivalDto seriesRivalDto);
}
