package com.freetonleague.core.controller;

import com.freetonleague.core.domain.dto.TournamentTeamProposalDto;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.RestTournamentTeamFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(path = TournamentTeamController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Tournament Proposals From Team Management Controller")
public class TournamentTeamController {

    public static final String BASE_PATH = "/api/tournament";
    public static final String BASE_POSTFIX_PATH = "/{tournament_id}/team/{team_id}";
    public static final String PATH_APPLY = "/apply";
    public static final String PATH_QUIT = "/quit";

    private final RestTournamentTeamFacade restTournamentTeamFacade;

    @ApiOperation("Apply to participate in tournament by id")
    @PostMapping(path = "/{tournament_id}/team/{team_id}/apply")
    public ResponseEntity<TournamentTeamProposalDto> getTournamentById(@PathVariable("tournament_id") long tournamentId,
                                                                       @PathVariable("team_id") long teamId,
                                                                       @RequestBody(required = false) TournamentTeamProposalDto teamProposalDto,
                                                                       @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentTeamFacade.createProposalToTournament(tournamentId, teamId, teamProposalDto, user), HttpStatus.OK);
    }

    @ApiOperation("Apply to participate in tournament by id")
    @PostMapping(path = BASE_POSTFIX_PATH + PATH_QUIT)
    public ResponseEntity<Void> getTournamentById(@PathVariable("tournament_id") long tournamentId,
                                                  @PathVariable("team_id") long teamId,
                                                  @ApiIgnore @AuthenticationPrincipal User user) {
        restTournamentTeamFacade.quitFromTournament(tournamentId, teamId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
