package com.freetonleague.core.controller;

import com.freetonleague.core.domain.dto.TeamDetailedInviteListDto;
import com.freetonleague.core.domain.dto.TeamDto;
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
    public static final String PATH_INVITE_GET_MY_LIST = "/invite/list-by-user/";
    public static final String PATH_INVITE_CREATE = "/invite/to-team/{team_id}";
    public static final String PATH_INVITE_INFO = "/invite/info/{invite_token}";
    public static final String PATH_INVITE_APPLY = "/invite/apply/{invite_token}";
    public static final String PATH_INVITE_CANCEL = "/invite/cancel/{invite_token}";
    public static final String PATH_INVITE_REJECT = "/invite/reject/{invite_token}";
    private final RestTeamParticipantFacade teamParticipantFacade;

    @ApiOperation("Get list of invitation for a team (only for captain)")
    @GetMapping(path = PATH_INVITE_GET_LIST)
    public ResponseEntity<List<TeamInviteRequestDto>> getListOfInvitation(@PathVariable("team_id") long teamId,
                                                                          @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(teamParticipantFacade.getInviteList(teamId, user), HttpStatus.OK);
    }

    @ApiOperation("Get list of invitation for a current user")
    @GetMapping(path = PATH_INVITE_GET_MY_LIST)
    public ResponseEntity<List<TeamDetailedInviteListDto>> getListOfMyInvitation(@ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(teamParticipantFacade.getMyInviteList(user), HttpStatus.OK);
    }

    @ApiOperation("Create new invitation to a team")
    @PostMapping(path = PATH_INVITE_CREATE)
    public ResponseEntity<TeamInviteRequestDto> createInvite(@PathVariable("team_id") long teamId,
                                                             @RequestParam(value = "username", required = false) String username,
                                                             @RequestParam(value = "leagueId", required = false) String leagueId,
                                                             @ApiIgnore @AuthenticationPrincipal User currentUser) {
        return new ResponseEntity<>(teamParticipantFacade.createInvite(teamId, currentUser, username, leagueId), HttpStatus.OK);
    }

    @ApiOperation("Get info about team by invitation")
    @PostMapping(path = PATH_INVITE_INFO)
    public ResponseEntity<TeamDto> getInviteInfo(@PathVariable("invite_token") String inviteToken,
                                                 @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(teamParticipantFacade.getInviteRequestInfo(inviteToken, user), HttpStatus.OK);
    }

    @ApiOperation("Apply for invitation to a team and become new participant")
    @PostMapping(path = PATH_INVITE_APPLY)
    public ResponseEntity<TeamParticipantDto> applyInvite(@PathVariable("invite_token") String inviteToken,
                                                          @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(teamParticipantFacade.applyInviteRequest(inviteToken, user), HttpStatus.OK);
    }

    @ApiOperation("Delete invitation to a team")
    @DeleteMapping(path = PATH_INVITE_CANCEL)
    public ResponseEntity<Void> cancelInvite(@PathVariable("invite_token") String inviteToken,
                                             @ApiIgnore @AuthenticationPrincipal User user) {
        teamParticipantFacade.cancelInviteRequest(inviteToken, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("Reject invitation to a team by user")
    @DeleteMapping(path = PATH_INVITE_REJECT)
    public ResponseEntity<Void> rejectInvite(@PathVariable("invite_token") String inviteToken,
                                             @ApiIgnore @AuthenticationPrincipal User user) {
        teamParticipantFacade.rejectInviteRequest(inviteToken, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
