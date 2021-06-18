package com.freetonleague.core.controller;


import com.freetonleague.core.config.ApiPageable;
import com.freetonleague.core.domain.dto.TournamentRoundDto;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.RestTournamentRoundFacade;
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
@RequestMapping(path = TournamentRoundController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Tournament Round Management Controller")
public class TournamentRoundController {

    public static final String BASE_PATH = "/api/tournament/round";
    public static final String PATH_GENERATE = "/generate-for-tournament/{tournament_id}";
    public static final String PATH_GET = "/{round_id}";
    public static final String PATH_GET_LIST_BY_TOURNAMENT = "/list-by-tournament/{tournament_id}";
    public static final String PATH_ADD = "/";
    public static final String PATH_EDIT = "/{round_id}";
    public static final String PATH_DELETE = "/{round_id}";

    private final RestTournamentRoundFacade restTournamentRoundFacade;

    @ApiOperation("Get Round by id")
    @GetMapping(path = PATH_GET)
    public ResponseEntity<TournamentRoundDto> getRoundById(@PathVariable("round_id") long id,
                                                           @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentRoundFacade.getRound(id, user), HttpStatus.OK);
    }

    @ApiPageable
    @ApiOperation("Get round list info by tournament")
    @GetMapping(path = PATH_GET_LIST_BY_TOURNAMENT)
    public ResponseEntity<Page<TournamentRoundDto>> getRoundList(@PageableDefault Pageable pageable,
                                                                 @PathVariable("tournament_id") long tournamentId,
                                                                 @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentRoundFacade.getRoundList(pageable, tournamentId, user), HttpStatus.OK);
    }

    @ApiOperation("Generate rounds (brackets) for specified tournament (only for orgs)")
    @PostMapping(path = PATH_GENERATE)
    public ResponseEntity<Void> generateRounds(@PathVariable("tournament_id") long tournamentId,
                                               @ApiIgnore @AuthenticationPrincipal User user) {
        restTournamentRoundFacade.generateRoundsForTournament(tournamentId, user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation("Create new round with specified params (only for orgs)")
    @PostMapping(path = PATH_ADD)
    public ResponseEntity<TournamentRoundDto> createRound(@RequestBody TournamentRoundDto tournamentRoundDto,
                                                          @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentRoundFacade.addRound(tournamentRoundDto, user), HttpStatus.CREATED);
    }

    @ApiOperation("Edit round info (only for orgs)")
    @PutMapping(path = PATH_EDIT)
    public ResponseEntity<TournamentRoundDto> editRound(@PathVariable("round_id") long id,
                                                        @RequestBody TournamentRoundDto tournamentRoundDto,
                                                        @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentRoundFacade.editRound(id, tournamentRoundDto, user), HttpStatus.OK);
    }

    @ApiOperation("Delete (archive) round (only for orgs)")
    @DeleteMapping(path = PATH_DELETE)
    public ResponseEntity<Void> deleteRound(@PathVariable("round_id") long id,
                                            @ApiIgnore @AuthenticationPrincipal User user) {
        restTournamentRoundFacade.deleteRound(id, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
