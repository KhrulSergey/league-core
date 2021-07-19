package com.freetonleague.core.controller.api;

import com.freetonleague.core.domain.dto.MatchHistoryItemDto;
import com.freetonleague.core.domain.dto.RouletteStatsDto;
import com.freetonleague.core.domain.filter.RouletteBetFilter;
import com.freetonleague.core.domain.model.User;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

public interface RouletteApi {

    String GET_STATS_PATH = "/api/roulette/stats";
    String GET_HISTORY_PATH = "/api/roulette/stats/history";
    String POST_MAKE_BET_PATH = "/api/roulette/bet";

    @GetMapping(GET_STATS_PATH)
    @ApiOperation("Getting global roulette stats")
    RouletteStatsDto getRouletteStats();

    @GetMapping(GET_HISTORY_PATH)
    @ApiOperation("Getting roulette matches history")
    List<MatchHistoryItemDto> getMatchesHistory();

    @GetMapping(POST_MAKE_BET_PATH)
    @ApiOperation("Make bet by user")
    void makeBet(
            @RequestBody @Valid RouletteBetFilter filter,
            @ApiIgnore @AuthenticationPrincipal User user
    );

}
