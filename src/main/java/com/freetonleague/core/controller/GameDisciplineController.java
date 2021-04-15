package com.freetonleague.core.controller;

import com.freetonleague.core.domain.dto.GameDisciplineDto;
import com.freetonleague.core.domain.dto.GameDisciplineSettingsDto;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.RestGameDisciplineFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping(path = GameDisciplineController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Game Discipline Management Controller")
public class GameDisciplineController {

    public static final String BASE_PATH = "/api/game-discipline";
    public static final String PATH_CREATE = "/";
    public static final String PATH_GET = "/{discipline_id}";
    public static final String PATH_GET_LIST = "/list";
    public static final String PATH_CREATE_SETTINGS = "/settings/";
    public static final String PATH_GET_SETTINGS = "/settings/{discipline_id}";
    private final RestGameDisciplineFacade restFacade;

    @ApiOperation("Get game discipline by id")
    @GetMapping(path = PATH_GET)
    public ResponseEntity<GameDisciplineDto> getDisciplineById(@PathVariable("discipline_id") long id,
                                                               @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restFacade.getDiscipline(id, user), HttpStatus.OK);
    }

    @ApiOperation("Get discipline list info")
    @GetMapping(path = PATH_GET_LIST)
    public ResponseEntity<List<GameDisciplineDto>> getList(@ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restFacade.getAllDisciplines(user), HttpStatus.OK);
    }

    @ApiOperation("Create new game discipline on platform")
    @PostMapping(path = PATH_CREATE)
    public ResponseEntity<GameDisciplineDto> create(@RequestBody GameDisciplineDto disciplineDto,
                                                    @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restFacade.addDiscipline(disciplineDto, user), HttpStatus.CREATED);
    }

    @ApiOperation("Get game discipline settings by id")
    @GetMapping(path = PATH_GET_SETTINGS)
    public ResponseEntity<GameDisciplineSettingsDto> getSettingsByDisciplineId(@PathVariable("discipline_id") long id,
                                                                               @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restFacade.getPrimaryDisciplineSettingsByDiscipline(id, user), HttpStatus.OK);
    }

    @ApiOperation("Create new game discipline settings")
    @PostMapping(path = PATH_CREATE_SETTINGS)
    public ResponseEntity<GameDisciplineSettingsDto> createSettings(@RequestBody GameDisciplineSettingsDto disciplineSettingsDto,
                                                                    @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restFacade.addDisciplineSettings(disciplineSettingsDto, user), HttpStatus.CREATED);
    }


}
