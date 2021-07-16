package com.freetonleague.core.controller;

import com.freetonleague.core.controller.api.RouletteApi;
import com.freetonleague.core.domain.dto.RouletteMatchHistoryItemDto;
import com.freetonleague.core.domain.dto.RouletteStatsDto;
import com.freetonleague.core.domain.filter.RouletteBetFilter;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.RouletteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RouletteController implements RouletteApi {

    private final RouletteService rouletteService;

    @Override
    public RouletteStatsDto getRouletteStats() {
        return rouletteService.getStats();
    }

    @Override
    public List<RouletteMatchHistoryItemDto> getMatchesHistory() {
        return rouletteService.getMatchHistory();
    }

    @Override
    public void makeBet(RouletteBetFilter filter, User user) {
        rouletteService.makeBet(user, filter.getBetAmount());
    }

}
