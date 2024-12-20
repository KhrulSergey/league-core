package com.freetonleague.core.controller;

import com.freetonleague.core.config.ApiPageable;
import com.freetonleague.core.domain.dto.tournament.TournamentDiscordInfoListDto;
import com.freetonleague.core.domain.dto.tournament.TournamentDto;
import com.freetonleague.core.domain.enums.tournament.TournamentStatusType;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.tournament.RestTournamentFacade;
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
@RequestMapping(path = TournamentController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Tournament Management Controller")
public class TournamentController {

    public static final String BASE_PATH = "/api/tournament";
    public static final String PATH_CREATE = "/";
    public static final String PATH_EDIT = "/";
    public static final String PATH_DEFINE_WINNERS = "/winners/";
    public static final String PATH_GET = "/{tournament_id}";
    public static final String PATH_DELETE = "/{tournament_id}";
    public static final String PATH_GET_LIST = "/list";
    public static final String PATH_GET_LIST_DETAILED = "/detailed-list";
    public static final String PATH_GET_DISCORD_CHANNELS = "/discord-channel-list";

    private final RestTournamentFacade restTournamentFacade;

    @ApiOperation("Get tournament by id")
    @GetMapping(path = PATH_GET)
    public ResponseEntity<TournamentDto> getTournamentById(@PathVariable("tournament_id") long id,
                                                           @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentFacade.getTournament(id, user), HttpStatus.OK);
    }

    @ApiOperation("Get tournament detailed list info (for managers)")
    @ApiPageable
    @GetMapping(path = PATH_GET_LIST_DETAILED)
    public ResponseEntity<Page<TournamentDto>> getTournamentDetailedList(@PageableDefault Pageable pageable,
                                                                         @ApiIgnore @AuthenticationPrincipal User user,
                                                                         @RequestParam(value = "discipline_ids", required = false) Long[] disciplineIdArray,
                                                                         @RequestParam(value = "creator", required = false) String creatorLeagueId,
                                                                         @RequestParam(value = "statuses", required = false) TournamentStatusType... statuses) {
        List<Long> disciplineIdList = nonNull(disciplineIdArray) ? List.of(disciplineIdArray) : null;
        List<TournamentStatusType> statusList = nonNull(statuses) ? List.of(statuses) : null;
        return new ResponseEntity<>(restTournamentFacade.getTournamentDetailedList(pageable, user, creatorLeagueId, disciplineIdList, statusList), HttpStatus.OK);
    }

    @ApiOperation("Get tournament list info")
    @ApiPageable
    @GetMapping(path = PATH_GET_LIST)
    public ResponseEntity<Page<TournamentDto>> getTournamentList(@PageableDefault Pageable pageable,
                                                                 @ApiIgnore @AuthenticationPrincipal User user,
                                                                 @RequestParam(value = "discipline_ids", required = false) Long[] disciplineIdArray,
                                                                 @RequestParam(value = "statuses", required = false) TournamentStatusType... statuses) {

        List<Long> disciplineIdList = nonNull(disciplineIdArray) ? List.of(disciplineIdArray) : null;
        List<TournamentStatusType> statusList = nonNull(statuses) ? List.of(statuses) : null;
        return new ResponseEntity<>(restTournamentFacade.getTournamentList(pageable, user, disciplineIdList, statusList), HttpStatus.OK);
    }

    @ApiOperation("Create new tournament on platform")
    @PostMapping(path = PATH_CREATE)
    public ResponseEntity<TournamentDto> createTournament(@RequestBody TournamentDto tournamentDto,
                                                          @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentFacade.addTournament(tournamentDto, user), HttpStatus.CREATED);
    }

    @ApiOperation("Modify tournament on platform")
    @PutMapping(path = PATH_EDIT)
    public ResponseEntity<TournamentDto> modifyTournament(@RequestBody TournamentDto tournamentDto,
                                                          @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentFacade.editTournament(tournamentDto, user), HttpStatus.OK);
    }

    @ApiOperation("Delete (mark) tournament on platform")
    @DeleteMapping(path = PATH_DELETE)
    public ResponseEntity<TournamentDto> deleteTournament(@PathVariable("tournament_id") long id,
                                                          @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentFacade.deleteTournament(id, user), HttpStatus.OK);
    }

    @ApiOperation("Get channel list info for active tournaments")
    @GetMapping(path = PATH_GET_DISCORD_CHANNELS)
    public ResponseEntity<TournamentDiscordInfoListDto> getDiscordChannelsForActiveTournaments(
            @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restTournamentFacade.getDiscordChannelsForActiveTournament(), HttpStatus.OK);
    }
}
