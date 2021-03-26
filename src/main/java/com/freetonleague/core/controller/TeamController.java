package com.freetonleague.core.controller;

import com.freetonleague.core.domain.dto.TeamBaseDto;
import com.freetonleague.core.domain.dto.TeamDto;
import com.freetonleague.core.domain.dto.TeamExtendedDto;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.RestTeamFacade;
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
@RequestMapping(path = TeamController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Participant Management Controller")
public class TeamController {

    private final RestTeamFacade restTeamFacade;

    public static final String BASE_PATH = "/api/team";
    public static final String PATH_REGISTER = "/register";
    public static final String PATH_GET = "/{id}";
    public static final String PATH_EDIT = "/{id}";
    public static final String PATH_DISBAND = "/disband/{id}";
    public static final String PATH_EXPEL = "/{id}/expel/{participant_id}";
    public static final String PATH_LIST_MY = "/list/my";
    public static final String PATH_QUIT_ME = "/quit/{id}";

    @ApiOperation("Get team by id")
    @GetMapping(path = PATH_GET)
    public ResponseEntity<TeamDto> getById(@PathVariable("id") long id) {
        return new ResponseEntity<>(restTeamFacade.getByUd(id), HttpStatus.OK);
    }

    @ApiOperation("Register new team on platform")
    @PostMapping(path = PATH_REGISTER)
    public ResponseEntity<TeamDto> register(@RequestBody TeamDto teamDto,
                                            @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTeamFacade.add(teamDto, user), HttpStatus.OK);
    }

    @ApiOperation("Edit team info")
    @PutMapping(path = PATH_EDIT)
    public ResponseEntity<TeamExtendedDto> edit(@PathVariable("id") long id, @RequestBody TeamBaseDto teamDto,
                             @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTeamFacade.edit(id, teamDto, user), HttpStatus.OK);
    }

    @ApiOperation("Expel participant from team")
    @PutMapping(path = PATH_EXPEL)
    public ResponseEntity<TeamExtendedDto> expel(@PathVariable("id") long id,
                                                 @PathVariable("participant_id") long participantId,
                                                 @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTeamFacade.expel(id, participantId, user), HttpStatus.OK);
    }

    @ApiOperation("Disband (archive) all the team")
    @DeleteMapping(path = PATH_DISBAND)
    public ResponseEntity<Void> disband(@PathVariable("id") long id,
                                                     @ApiIgnore @AuthenticationPrincipal User user) {
        restTeamFacade.disband(id, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("Get all team with my participation")
    @PutMapping(path = PATH_QUIT_ME)
    public ResponseEntity<Void> quitMeFromTeam(@PathVariable("id") long id,
                                               @ApiIgnore @AuthenticationPrincipal User user) {
        restTeamFacade.quitUserFromTeam(id, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("Get all team with my participation")
    @GetMapping(path = PATH_LIST_MY)
    public ResponseEntity<List<TeamExtendedDto>> getMyTeamList(@ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTeamFacade.getUserTeamList(user), HttpStatus.OK);
    }
}
