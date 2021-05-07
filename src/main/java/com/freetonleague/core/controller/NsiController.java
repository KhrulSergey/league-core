package com.freetonleague.core.controller;

import com.freetonleague.core.domain.dto.GameIndicatorTypeDto;
import com.freetonleague.core.domain.dto.MatchPropertyTypeDto;
import com.freetonleague.core.service.RestNsiFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = NsiController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Game Discipline Management Controller")
public class NsiController {

    public static final String BASE_PATH = "/api/nsi";
    public static final String PATH_GAME_INDICATOR_LIST = "/game-indicator-list";
    public static final String PATH_MATCH_PROPERTY_LIST = "/match-property-list";
    private final RestNsiFacade restNsiFacade;

    @ApiOperation("Get game indicator list with names and values")
    @GetMapping(path = PATH_GAME_INDICATOR_LIST)
    public ResponseEntity<List<GameIndicatorTypeDto>> getGameIndicatorList() {
        return new ResponseEntity<>(restNsiFacade.getGameIndicatorList(), HttpStatus.OK);
    }

    @ApiOperation("Get match property list with names and values")
    @GetMapping(path = PATH_MATCH_PROPERTY_LIST)
    public ResponseEntity<List<MatchPropertyTypeDto>> getMatchPropertyList() {
        return new ResponseEntity<>(restNsiFacade.getMatchPropertyList(), HttpStatus.OK);
    }
}
