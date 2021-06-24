package com.freetonleague.core.controller;

import com.freetonleague.core.config.ApiPageable;
import com.freetonleague.core.domain.dto.AccountInfoDto;
import com.freetonleague.core.domain.dto.AccountTransactionInfoDto;
import com.freetonleague.core.domain.dto.MPubgTonExchangeAmountDto;
import com.freetonleague.core.domain.enums.AccountTransactionStatusType;
import com.freetonleague.core.domain.filter.MPubgTonWithdrawalCreationFilter;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.RestFinanceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static java.util.Objects.nonNull;

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

    public static final String PATH_GET_TRANSACTION = "/transaction/{transaction_guid}";
    public static final String PATH_GET_MY_TRANSACTION_LIST = "/transaction/list/my";
    public static final String PATH_GET_TRANSACTION_LIST = "/transaction/list";
    public static final String PATH_CREATE_WITHDRAW = "/transaction/withdraw";
    public static final String PATH_CANCEL_WITHDRAW = "/transaction/withdraw/{transaction_guid}";
    public static final String PATH_MODERATE_WITHDRAW = "/transaction/withdraw/{transaction_guid}";

    public static final String PATH_WITHDRAW_TO_MPUBG = "/transaction/withdraw/mpubg";

    public static final String PATH_APPLY_COUPON = "/apply-coupon/";

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

    @ApiOperation("Get transaction by GUID (only for admin")
    @GetMapping(path = PATH_GET_TRANSACTION)
    public ResponseEntity<AccountTransactionInfoDto> getTransactionByGUID(@PathVariable("transaction_guid") String transactionGUID,
                                                                          @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restFinanceFacade.getTransactionByGUID(transactionGUID, user), HttpStatus.OK);
    }

    @ApiOperation("Get transaction (history) list for current user")
    @ApiPageable
    @GetMapping(path = PATH_GET_MY_TRANSACTION_LIST)
    public ResponseEntity<Page<AccountTransactionInfoDto>> getMyTransactionHistory(@PageableDefault Pageable pageable,
                                                                                   @ApiIgnore @AuthenticationPrincipal User user,
                                                                                   @RequestParam(value = "statuses", required = false) AccountTransactionStatusType... statuses) {
        List<AccountTransactionStatusType> statusList = nonNull(statuses) ? List.of(statuses) : null;
        return new ResponseEntity<>(restFinanceFacade.getMyTransactionsHistory(pageable, statusList, user), HttpStatus.OK);
    }

    @ApiOperation("Get transaction history by parameters: statuses or/and user (only for admin)")
    @ApiPageable
    @GetMapping(path = PATH_GET_TRANSACTION_LIST)
    public ResponseEntity<Page<AccountTransactionInfoDto>> getTransactionHistory(@PageableDefault Pageable pageable,
                                                                                 @ApiIgnore @AuthenticationPrincipal User user,
                                                                                 @RequestParam(value = "leagueId", required = false) String leagueId,
                                                                                 @RequestParam(value = "statuses", required = false) AccountTransactionStatusType... statuses) {
        List<AccountTransactionStatusType> statusList = nonNull(statuses) ? List.of(statuses) : null;
        return new ResponseEntity<>(restFinanceFacade.getTransactionsHistory(pageable, leagueId, statusList, user), HttpStatus.OK);
    }

    @ApiOperation("Create withdraw request from user account")
    @PostMapping(path = PATH_CREATE_WITHDRAW)
    public ResponseEntity<AccountTransactionInfoDto> createWithdrawTransaction(@RequestParam(value = "amount") Double amount,
                                                                               @RequestParam(value = "source_account_guid") String sourceAccountGUID,
                                                                               @RequestParam(value = "target_address") String targetAddress,
                                                                               @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restFinanceFacade.createWithdrawRequest(amount, sourceAccountGUID, targetAddress, user), HttpStatus.OK);
    }

    @ApiOperation("Modify withdraw transaction request with new data (only for admin)")
    @PutMapping(path = PATH_MODERATE_WITHDRAW)
    public ResponseEntity<AccountTransactionInfoDto> moderateWithdrawTransaction(@PathVariable("transaction_guid") String transactionGUID,
                                                                                 @RequestBody AccountTransactionInfoDto transactionDto,
                                                                                 @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restFinanceFacade.editWithdrawRequest(transactionGUID, transactionDto, user), HttpStatus.OK);
    }

    @GetMapping(path = PATH_WITHDRAW_TO_MPUBG)
    @ApiOperation("Getting the amount of Mobile PUBG 'UC' in exchange for 'TON'")
    public ResponseEntity<MPubgTonExchangeAmountDto> getMPubgExchangeAmount(
            @Validated @Min(0) Double tonAmount
    ) {
        return ResponseEntity.ok(restFinanceFacade.getMPubgExchangeAmountForTon(tonAmount));
    }

    @PostMapping(path = PATH_WITHDRAW_TO_MPUBG)
    @ApiOperation("Creating a transaction to withdrawal in Mobile PUBG 'UC' currency")
    public ResponseEntity<Void> createMPubgWithdrawalTransaction(
            @RequestBody @Valid MPubgTonWithdrawalCreationFilter filter,
            @ApiIgnore @AuthenticationPrincipal User user
    ) {
        restFinanceFacade.createMPubgWithdrawalTransaction(filter, user);

        return ResponseEntity.noContent().build();
    }


    //TODO delete bonus payments method if no need until 01/09/2021
//    @ApiOperation("Apply coupon by hash for user from session")
//    @PostMapping(path = PATH_APPLY_COUPON)
//    public ResponseEntity<AccountInfoDto> applyCouponForUser(@RequestParam(value = "coupon_hash" ) String couponHash,
//                                                             @ApiIgnore @AuthenticationPrincipal User user) {
//        return new ResponseEntity<>(restFinanceFacade.applyCouponByHashForUser(couponHash, user), HttpStatus.OK);
//    }
}
