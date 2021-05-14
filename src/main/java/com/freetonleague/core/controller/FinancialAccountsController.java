package com.freetonleague.core.controller;

import com.freetonleague.core.domain.dto.AccountInfoDto;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.RestFinanceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(path = FinancialAccountsController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Financial Accounts Data Controller")
public class FinancialAccountsController {

    public static final String BASE_PATH = "/api/accounts";
    public static final String PATH_GET_MY = "/my-balance/";
    public static final String PATH_GET_FOR_USER = "/balance-by-user/{leagueId}";
    public static final String PATH_GET = "/balance-by-account-GUID/{GUID}";
    public static final String PATH_GET_TEAM = "/balance-by-team/{team_id}";
    public static final String PATH_GET_TOURNAMENT = "/balance-by-tournament/{tournament_id}";

    private final RestFinanceFacade restFinanceFacade;

    @ApiOperation("Get account info for current user")
    @GetMapping(path = PATH_GET_MY)
    public ResponseEntity<AccountInfoDto> getBalanceForCurrentUser(@ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restFinanceFacade.getBalanceForUser(user), HttpStatus.OK);
    }

    @ApiOperation("Get account info by leagueId")
    @GetMapping(path = PATH_GET_FOR_USER)
    public ResponseEntity<AccountInfoDto> getBalanceByUser(@PathVariable("leagueId") String leagueId,
                                                           @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restFinanceFacade.getBalanceByUserLeagueId(leagueId, user), HttpStatus.OK);
    }

    @ApiOperation("Get account info by tournament GUID (for orgs)")
    @GetMapping(path = PATH_GET_TEAM)
    public ResponseEntity<AccountInfoDto> getBalanceByTeam(@PathVariable("team_id") long teamId,
                                                           @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restFinanceFacade.getBalanceByTeam(teamId, user), HttpStatus.OK);
    }

    @ApiOperation("Get account info by team GUID (for capitan)")
    @GetMapping(path = PATH_GET_TOURNAMENT)
    public ResponseEntity<AccountInfoDto> getBalanceByTournament(@PathVariable("tournament_id") long tournamentId,
                                                                 @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restFinanceFacade.getBalanceByTournament(tournamentId, user), HttpStatus.OK);
    }

    @ApiOperation("Get account info by account unique identifier (only for holder owner)")
    @GetMapping(path = PATH_GET)
    public ResponseEntity<AccountInfoDto> getBalanceByGUID(@PathVariable("GUID") String GUID,
                                                           @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restFinanceFacade.getBalanceByGUID(GUID, user), HttpStatus.OK);
    }
}
