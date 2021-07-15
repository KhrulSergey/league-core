package com.freetonleague.core.controller;


import com.freetonleague.core.domain.dto.tournament.TournamentSeriesDto;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.tournament.RestTournamentSeriesFacade;
import com.freetonleague.core.service.tournament.RestTournamentSeriesRivalFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
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
    public static final String PATH_GENERATE_OMT = "/generate-omt-for-series/{series_id}";
    public static final String PATH_GET = "/{series_id}";
    public static final String PATH_ADD = "/";
    public static final String PATH_EDIT = "/{series_id}";
    public static final String PATH_EDIT_BY_RIVALS = "/for-rivals/{series_id}";
    public static final String PATH_DELETE = "/{series_id}";
    public static final String PATH_DELETE_SERIES_RIVAL = "/rival/{rival_id}";

    private final RestTournamentSeriesFacade restTournamentSeriesFacade;
    private final RestTournamentSeriesRivalFacade restTournamentSeriesRivalFacade;

    @ApiOperation("Get Series by id")
    @GetMapping(path = PATH_GET)
    public ResponseEntity<TournamentSeriesDto> getSeriesById(@PathVariable("series_id") long id,
                                                             @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentSeriesFacade.getSeries(id, user), HttpStatus.OK);
    }

    @ApiOperation("Generate one more match (OMT) for series (only for orgs)")
    @PostMapping(path = PATH_GENERATE_OMT)
    public ResponseEntity<TournamentSeriesDto> generateOmtForSeries(@PathVariable("series_id") long id,
                                                                    @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentSeriesFacade.generateOmtForSeries(id, user), HttpStatus.CREATED);
    }

    @ApiOperation("Create new series with specified params (only for orgs)")
    @PostMapping(path = PATH_ADD)
    public ResponseEntity<TournamentSeriesDto> createSeries(@RequestBody TournamentSeriesDto tournamentSeriesDto,
                                                            @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentSeriesFacade.addSeries(tournamentSeriesDto, user), HttpStatus.CREATED);
    }

    @ApiOperation("Edit series info (only for orgs)")
    @PutMapping(path = PATH_EDIT)
    public ResponseEntity<TournamentSeriesDto> editSeries(@PathVariable("series_id") long id,
                                                          @RequestBody TournamentSeriesDto tournamentSeriesDto,
                                                          @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentSeriesFacade.editSeries(id, tournamentSeriesDto, user), HttpStatus.OK);
    }

    @ApiOperation("Edit series winners (only for tournament participants/rivals)")
    @PutMapping(path = PATH_EDIT_BY_RIVALS)
    public ResponseEntity<TournamentSeriesDto> editSeriesByRivals(@PathVariable("series_id") long id,
                                                                  @RequestBody TournamentSeriesDto tournamentSeriesDto,
                                                                  @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentSeriesFacade.editSeriesByRivals(id, tournamentSeriesDto, user), HttpStatus.OK);
    }

    @ApiOperation("Delete (archive) series (only for orgs)")
    @DeleteMapping(path = PATH_DELETE)
    public ResponseEntity<Void> deleteSeries(@PathVariable("series_id") long id,
                                             @ApiIgnore @AuthenticationPrincipal User user) {
        restTournamentSeriesFacade.deleteSeries(id, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("Delete series rivals (only for orgs)")
    @DeleteMapping(path = PATH_DELETE_SERIES_RIVAL)
    public ResponseEntity<Void> deleteSeriesRival(@PathVariable("rival_id") long seriesRivalId,
                                                  @ApiIgnore @AuthenticationPrincipal User user) {
        restTournamentSeriesRivalFacade.deleteSeriesRival(seriesRivalId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
