package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.AccountInfoDto;
import com.freetonleague.core.domain.dto.AccountTransactionInfoDto;
import com.freetonleague.core.domain.dto.CouponInfoDto;
import com.freetonleague.core.domain.enums.AccountHolderType;
import com.freetonleague.core.domain.enums.AccountTransactionStatusType;
import com.freetonleague.core.domain.enums.TransactionTemplateType;
import com.freetonleague.core.domain.enums.TransactionType;
import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.*;
import com.freetonleague.core.security.permissions.CanManageFinTransaction;
import com.freetonleague.core.security.permissions.CanManageSystem;
import com.freetonleague.core.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Service-facade for provide data from inner DB and process requests (incl. callback from bank-providers) to save data
 * Also Validate request and incoming data
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RestFinanceFacadeImpl implements RestFinanceFacade {

    //call core-service to get data about user accounting
    private final FinancialClientService financialClientService;
    private final RestTournamentFacade restTournamentFacade;
    private final RestTeamFacade restTeamFacade;
    private final RestUserFacade restUserFacade;

    @Override
    public AccountInfoDto getBalanceByGUID(String GUID, User user) {
        return this.getVerifiedAccountByGUID(GUID, user, true);
    }

    @Override
    public AccountInfoDto getBalanceForUser(User user) {
        if (isNull(user)) {
            log.debug("^ user is not authenticate. 'getBalanceForUser' in RestFinanceFacade request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'getBalanceForUser' request denied");
        }
        return this.getVerifiedAccountByHolder(user.getLeagueId(), AccountHolderType.USER, user, false);
    }

    @CanManageSystem
    @Override
    public AccountInfoDto getBalanceByUserLeagueId(String leagueId, User user) {
        User userByLeagueId = restUserFacade.getVerifiedUserByLeagueId(leagueId);
        return this.getVerifiedAccountByHolder(userByLeagueId.getLeagueId(), AccountHolderType.USER, user, true);
    }

    @Override
    public AccountInfoDto getBalanceByTeam(Long teamId, User user) {
        Team team = restTeamFacade.getVerifiedTeamById(teamId, user, true);
        return this.getVerifiedAccountByHolder(team.getCoreId(), AccountHolderType.TEAM, user, true);
    }

    @CanManageSystem
    @Override
    public AccountInfoDto getBalanceByTournament(Long tournamentId, User user) {
        Tournament tournament = restTournamentFacade.getVerifiedTournamentById(tournamentId);
        return this.getVerifiedAccountByHolder(tournament.getCoreId(), AccountHolderType.TOURNAMENT, user, true);
    }

    /**
     * Returns created withdraw fund transaction info (with pause status) for specified params
     */
    @Override
    public AccountTransactionInfoDto createWithdrawRequest(Double amount, String sourceAccountGUID, String targetAddress, User user) {
        if (amount <= 0) {
            log.warn("~ parameter 'amount' is negative or zero and rejected for createWithdrawRequest");
            throw new ValidationException(ExceptionMessages.ACCOUNT_WITHDRAW_VALIDATION_ERROR, "amount",
                    "parameter 'amount' is negative or zero and rejected for createWithdrawRequest");
        }
        if (isBlank(sourceAccountGUID)) {
            log.warn("~ parameter 'sourceAccountGUID' is blank and rejected for createWithdrawRequest");
            throw new ValidationException(ExceptionMessages.ACCOUNT_WITHDRAW_VALIDATION_ERROR, "sourceAccountGUID",
                    "parameter 'sourceAccountGUID' is blank and rejected for createWithdrawRequest");
        }
        if (isBlank(targetAddress)) {
            log.warn("~ parameter 'targetAddress' is blank and rejected for createWithdrawRequest");
            throw new ValidationException(ExceptionMessages.ACCOUNT_WITHDRAW_VALIDATION_ERROR, "targetAddress",
                    "parameter 'targetAddress' is blank and rejected for createWithdrawRequest");
        }
        AccountInfoDto userAccount = this.getVerifiedAccountByGUID(sourceAccountGUID, user, true);
        if (!userAccount.getOwnerExternalGUID().equals(user.getLeagueId().toString())) {
            log.warn("~ parameter 'sourceAccountGUID' is not belongs to current user. Create withdraw request was rejected");
            throw new ValidationException(ExceptionMessages.ACCOUNT_WITHDRAW_VALIDATION_ERROR, "sourceAccountGUID",
                    "parameter 'sourceAccountGUID' is not belongs to current user. Create withdraw request was rejected");
        }

        AccountInfoDto targetAccount = this.getVerifiedAccountByGUID(targetAddress, user, false);
        AccountTransactionInfoDto accountTransactionInfoDto = AccountTransactionInfoDto.builder()
                .sourceAccount(userAccount)
                .targetAccount(targetAccount)
                .amount(amount)
                .status(AccountTransactionStatusType.FROZEN)
                .transactionType(TransactionType.WITHDRAW)
                .transactionTemplateType(TransactionTemplateType.EXTERNAL_BANK)
                .build();
        AccountTransactionInfoDto savedTransactionInfoDto = financialClientService.applyWithdrawTransaction(accountTransactionInfoDto);
        if (isNull(accountTransactionInfoDto)) {

            log.error("!> error while creating team from dto {} for user {}.", savedTransactionInfoDto, user);
            throw new TeamManageException(ExceptionMessages.TEAM_CREATION_ERROR, "Team was not saved on Portal. Check requested params.");
        }
        return savedTransactionInfoDto;
    }


    /**
     * Returns edited withdraw fund transaction info (with pause status) for specified params (only for admin)
     */
    @CanManageFinTransaction
    @Override
    public AccountTransactionInfoDto editWithdrawRequest(String transactionGUID, AccountTransactionInfoDto transactionInfoDto, User user) {
        return null;
    }

    /**
     * Returns updated info of canceled withdraw transaction (not implemented)
     */
    @Override
    public AccountTransactionInfoDto cancelWithdrawRequest(String transactionGUID, User user) {
        log.error("!> cancelWithdrawRequest is not implemented.");
        return null;
    }

    /**
     * Apply coupon by advertisement company hash for user from session
     */
    @Override
    public AccountInfoDto applyCouponByHashForUser(String couponHash, User user) {
        if (isNull(user)) {
            log.debug("^ user is not authenticate. 'applyCouponForUser' in RestFinanceFacade request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'applyCouponForUser' request denied");
        }
        CouponInfoDto advertisementCompanyAccount = financialClientService.getVerifiedAdvertisementCompany(couponHash);
        if (isNull(advertisementCompanyAccount)) {
            log.warn("~ Applying coupon with hash {} was unsuccessful for user. " +
                    "Active advertisement company was not found. Request denied in RestFinanceFacade", couponHash);
            throw new AccountFinanceManageException(ExceptionMessages.ACCOUNT_COUPON_APPLY_ERROR,
                    "Active advertisement company was not found. Applying coupon with hash " + couponHash + " was unsuccessful");
        }

        AccountInfoDto account = financialClientService.applyCouponForUser(advertisementCompanyAccount, user);
        if (isNull(account)) {
            log.warn("~ Applying coupon with hash {} was unsuccessful for user {}. " +
                    "ApplyCouponForUser in RestFinanceFacade request denied", couponHash, user);
            throw new AccountFinanceManageException(ExceptionMessages.ACCOUNT_COUPON_APPLY_ERROR,
                    "Applying coupon with hash " + couponHash + " was unsuccessful");
        }
        return account;
    }

    /**
     * Returns account info by account GUID and user with privacy check
     */
    public AccountInfoDto getVerifiedAccountByGUID(String GUID, User user, boolean checkUser) {
        if (checkUser && isNull(user)) {
            log.debug("^ user is not authenticate. 'getVerifiedAccount' in RestFinanceFacade request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'getVerifiedAccount' request denied");
        }
        AccountInfoDto account = financialClientService.getAccountByGUID(GUID);
        if (isNull(account)) {
            log.debug("^ Account for requested GUID {} was not found. 'getVerifiedAccount' in RestFinanceFacade request denied", GUID);
            throw new AccountFinanceManageException(ExceptionMessages.ACCOUNT_INFO_NOT_FOUND_ERROR, "Account with requested id " + GUID + " was not found");
        }
        return account;
    }

    /**
     * Returns account info by account GUID and user with privacy check
     */
    public AccountInfoDto getVerifiedAccountByExternalAddress(String externalAddress) {
        if (isBlank(externalAddress)) {
            log.debug("^ requested getVerifiedAccountByExternalAddress for BLANK externalAddress. Request rejected");
            throw new AccountFinanceManageException(ExceptionMessages.ACCOUNT_INFO_NOT_FOUND_ERROR, "Account with external address " + externalAddress + " was not found");
        }
        AccountInfoDto account = financialClientService.getAccountByExternalAddress(externalAddress);
        if (isNull(account)) {
            log.debug("^ Account for requested external address {} was not found. 'getVerifiedAccountByExternalAddress' in RestFinanceFacade request denied", externalAddress);
            throw new AccountFinanceManageException(ExceptionMessages.ACCOUNT_INFO_NOT_FOUND_ERROR, "Account with requested external address " + externalAddress + " was not found");
        }
        return account;
    }

    /**
     * Returns account info by account holder type, holder GUID and user with privacy check
     */
    private AccountInfoDto getVerifiedAccountByHolder(UUID GUID, AccountHolderType accountHolderType, User user, boolean checkUser) {
        if (checkUser && isNull(user)) {
            log.debug("^ user is not authenticate. 'getVerifiedAccount' in RestFinanceFacade request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'getVerifiedAccount' request denied");
        }
        AccountInfoDto account = financialClientService.getAccountByHolderInfo(GUID, accountHolderType);
        if (isNull(account)) {
            log.debug("^ Account for requested GUID {} was not found. 'getVerifiedAccount' in RestFinanceFacade request denied", GUID);
            throw new AccountFinanceManageException(ExceptionMessages.ACCOUNT_INFO_NOT_FOUND_ERROR, "Account with requested id " + GUID + " was not found");
        }
        return account;
    }
}
