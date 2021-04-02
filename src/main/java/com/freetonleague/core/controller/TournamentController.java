package com.freetonleague.core.controller;

import com.freetonleague.core.domain.dto.TournamentDto;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.RestTournamentFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping(path = TournamentController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Tournament Management Controller")
public class TournamentController {

    public static final String BASE_PATH = "/api/tournament";
    public static final String PATH_CREATE = "/";
    public static final String PATH_MODIFY = "/";
    public static final String PATH_GET = "/{tournament_id}";
    public static final String PATH_GET_LIST = "/list";
    private final RestTournamentFacade restTournamentFacade;

    @ApiOperation("Get tournament by id")
    @GetMapping(path = PATH_GET)
    public ResponseEntity<TournamentDto> getTournamentById(@PathVariable("tournament_id") long id,
                                                           @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentFacade.getTournament(id, user), HttpStatus.OK);
    }

    @ApiOperation("Get tournament list info")
    @GetMapping(path = PATH_GET_LIST)
    public ResponseEntity<List<TournamentDto>> geTournamentList(@PageableDefault Pageable pageable,
                                                                @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentFacade.getTournamentList(pageable, user), HttpStatus.OK);
    }

    @ApiOperation("Create new tournament on platform")
    @PostMapping(path = PATH_CREATE)
    public ResponseEntity<TournamentDto> createTournament(@RequestBody TournamentDto tournamentDto,
                                                          @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentFacade.addTournament(tournamentDto, user), HttpStatus.OK);
    }

    @ApiOperation("Modify tournament on platform")
    @PutMapping(path = PATH_MODIFY)
    public ResponseEntity<TournamentDto> modifyTournament(@RequestBody TournamentDto tournamentDto,
                                                          @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentFacade.editTournament(tournamentDto, user), HttpStatus.OK);
    }

}
