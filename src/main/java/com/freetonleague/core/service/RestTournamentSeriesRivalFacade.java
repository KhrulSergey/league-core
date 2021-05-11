package com.freetonleague.core.service;


import com.freetonleague.core.domain.dto.TournamentSeriesRivalDto;
import com.freetonleague.core.domain.model.TournamentSeriesRival;

public interface RestTournamentSeriesRivalFacade {

    /**
     * Returns tournament series rival by id with privacy check
     */
    TournamentSeriesRival getVerifiedSeriesRivalById(long id);

    /**
     * Returns tournament series rival by dto with privacy check
     */
    TournamentSeriesRival getVerifiedSeriesRivalByDto(TournamentSeriesRivalDto seriesRivalDto);
}
