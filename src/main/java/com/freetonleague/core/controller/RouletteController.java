package com.freetonleague.core.controller;

import com.freetonleague.core.controller.api.RouletteApi;
import com.freetonleague.core.domain.dto.RouletteMatchHistoryItemDto;
import com.freetonleague.core.domain.dto.RouletteMatchStatsDto;
import com.freetonleague.core.domain.dto.RouletteStatsDto;
import com.freetonleague.core.domain.filter.RouletteBetFilter;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.RouletteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RouletteController implements RouletteApi {

    private final RouletteService rouletteService;

    @Override
    public RouletteStatsDto getRouletteStats() {
        return rouletteService.getStats();
    }

    @Override
    public RouletteMatchStatsDto getMatchStatsById(Long matchId) {
        return rouletteService.getMatchStatsById(matchId);
    }

    @Override
    public Page<RouletteMatchHistoryItemDto> getMatchesHistory(Pageable pageable) {
        return rouletteService.getMatchHistory(pageable);
    }

    @Override
    public RouletteStatsDto makeBet(RouletteBetFilter filter, User user) {
        return rouletteService.makeBet(user, filter.getBetAmount());
    }

}
