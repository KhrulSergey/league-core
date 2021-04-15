package com.freetonleague.core.controller;


import com.freetonleague.core.domain.dto.TournamentSeriesDto;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.RestTournamentSeriesService;
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
@RequestMapping(path = TournamentSeriesController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Tournament Series Management Controller")
public class TournamentSeriesController {

    public static final String BASE_PATH = "/api/tournament/series";
    public static final String PATH_GENERATE = "/generate-for-tournament/{tournament_id}";
    public static final String PATH_GET = "/{series_id}";
    public static final String PATH_GET_LIST_BY_TOURNAMENT = "/list-by-tournament/{tournament_id}";
    public static final String PATH_ADD = "/";
    public static final String PATH_EDIT = "/{series_id}";
    public static final String PATH_DELETE = "/{series_id}";

    private final RestTournamentSeriesService restTournamentSeriesService;

    @ApiOperation("Get Series by id")
    @GetMapping(path = PATH_GET)
    public ResponseEntity<TournamentSeriesDto> getSeriesById(@PathVariable("series_id") long id,
                                                             @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentSeriesService.getSeries(id, user), HttpStatus.OK);
    }

    @ApiOperation("Get series list info by tournament")
    @GetMapping(path = PATH_GET_LIST_BY_TOURNAMENT)
    public ResponseEntity<Page<TournamentSeriesDto>> getSeriesList(@PageableDefault Pageable pageable,
                                                                   @PathVariable("tournament_id") long tournamentId,
                                                                   @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentSeriesService.getSeriesList(pageable, tournamentId, user), HttpStatus.OK);
    }

    @ApiOperation("Generate tournament next (active) series for tournament (only for orgs)")
    @PostMapping(path = PATH_GENERATE)
    public ResponseEntity<Void> generateSeries(@PathVariable("tournament_id") long tournamentId,
                                               @ApiIgnore @AuthenticationPrincipal User user) {
        restTournamentSeriesService.generateSeriesForTournament(tournamentId, user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation("Create new series with specified params (only for orgs)")
    @PostMapping(path = PATH_ADD)
    public ResponseEntity<TournamentSeriesDto> createSeries(@RequestBody TournamentSeriesDto tournamentSeriesDto,
                                                            @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentSeriesService.addSeries(tournamentSeriesDto, user), HttpStatus.CREATED);
    }

    @ApiOperation("Edit series info (only for orgs)")
    @PutMapping(path = PATH_EDIT)
    public ResponseEntity<TournamentSeriesDto> editSeries(@PathVariable("series_id") long id,
                                                          @RequestBody TournamentSeriesDto tournamentSeriesDto,
                                                          @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentSeriesService.editSeries(id, tournamentSeriesDto, user), HttpStatus.OK);
    }

    @ApiOperation("Delete (archive) series (only for orgs)")
    @DeleteMapping(path = PATH_DELETE)
    public ResponseEntity<Void> deleteSeries(@PathVariable("series_id") long id,
                                             @ApiIgnore @AuthenticationPrincipal User user) {
        restTournamentSeriesService.deleteSeries(id, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
