package com.freetonleague.core.controller;

import com.freetonleague.core.domain.dto.TournamentMatchDto;
import com.freetonleague.core.domain.dto.TournamentMatchRivalParticipantDto;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.RestTournamentMatchRivalService;
import com.freetonleague.core.service.RestTournamentMatchService;
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

@RestController
@RequestMapping(path = TournamentMatchController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Tournament Matches Management Controller")
public class TournamentMatchController {

    public static final String BASE_PATH = "/api/tournament/match";
    public static final String PATH_EDIT = "/{match_id}";
    public static final String PATH_GET = "/{match_id}";
    public static final String PATH_DELETE = "/{match_id}";
    public static final String PATH_GET_LIST_BY_SERIES = "/list-by-series/{series_id}";
    public static final String PATH_RIVAL_PARTICIPANT_EDIT = "{match_id}/rival/{rival_id}/participants";


    private final RestTournamentMatchRivalService restTournamentMatchRivalService;
    private final RestTournamentMatchService restTournamentMatchService;

    @ApiOperation("Get match by id")
    @GetMapping(path = PATH_GET)
    public ResponseEntity<TournamentMatchDto> getMatchById(@PathVariable("match_id") long id,
                                                           @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentMatchService.getMatch(id, user), HttpStatus.OK);
    }

    @ApiOperation("Get matches list info by series")
    @GetMapping(path = PATH_GET_LIST_BY_SERIES)
    public ResponseEntity<Page<TournamentMatchDto>> getMatchList(@PageableDefault Pageable pageable,
                                                                 @PathVariable("series_id") long seriesId,
                                                                 @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentMatchService.getMatchList(pageable, seriesId, user), HttpStatus.OK);
    }

    @ApiOperation("Edit match info")
    @PutMapping(path = PATH_EDIT)
    public ResponseEntity<TournamentMatchDto> editMatch(@PathVariable("match_id") long matchId,
                                                        @RequestBody TournamentMatchDto tournamentMatchDto,
                                                        @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentMatchService.editMatch(matchId, tournamentMatchDto, user), HttpStatus.OK);
    }

    @ApiOperation("Delete (archive) match")
    @DeleteMapping(path = PATH_DELETE)
    public ResponseEntity<Void> deleteMatch(@PathVariable("match_id") long matchId,
                                            @ApiIgnore @AuthenticationPrincipal User user) {
        restTournamentMatchService.deleteMatch(matchId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("Edit rival participant composition for match (only for capitan and orgs)")
    @PutMapping(path = PATH_RIVAL_PARTICIPANT_EDIT)
    public ResponseEntity<TournamentMatchDto> editMatchRivalComposition(@PathVariable("match_id") long matchId,
                                                                        @PathVariable("rival_id") long rivalId,
                                                                        @RequestBody List<TournamentMatchRivalParticipantDto> rivalParticipantList,
                                                                        @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentMatchService.editMatchRivalParticipant(matchId, rivalId, rivalParticipantList, user), HttpStatus.OK);
    }
}
