package com.freetonleague.core.controller.api;

import com.freetonleague.core.config.ApiPageable;
import com.freetonleague.core.domain.dto.RouletteMatchHistoryItemDto;
import com.freetonleague.core.domain.dto.RouletteMatchStatsDto;
import com.freetonleague.core.domain.dto.RouletteStatsDto;
import com.freetonleague.core.domain.filter.RouletteBetFilter;
import com.freetonleague.core.domain.model.User;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

public interface RouletteApi {

    String GET_STATS_PATH = "/api/roulette/stats";
    String GET_HISTORY_PATH = "/api/roulette/stats/history";
    String POST_MAKE_BET_PATH = "/api/roulette/bet";
    String GET_MATCH_STATS_BY_ID_PATH = "/api/roulette/match-stats/{matchId}";

    @GetMapping(GET_STATS_PATH)
    @ApiOperation("Getting global roulette stats")
    RouletteStatsDto getRouletteStats();

    @GetMapping(GET_MATCH_STATS_BY_ID_PATH)
    RouletteMatchStatsDto getMatchStatsById(
            @PathVariable Long matchId
    );

    @ApiPageable
    @GetMapping(GET_HISTORY_PATH)
    @ApiOperation("Getting roulette matches history")
    Page<RouletteMatchHistoryItemDto> getMatchesHistory(
            @PageableDefault Pageable pageable
    );

    @PostMapping(POST_MAKE_BET_PATH)
    @ApiOperation("Make bet by user")
    RouletteStatsDto makeBet(
            @RequestBody @Valid RouletteBetFilter filter,
            @ApiIgnore @AuthenticationPrincipal User user
    );

}
