package com.freetonleague.core.controller;

import com.freetonleague.core.domain.dto.TeamInviteRequestDto;
import com.freetonleague.core.domain.dto.TeamParticipantDto;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.RestTeamParticipantFacade;
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
@RequestMapping(path = TeamParticipantController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Team participant management controller")
public class TeamParticipantController {

    public static final String BASE_PATH = "/api/team/participant";
    public static final String PATH_INVITE_GET_LIST = "/invite/list-by-team/{team_id}";
    public static final String PATH_INVITE_CREATE = "/invite/add-for-team/{team_id}";
    public static final String PATH_INVITE_APPLY = "/invite/apply/{invite_token}";
    public static final String PATH_INVITE_DELETE = "/invite/{invite_token}";
    private final RestTeamParticipantFacade teamParticipantFacade;

    @ApiOperation("Get list of invitation for a team (only for captain)")
    @GetMapping(path = PATH_INVITE_GET_LIST)
    public ResponseEntity<List<TeamInviteRequestDto>> getListOfInvitation(@PathVariable("team_id") long teamId,
                                                                          @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(teamParticipantFacade.getInviteList(teamId, user), HttpStatus.OK);
    }

    @ApiOperation("Create new invitation to a team")
    @PostMapping(path = PATH_INVITE_CREATE)
    public ResponseEntity<TeamInviteRequestDto> createInvite(@PathVariable("team_id") long teamId,
                                                             @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(teamParticipantFacade.createInvite(teamId, user), HttpStatus.OK);
    }

    @ApiOperation("Apply for invitation to a team and become new participant")
    @PostMapping(path = PATH_INVITE_APPLY)
    public ResponseEntity<TeamParticipantDto> createInvite(@PathVariable("invite_token") String inviteToken,
                                                           @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(teamParticipantFacade.applyInviteRequest(inviteToken, user), HttpStatus.OK);
    }

    @ApiOperation("Delete invitation to a team")
    @DeleteMapping(path = PATH_INVITE_DELETE)
    public ResponseEntity<Void> deleteInvite(@PathVariable("invite_token") String inviteToken,
                                             @ApiIgnore @AuthenticationPrincipal User user) {
        teamParticipantFacade.deleteInviteRequest(inviteToken, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
