package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.MatchHistoryItemDto;
import com.freetonleague.core.domain.dto.RouletteStatsDto;
import com.freetonleague.core.domain.model.User;

import java.util.List;

public interface RouletteService {

    void update();

    void makeBet(User authUser, Long betAmount);

    RouletteStatsDto getStats();

    List<MatchHistoryItemDto> getMatchHistory();

}
