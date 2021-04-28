package com.freetonleague.core.controller;

import com.freetonleague.core.config.ApiPageable;
import com.freetonleague.core.domain.dto.TournamentTeamProposalBaseDto;
import com.freetonleague.core.domain.dto.TournamentTeamProposalDto;
import com.freetonleague.core.domain.enums.TournamentTeamStateType;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.RestTournamentTeamFacade;
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
@RequestMapping(path = TournamentTeamController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Tournament Activity From Team Management Controller")
public class TournamentTeamController {

    public static final String BASE_PATH = "/api/tournament";
    public static final String BASE_PROPOSALS_POSTFIX_PATH = "/proposal";
    public static final String PATH_APPLY_TO_TOURNAMENT = "/apply";
    public static final String PATH_QUIT_FROM_TOURNAMENT = "/quit";
    public static final String PATH_GET_FOR_TOURNAMENT = "/";
    public static final String PATH_EDIT_TEAM_PROPOSAL = "/";
    public static final String PATH_GET_LIST_FOR_TOURNAMENT = "/list";

    private final RestTournamentTeamFacade restTournamentTeamFacade;

    @ApiOperation("Apply to participate in tournament by id")
    @PostMapping(path = BASE_PROPOSALS_POSTFIX_PATH + PATH_APPLY_TO_TOURNAMENT)
    public ResponseEntity<TournamentTeamProposalDto> applyToTournamentById(@RequestParam(value = "tournament_id", required = true) long tournamentId,
                                                                           @RequestParam(value = "team_id", required = true) long teamId,
                                                                           @ApiIgnore @RequestBody(required = false) TournamentTeamProposalDto teamProposalDto,
                                                                           @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentTeamFacade.createProposalToTournament(tournamentId, teamId, teamProposalDto, user), HttpStatus.OK);
    }

    @ApiOperation("Change team proposal to tournament by teamProposalId or by tournamentId + teamId (available edit only state, for orgs)")
    @PutMapping(path = BASE_PROPOSALS_POSTFIX_PATH + PATH_EDIT_TEAM_PROPOSAL)
    public ResponseEntity<TournamentTeamProposalDto> editTeamProposal(@RequestParam(value = "tournament_id", required = false) Long tournamentId,
                                                                      @RequestParam(value = "team_id", required = false) Long teamId,
                                                                      @RequestParam(value = "team_poposal_id", required = false) Long teamProposalId,
                                                                      @RequestParam(value = "team_poposal_state", required = true) TournamentTeamStateType teamProposalState,
                                                                      @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentTeamFacade.editProposalToTournament(tournamentId, teamId, teamProposalId, teamProposalState, user), HttpStatus.OK);
    }

    @ApiOperation("Quit team from tournament by tournament and team id")
    @PostMapping(path = BASE_PROPOSALS_POSTFIX_PATH + PATH_QUIT_FROM_TOURNAMENT)
    public ResponseEntity<Void> quitFromTournamentById(@RequestParam(value = "tournament_id", required = true) long tournamentId,
                                                       @RequestParam(value = "team_id", required = true) long teamId,
                                                       @ApiIgnore @AuthenticationPrincipal User user) {
        restTournamentTeamFacade.quitFromTournament(tournamentId, teamId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("Get team proposal for tournament by Team (for orgs and team - extended view)")
    @GetMapping(path = BASE_PROPOSALS_POSTFIX_PATH + PATH_GET_FOR_TOURNAMENT)
    public ResponseEntity<TournamentTeamProposalDto> getTournamentProposalByTeamId(@RequestParam(value = "tournament_id", required = true) long tournamentId,
                                                                                   @RequestParam(value = "team_id", required = true) long teamId,
                                                                                   @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentTeamFacade.getProposalFromTeamForTournament(tournamentId, teamId, user), HttpStatus.OK);
    }

    @ApiPageable
    @ApiOperation("Get team proposal list for tournament (for orgs and team - extended view)")
    @GetMapping(path = BASE_PROPOSALS_POSTFIX_PATH + PATH_GET_LIST_FOR_TOURNAMENT)
    public ResponseEntity<Page<TournamentTeamProposalBaseDto>> getTournamentProposalList(@PageableDefault Pageable pageable,
                                                                                         @RequestParam(value = "tournament_id", required = true) long tournamentId,
                                                                                         @ApiIgnore @AuthenticationPrincipal User user) {

        return new ResponseEntity<>(restTournamentTeamFacade.getProposalListForTournament(pageable, tournamentId, user), HttpStatus.OK);
    }
}
