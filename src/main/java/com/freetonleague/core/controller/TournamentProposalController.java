package com.freetonleague.core.controller;

import com.freetonleague.core.config.ApiPageable;
import com.freetonleague.core.domain.dto.TournamentTeamProposalDto;
import com.freetonleague.core.domain.enums.ParticipationStateType;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.RestTournamentProposalFacade;
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

import java.util.List;

import static java.util.Objects.nonNull;

@RestController
@RequestMapping(path = TournamentProposalController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Tournament Activity From Team Management Controller")
public class TournamentProposalController {

    public static final String BASE_PATH = "/api/tournament";
    public static final String BASE_PROPOSALS_POSTFIX_PATH = "/proposal";
    public static final String PATH_APPLY_TO_TOURNAMENT = "/apply";
    public static final String PATH_CHECK_IN_TO_TOURNAMENT = "/check-in";
    public static final String PATH_APPLY_TO_TOURNAMENT_BY_USER = "/apply-by-user";
    public static final String PATH_QUIT_FROM_TOURNAMENT = "/quit";
    public static final String PATH_GET_FOR_TOURNAMENT = "/";
    public static final String PATH_GET_FOR_TOURNAMENT_BY_USER = "/by-user";
    public static final String PATH_EDIT_TEAM_PROPOSAL = "/";
    public static final String PATH_GET_LIST_FOR_TOURNAMENT = "/list";

    private final RestTournamentProposalFacade restTournamentProposalFacade;


    @ApiOperation("Get team proposal for tournament by Team")
    @GetMapping(path = BASE_PROPOSALS_POSTFIX_PATH + PATH_GET_FOR_TOURNAMENT)
    public ResponseEntity<TournamentTeamProposalDto> getTournamentProposalByTeamId(@RequestParam(value = "tournament_id") long tournamentId,
                                                                                   @RequestParam(value = "team_id") long teamId,
                                                                                   @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentProposalFacade.getProposalFromTeamForTournament(tournamentId, teamId, user), HttpStatus.OK);
    }

    @ApiOperation("Get proposal for tournament by user (virtual team)")
    @GetMapping(path = BASE_PROPOSALS_POSTFIX_PATH + PATH_GET_FOR_TOURNAMENT_BY_USER)
    public ResponseEntity<TournamentTeamProposalDto> getTournamentProposalByUser(@RequestParam(value = "tournament_id") long tournamentId,
                                                                                 @RequestParam(value = "league_id") String leagueId,
                                                                                 @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentProposalFacade.getProposalFromUserForTournament(tournamentId, leagueId, user), HttpStatus.OK);
    }


    @ApiOperation("Apply to participate in tournament from team")
    @PostMapping(path = BASE_PROPOSALS_POSTFIX_PATH + PATH_APPLY_TO_TOURNAMENT)
    public ResponseEntity<TournamentTeamProposalDto> applyToTournamentFromTeam(@RequestParam(value = "tournament_id") long tournamentId,
                                                                               @RequestParam(value = "team_id") long teamId,
                                                                               @ApiIgnore @RequestBody(required = false) TournamentTeamProposalDto teamProposalDto,
                                                                               @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentProposalFacade.createProposalToTournament(tournamentId, teamId, teamProposalDto, user), HttpStatus.OK);
    }

    @ApiOperation("Apply to participate in tournament from user (only for lobby tournament)")
    @PostMapping(path = BASE_PROPOSALS_POSTFIX_PATH + PATH_APPLY_TO_TOURNAMENT_BY_USER)
    public ResponseEntity<TournamentTeamProposalDto> applyToTournamentFromUser(@RequestParam(value = "tournament_id") long tournamentId,
                                                                               @RequestParam(value = "league_id") String leagueId,
                                                                               @ApiIgnore @RequestBody(required = false) TournamentTeamProposalDto teamProposalDto,
                                                                               @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentProposalFacade.createProposalToTournamentFromUser(tournamentId, leagueId, user), HttpStatus.OK);
    }

    @ApiOperation("Check-in participation in tournament")
    @PostMapping(path = BASE_PROPOSALS_POSTFIX_PATH + PATH_CHECK_IN_TO_TOURNAMENT)
    public ResponseEntity<TournamentTeamProposalDto> checkInToTournamentFromTeam(@RequestParam(value = "tournament_id") long tournamentId,
                                                                                 @RequestParam(value = "team_poposal_id", required = false) Long teamProposalId,
                                                                                 @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentProposalFacade.checkInParticipationToTournament(tournamentId, teamProposalId, user), HttpStatus.OK);
    }

    @ApiOperation("Change team proposal to tournament by teamProposalId or by tournamentId + teamId (available edit only state, for orgs)")
    @PutMapping(path = BASE_PROPOSALS_POSTFIX_PATH + PATH_EDIT_TEAM_PROPOSAL)
    public ResponseEntity<TournamentTeamProposalDto> editTeamProposal(@RequestParam(value = "tournament_id", required = false) Long tournamentId,
                                                                      @RequestParam(value = "team_id", required = false) Long teamId,
                                                                      @RequestParam(value = "team_poposal_id", required = false) Long teamProposalId,
                                                                      @RequestParam(value = "team_poposal_state") ParticipationStateType teamProposalState,
                                                                      @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentProposalFacade.editProposalToTournament(tournamentId, teamId, teamProposalId, teamProposalState, user), HttpStatus.OK);
    }

    @ApiOperation("Quit team from tournament by tournament and team id")
    @PostMapping(path = BASE_PROPOSALS_POSTFIX_PATH + PATH_QUIT_FROM_TOURNAMENT)
    public ResponseEntity<TournamentTeamProposalDto> quitFromTournamentById(@RequestParam(value = "tournament_id") long tournamentId,
                                                                            @RequestParam(value = "team_poposal_id") long teamProposalId,
                                                                            @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentProposalFacade.quitFromTournament(tournamentId, teamProposalId, user), HttpStatus.OK);
    }

    @ApiPageable
    @ApiOperation("Get team proposal list for tournament (for orgs and team - extended view)")
    @GetMapping(path = BASE_PROPOSALS_POSTFIX_PATH + PATH_GET_LIST_FOR_TOURNAMENT)
    public ResponseEntity<Page<TournamentTeamProposalDto>> getTournamentProposalList(
            @PageableDefault Pageable pageable,
            @RequestParam(value = "tournament_id") long tournamentId,
            @RequestParam(value = "check_in", required = false) Boolean confirmed,
            @RequestParam(value = "states", required = false) ParticipationStateType[] states) {
        List<ParticipationStateType> stateList = nonNull(states) ? List.of(states) : null;
        return new ResponseEntity<>(restTournamentProposalFacade.getProposalListForTournament(pageable, tournamentId, confirmed, stateList), HttpStatus.OK);
    }
}
