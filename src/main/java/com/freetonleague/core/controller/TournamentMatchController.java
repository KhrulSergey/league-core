package com.freetonleague.core.controller;

import com.freetonleague.core.config.ApiPageable;
import com.freetonleague.core.domain.dto.TournamentMatchDto;
import com.freetonleague.core.domain.dto.TournamentMatchRivalDto;
import com.freetonleague.core.domain.dto.TournamentTeamParticipantDto;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.RestTournamentMatchFacade;
import com.freetonleague.core.service.RestTournamentMatchRivalFacade;
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

import java.util.Set;

@RestController
@RequestMapping(path = TournamentMatchController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Tournament Matches Management Controller")
public class TournamentMatchController {

    public static final String BASE_PATH = "/api/tournament/match";
    public static final String PATH_EDIT = "/{match_id}";
    public static final String PATH_ADD = "/";
    public static final String PATH_GET = "/{match_id}";
    public static final String PATH_DELETE = "/{match_id}";
    public static final String PATH_GET_LIST_BY_SERIES = "/list-by-series/{series_id}";

    public static final String PATH_RIVAL_PARTICIPANT_EDIT = "{match_id}/rival/{rival_id}/participants";
    public static final String PATH_RIVAL_DELETE = "/rival/{rival_id}";
    public static final String PATH_RIVAL_PARTICIPANT_DELETE = "/rival/participant/{rival_participant_id}";

    private final RestTournamentMatchRivalFacade restTournamentMatchRivalFacade;
    private final RestTournamentMatchFacade restTournamentMatchFacade;

    @ApiOperation("Get match by id")
    @GetMapping(path = PATH_GET)
    public ResponseEntity<TournamentMatchDto> getMatchById(@PathVariable("match_id") long id,
                                                           @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentMatchFacade.getMatch(id, user), HttpStatus.OK);
    }

    @ApiOperation("Create new match with specified params (only for orgs)")
    @PostMapping(path = PATH_ADD)
    public ResponseEntity<TournamentMatchDto> createMatch(@RequestBody TournamentMatchDto tournamentMatchDto,
                                                          @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentMatchFacade.addMatch(tournamentMatchDto, user), HttpStatus.CREATED);
    }

    @ApiPageable
    @ApiOperation("Get matches list info by series")
    @GetMapping(path = PATH_GET_LIST_BY_SERIES)
    public ResponseEntity<Page<TournamentMatchDto>> getMatchList(@PageableDefault Pageable pageable,
                                                                 @PathVariable("series_id") long seriesId,
                                                                 @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentMatchFacade.getMatchList(pageable, seriesId, user), HttpStatus.OK);
    }

    @ApiOperation("Edit match info (only for orgs)")
    @PutMapping(path = PATH_EDIT)
    public ResponseEntity<TournamentMatchDto> editMatch(@PathVariable("match_id") long matchId,
                                                        @RequestBody TournamentMatchDto tournamentMatchDto,
                                                        @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentMatchFacade.editMatch(matchId, tournamentMatchDto, user), HttpStatus.OK);
    }

    @ApiOperation("Delete (archive) match (only for orgs)")
    @DeleteMapping(path = PATH_DELETE)
    public ResponseEntity<Void> deleteMatch(@PathVariable("match_id") long matchId,
                                            @ApiIgnore @AuthenticationPrincipal User user) {
        restTournamentMatchFacade.deleteMatch(matchId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("Edit rival participant composition for match (only for capitan and orgs)")
    @PutMapping(path = PATH_RIVAL_PARTICIPANT_EDIT)
    public ResponseEntity<TournamentMatchRivalDto> editMatchRivalComposition(@PathVariable("match_id") long matchId,
                                                                             @PathVariable("rival_id") long rivalId,
                                                                             @RequestBody Set<TournamentTeamParticipantDto> rivalParticipantList,
                                                                             @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentMatchRivalFacade.changeActiveMatchRivalParticipants(matchId, rivalId, rivalParticipantList, user), HttpStatus.OK);
    }

    @ApiOperation("Delete match rival (only for orgs)")
    @DeleteMapping(path = PATH_RIVAL_DELETE)
    public ResponseEntity<Void> deleteMatchRival(@PathVariable("rival_id") long matchRivalId,
                                                 @ApiIgnore @AuthenticationPrincipal User user) {
        restTournamentMatchRivalFacade.deleteMatchRival(matchRivalId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("Delete match rival participant (only for orgs)")
    @DeleteMapping(path = PATH_RIVAL_PARTICIPANT_DELETE)
    public ResponseEntity<Void> deleteMatchRivalParticipant(@PathVariable("rival_participant_id") long matchRivalParticipantId,
                                                            @ApiIgnore @AuthenticationPrincipal User user) {
        restTournamentMatchRivalFacade.deleteMatchRivalParticipant(matchRivalParticipantId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
