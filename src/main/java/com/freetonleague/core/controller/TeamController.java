package com.freetonleague.core.controller;

import com.freetonleague.core.config.ApiPageable;
import com.freetonleague.core.domain.dto.TeamBaseDto;
import com.freetonleague.core.domain.dto.TeamDto;
import com.freetonleague.core.domain.dto.TeamExtendedDto;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.RestTeamFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(path = TeamController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Participant Management Controller")
public class TeamController {

    private final RestTeamFacade restTeamFacade;

    public static final String BASE_PATH = "/api/team";
    public static final String PATH_REGISTER = "/register";
    public static final String PATH_GET = "/{team_id}";
    public static final String PATH_GET_LIST = "/list";
    public static final String PATH_EDIT = "/{id}";
    public static final String PATH_DISBAND = "/disband/{team_id}";
    public static final String PATH_EXPEL = "/{team_id}/expel/{participant_id}";
    public static final String PATH_LIST_MY = "/list/my";
    public static final String PATH_QUIT_ME = "/quit/{team_id}";

    @ApiOperation("Get team by id")
    @GetMapping(path = PATH_GET)
    public ResponseEntity<TeamExtendedDto> getTeamById(@PathVariable("team_id") long id,
                                                       @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTeamFacade.getTeamById(id, user), HttpStatus.OK);
    }

    @ApiOperation("Get team list info")
    @ApiPageable
    @GetMapping(path = PATH_GET_LIST)
    public ResponseEntity<Page<TeamExtendedDto>> getTeamList(@PageableDefault Pageable pageable,
                                                             @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTeamFacade.getTeamList(pageable, user), HttpStatus.OK);
    }

    @ApiOperation("Get all team with my participation")
    @ApiPageable
    @GetMapping(path = PATH_LIST_MY)
    public ResponseEntity<Page<TeamExtendedDto>> getMyTeamList(@PageableDefault Pageable pageable,
                                                               @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTeamFacade.getUserTeamList(pageable, user), HttpStatus.OK);
    }

    @ApiOperation("Register new team on platform")
    @PostMapping(path = PATH_REGISTER)
    public ResponseEntity<TeamDto> registerTeam(@RequestBody TeamBaseDto teamDto,
                                                @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTeamFacade.addTeam(teamDto, user), HttpStatus.CREATED);
    }

    @ApiOperation("Edit team info (only for captain)")
    @PutMapping(path = PATH_EDIT)
    public ResponseEntity<TeamExtendedDto> editTeam(@PathVariable("id") long id, @RequestBody TeamBaseDto teamDto,
                                                    @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTeamFacade.editTeam(id, teamDto, user), HttpStatus.OK);
    }

    @ApiOperation("Expel participant from team (only for captain)")
    @PutMapping(path = PATH_EXPEL)
    public ResponseEntity<TeamExtendedDto> expelFromTeam(@PathVariable("team_id") long id,
                                                         @PathVariable("participant_id") long participantId,
                                                         @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTeamFacade.expel(id, participantId, user), HttpStatus.OK);
    }

    @ApiOperation("Disband (archive) all the team (only for captain)")
    @DeleteMapping(path = PATH_DISBAND)
    public ResponseEntity<Void> disbandTeam(@PathVariable("team_id") long id,
                                            @ApiIgnore @AuthenticationPrincipal User user) {
        restTeamFacade.disband(id, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("Delete my participation from requested team id")
    @PutMapping(path = PATH_QUIT_ME)
    public ResponseEntity<Void> quitMeFromTeam(@PathVariable("team_id") long id,
                                               @ApiIgnore @AuthenticationPrincipal User user) {
        restTeamFacade.quitUserFromTeam(id, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
