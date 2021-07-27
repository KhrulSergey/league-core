package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.RouletteMatchHistoryItemDto;
import com.freetonleague.core.domain.dto.RouletteMatchStatsDto;
import com.freetonleague.core.domain.dto.RouletteStatsDto;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RouletteService {

    void update();

    RouletteStatsDto makeBet(User authUser, Long betAmount);

    RouletteStatsDto getStats();

    Page<RouletteMatchHistoryItemDto> getMatchHistory(Pageable pageable);

    RouletteMatchStatsDto getMatchStatsById(Long matchId);
}
