package com.freetonleague.core.service.implementations;

import com.freetonleague.core.cloudclient.TelegramClientService;
import com.freetonleague.core.domain.dto.CouponInfoDto;
import com.freetonleague.core.domain.dto.MPubgTonExchangeAmountDto;
import com.freetonleague.core.domain.dto.TelegramMPubgExchangeNotification;
import com.freetonleague.core.domain.dto.finance.AccountInfoDto;
import com.freetonleague.core.domain.dto.finance.AccountTransactionInfoDto;
import com.freetonleague.core.domain.dto.finance.ExchangeOrderDto;
import com.freetonleague.core.domain.dto.finance.ExchangeRatioDto;
import com.freetonleague.core.domain.enums.finance.*;
import com.freetonleague.core.domain.filter.MPubgTonWithdrawalCreationFilter;
import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.domain.model.finance.Account;
import com.freetonleague.core.domain.model.finance.AccountTransaction;
import com.freetonleague.core.domain.model.tournament.Tournament;
import com.freetonleague.core.exception.AccountFinanceManageException;
import com.freetonleague.core.exception.UnauthorizedException;
import com.freetonleague.core.exception.ValidationException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.mapper.UserMapper;
import com.freetonleague.core.security.permissions.CanManageFinTransaction;
import com.freetonleague.core.security.permissions.CanManageSystem;
import com.freetonleague.core.service.*;
import com.freetonleague.core.service.financeUnit.FinancialUnitService;
import com.freetonleague.core.service.financeUnit.RestFinancialUnitFacade;
import com.freetonleague.core.service.tournament.RestTournamentFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Service-facade for provide data from inner DB and process requests (incl. callback from bank-providers) to save data
 * Also Validate request and incoming data
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RestFinanceFacadeImpl implements RestFinanceFacade {

    //TODO delete and user directly RestFinancialUnitFacade
    private final FinancialClientService financialClientService;    //call core-service to get data about user accounting
    private final FinancialUnitService financialUnitService;
    private final RestTournamentFacade restTournamentFacade;
    private final RestTeamFacade restTeamFacade;
    private final RestUserFacade restUserFacade;
    private final UserMapper userMapper;
    private final SettingsService settingsService;
    private final RestFinancialUnitFacade restFinancialUnitFacade;
    private final TelegramClientService telegramClientService;

    @Value("${freetonleague.service.league-finance.min-withdraw-value:12.0}")
    private Double minWithdrawFundValue;

    private static final Double withdrawFundFractionStep = 0.1;

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
     * Returns found transaction by specified GUID or Null
     */
    @CanManageFinTransaction
    @Override
    public AccountTransactionInfoDto getTransactionByGUID(String transactionGUID, User user) {
        return this.getVerifiedTransactionByGUID(transactionGUID);
    }

    /**
     * Returns found transaction history (list) for current user
     */
    @Override
    public Page<AccountTransactionInfoDto> getMyTransactionsHistory(Pageable pageable, List<AccountTransactionStatusType> statusList, User user) {
        if (isNull(user)) {
            log.debug("^ user is not authenticate. 'getMyTransactionsHistory' in RestFinanceFacade request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'getMyTransactionsHistory' request denied");
        }
        log.debug("^ requested getMyTransactionsHistory in RestFinanceFacade for statusList '{}' by user.id '{}'",
                statusList, user.getLeagueId());
        AccountInfoDto accountInfoDto = this.getVerifiedAccountByHolder(user.getLeagueId(), AccountHolderType.USER, user, false);
        return financialClientService.getTransactionsHistory(pageable, statusList, accountInfoDto);
    }

    /**
     * Returns found transaction history (list) by specified params (only for admin)
     */
    @CanManageFinTransaction
    @Override
    public Page<AccountTransactionInfoDto> getTransactionsHistory(Pageable pageable, String leagueId, List<AccountTransactionStatusType> statusList, User user) {
        AccountInfoDto accountInfoDto = null;
        if (!isBlank(leagueId)) {
            User filteredUser = restUserFacade.getVerifiedUserByLeagueId(leagueId);
            accountInfoDto = nonNull(filteredUser)
                    ? this.getVerifiedAccountByHolder(filteredUser.getLeagueId(), AccountHolderType.USER, user, false)
                    : null;
        }
        log.debug("^ requested getTransactionsHistory in RestFinanceFacade for statusList '{}' for user.id '{}'",
                statusList, leagueId);
        return financialClientService.getTransactionsHistory(pageable, statusList, accountInfoDto);
    }

    /**
     * Returns created withdraw fund transaction info (with pause status) for specified params
     */
    @Override
    public AccountTransactionInfoDto createWithdrawRequest(Double amount, String sourceAccountGUID, String targetAddress, User user) {
        if (amount < minWithdrawFundValue || amount % withdrawFundFractionStep == 0) {
            log.warn("~ parameter 'amount' is less than minWithdrawFundValue or fractional step is illegal. Withdraw request is rejected");
            throw new ValidationException(ExceptionMessages.TRANSACTION_VALIDATION_ERROR, "amount",
                    "parameter 'amount' is less than minWithdrawFundValue or fractional step is illegal. Withdraw request is rejected");
        }
        if (isBlank(sourceAccountGUID)) {
            log.warn("~ parameter 'sourceAccountGUID' is blank and rejected for createWithdrawRequest");
            throw new ValidationException(ExceptionMessages.TRANSACTION_VALIDATION_ERROR, "sourceAccountGUID",
                    "parameter 'sourceAccountGUID' is blank and rejected for createWithdrawRequest");
        }
        if (isBlank(targetAddress)) {
            log.warn("~ parameter 'targetAddress' is blank and rejected for createWithdrawRequest");
            throw new ValidationException(ExceptionMessages.TRANSACTION_VALIDATION_ERROR, "targetAddress",
                    "parameter 'targetAddress' is blank and rejected for createWithdrawRequest");
        }
        AccountInfoDto userAccount = this.getVerifiedAccountByGUID(sourceAccountGUID, user, true);
        if (!userAccount.getOwnerExternalGUID().equals(user.getLeagueId().toString())) {
            log.warn("~ parameter 'sourceAccountGUID' is not belongs to current user. Create withdraw request was rejected");
            throw new ValidationException(ExceptionMessages.TRANSACTION_VALIDATION_ERROR, "sourceAccountGUID",
                    "parameter 'sourceAccountGUID' is not belongs to current user. Create withdraw request was rejected");
        }
        log.debug("^ requested createWithdrawRequest in RestFinanceFacade with amount '{}', sourceAccountGUID '{}', targetAddress '{}' by user.id '{}'",
                amount, sourceAccountGUID, targetAddress, user.getLeagueId());
        // get external account by ExternalAddress (or create new one)
        AccountInfoDto targetAccount = this.getVerifiedAccountByExternalAddress(targetAddress);
        //Compose withdraw transaction
        AccountTransactionInfoDto accountTransactionInfoDto = AccountTransactionInfoDto.builder()
                .sourceAccount(userAccount)
                .targetAccount(targetAccount)
                .amount(amount)
                .status(AccountTransactionStatusType.FROZEN) //Pause transaction and wait for manager approve
                .transactionType(AccountTransactionType.WITHDRAW)
                .transactionTemplateType(AccountTransactionTemplateType.EXTERNAL_BANK)
                .build();
        AccountTransactionInfoDto savedTransactionInfoDto = financialClientService.applyWithdrawTransaction(accountTransactionInfoDto);
        if (isNull(savedTransactionInfoDto)) {
            log.error("!> error while creating withdraw transaction from data '{}' for user '{}'.", accountTransactionInfoDto, user.getLeagueId());
            throw new AccountFinanceManageException(ExceptionMessages.TRANSACTION_WITHDRAW_CREATION_ERROR,
                    "Withdraw transaction was not finished and not saved on Portal. Check requested params.");
        }
        return savedTransactionInfoDto;
    }


    /**
     * Returns edited withdraw fund transaction info (with pause status) for specified params (only for admin)
     */
    @CanManageFinTransaction
    @Override
    public AccountTransactionInfoDto editWithdrawRequest(String transactionGUID, AccountTransactionInfoDto transactionInfoDto, User user) {
        if (isBlank(transactionInfoDto.getGUID()) || !transactionGUID.equals((transactionInfoDto.getGUID()))) {
            log.debug("^ Transaction with requested GUID parameter '{}' is not match specified GUID in transactionInfoDto '{}' for." +
                    " 'editWithdrawRequest' in RestFinanceFacade request denied", transactionGUID, transactionInfoDto.getGUID());
            throw new AccountFinanceManageException(ExceptionMessages.TRANSACTION_VALIDATION_ERROR,
                    String.format("Transaction with requested GUID '%s' is not match specified GUID in transactionInfoDto '%s'", transactionGUID, transactionInfoDto.getGUID()));
        }
        if (isNull(transactionInfoDto.getStatus())) {
            log.warn("~ parameter 'status' is not set for transactionInfoDto. Modifying transaction request was rejected");
            throw new ValidationException(ExceptionMessages.TRANSACTION_VALIDATION_ERROR, "status",
                    "parameter 'status' is is not set for transactionInfoDto. Modifying transaction request was rejected");
        }
        log.debug("^ requested editWithdrawRequest in RestFinanceFacade for transactionGUID '{}', data '{}', by user.id '{}'",
                transactionGUID, transactionInfoDto, user.getLeagueId());
        AccountTransactionInfoDto existedTransaction = this.getVerifiedTransactionByGUID(transactionGUID);
        // Change only status of transaction
        existedTransaction.setStatus(transactionInfoDto.getStatus());
        if (transactionInfoDto.getStatus().isFinished()) {
            existedTransaction.setApprovedBy(userMapper.toDto(user));
        }

        AccountTransactionInfoDto savedTransactionInfoDto = financialClientService.editWithdrawTransaction(existedTransaction);
        if (isNull(savedTransactionInfoDto)) {
            log.error("!> error while modifying withdraw transaction from data '{}' for user '{}'.", transactionInfoDto, user.getLeagueId());
            throw new AccountFinanceManageException(ExceptionMessages.TRANSACTION_WITHDRAW_CREATION_ERROR,
                    "Withdraw transaction was not finished and not saved on Portal. Check requested params.");
        }
        return savedTransactionInfoDto;
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
            log.warn("~ Applying coupon with hash '{}' was unsuccessful for user. " +
                    "Active advertisement company was not found. Request denied in RestFinanceFacade", couponHash);
            throw new AccountFinanceManageException(ExceptionMessages.ACCOUNT_COUPON_APPLY_ERROR,
                    "Active advertisement company was not found. Applying coupon with hash " + couponHash + " was unsuccessful");
        }

        AccountInfoDto account = financialClientService.applyCouponForUser(advertisementCompanyAccount, user);
        if (isNull(account)) {
            log.warn("~ Applying coupon with hash '{}' was unsuccessful for user '{}'. " +
                    "ApplyCouponForUser in RestFinanceFacade request denied", couponHash, user);
            throw new AccountFinanceManageException(ExceptionMessages.ACCOUNT_COUPON_APPLY_ERROR,
                    "Applying coupon with hash " + couponHash + " was unsuccessful");
        }
        return account;
    }

    @Override
    public MPubgTonExchangeAmountDto getMPubgExchangeAmountForTon(Double tonAmount) {
        Double rate = Double.valueOf(settingsService.getValue(SettingsService.TON_TO_UC_EXCHANGE_RATE_KEY));

        return MPubgTonExchangeAmountDto.builder()
                .tonAmount(tonAmount)
                .ucAmount(tonAmount * rate)
                .build();
    }

    @Override
    public void createMPubgWithdrawalTransaction(MPubgTonWithdrawalCreationFilter filter, User user) {
        MPubgTonExchangeAmountDto amountDto = getMPubgExchangeAmountForTon(filter.getTonAmount());

        Account account = financialUnitService.getAccountByHolderExternalGUIDAndType(
                user.getLeagueId(), AccountHolderType.USER);

        AccountInfoDto targetAccount = restFinancialUnitFacade.findAccountByExternalAddress("MPUBG");

        AccountTransaction accountTransaction = AccountTransaction.builder()
                .amount(filter.getTonAmount())
                .sourceAccount(account)
                .targetAccount(financialUnitService.getAccountByGUID(UUID.fromString(targetAccount.getGUID())))
                .transactionType(AccountTransactionType.PAYMENT)
                .transactionTemplateType(AccountTransactionTemplateType.PRODUCT_PURCHASE)
                .status(AccountTransactionStatusType.FINISHED)
                .build();

        financialUnitService.createTransaction(accountTransaction);

        log.info("From TON to UC request. {} TON to {} UC", filter.getTonAmount(), amountDto.getUcAmount());

        telegramClientService.sendMPubgExchangeNotification(
                TelegramMPubgExchangeNotification.builder()
                        .pubgId(filter.getPubgId())
                        .ucAmount(amountDto.getUcAmount())
                        .tonAmount(amountDto.getTonAmount())
                        .build()
        );
    }

    /**
     * Get exchange rate for specified currencies
     */
    @Override
    public ExchangeRatioDto getExchangeRateForCurrencies(Currency currencyToBuy, Currency currencyToSell) {
        ExchangeRatioDto exchangeRatioDto = restFinancialUnitFacade.getExchangeRateForCurrencies(currencyToBuy, currencyToSell);
        if (isNull(exchangeRatioDto)) {
            log.error("!> Exchange currency rate request was not created for specified data currencyToBuy '{}', currencyToSell '{}'." +
                    " . Check stack trace!", currencyToBuy, currencyToSell);
            throw new AccountFinanceManageException(ExceptionMessages.EXCHANGE_CURRENCY_RATE_CREATION_ERROR,
                    "Exchange currency rate request was not created for specified data. Check provider data");
        }
        return exchangeRatioDto;
    }

    /**
     * Create exchange transaction for specified currencies and account GUID (or from user session)
     * Now it's available to buy only TON crystals
     */
    @Override
    public ExchangeOrderDto createExchangeOrder(Double amountToBuy, Currency currencyToBuy,
                                                Currency currencyToSell, String targetAccountGUID, User user) {
        if (isNull(user)) {
            log.debug("^ user is not authenticate. 'createExchangeTransaction' in RestFinanceFacade request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'createExchangeOrder' request denied");
        }
        if (!this.verifyExchangeCurrencyOrderBusinessLogic(amountToBuy, currencyToBuy, currencyToSell)) {
            log.debug("^ Exchange currency transaction is badly set with data amountToBuy: '{}', currencyToBuy '{}', " +
                    "currencyToSell '{}', targetAccountGUID '{}', user.id '{}'. 'createExchangeOrder' in " +
                    "RestFinanceFacade request denied", amountToBuy, currencyToBuy, currencyToSell, targetAccountGUID, user.getLeagueId());
            throw new AccountFinanceManageException(ExceptionMessages.TRANSACTION_VALIDATION_ERROR,
                    "Exchange currency transaction data is badly set");
        }
        String userAccountExternalAddress;
        if (!isBlank(targetAccountGUID)) {
            userAccountExternalAddress = this.getVerifiedAccountByGUID(targetAccountGUID, user, false).getExternalAddress();
        } else if (!isBlank(user.getBankAccountAddress())) {
            userAccountExternalAddress = user.getBankAccountAddress();
        } else {
            userAccountExternalAddress = this.getVerifiedAccountByHolder(user.getLeagueId(),
                    AccountHolderType.USER, user, false).getExternalAddress();
        }
        if (isBlank(userAccountExternalAddress)) {
            log.debug("^ Account for specified user '{}' was not found. " +
                    "'createExchangeTransaction' in RestFinanceFacade request denied", user.getLeagueId());
            throw new AccountFinanceManageException(ExceptionMessages.ACCOUNT_INFO_NOT_FOUND_ERROR,
                    "Account for requested user '" + user.getLeagueId() + "' was not found for createExchangeOrder");
        }
        ExchangeOrderDto exchangeOrderDto = restFinancialUnitFacade.createExchangeCurrencyOrder(amountToBuy,
                currencyToBuy, currencyToSell, userAccountExternalAddress);
        if (isNull(exchangeOrderDto)) {
            log.error("!> Exchange currency order was not created for specified data amountToBuy: '{}', currencyToBuy '{}', " +
                            "currencyToSell '{}', targetAccountGUID '{}', user.id '{}'. Check stack trace!", amountToBuy,
                    currencyToBuy, currencyToSell, targetAccountGUID, user.getLeagueId());
            throw new AccountFinanceManageException(ExceptionMessages.EXCHANGE_CURRENCY_ORDER_CREATION_ERROR,
                    "Exchange currency order was not created for specified data. Check provider data");
        }
        return exchangeOrderDto;
    }

    /**
     * Approve exchange order by specified GUID and make payment to client Account
     */
    @CanManageSystem
    @Override
    public ExchangeOrderDto approveExchangeOrder(String exchangeOrderGUID, User user) {
        return restFinancialUnitFacade.approveExchangeOrder(exchangeOrderGUID);
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
            log.debug("^ Account for requested GUID '{}' was not found. 'getVerifiedAccount' in RestFinanceFacade request denied", GUID);
            throw new AccountFinanceManageException(ExceptionMessages.ACCOUNT_INFO_NOT_FOUND_ERROR, "Account with requested id '" + GUID + "' was not found");
        }
        if (!account.getOwnerExternalGUID().equals(user.getLeagueId().toString()) && !account.getOwnerType().equals(AccountHolderType.USER)) {
            log.debug("^ Specified account.guid '{}' not belong to for specified user.leagueId '{}'. 'getVerifiedAccount' in RestFinanceFacade request denied", GUID, user.getLeagueId());
            throw new AccountFinanceManageException(ExceptionMessages.ACCOUNT_NOT_BELONG_TO_USER, "Account for requested guid '" + GUID + "' was not belongs to user");
        }
        return account;
    }

    /**
     * Returns account info by account External address with privacy check
     */
    public AccountInfoDto getVerifiedAccountByExternalAddress(String externalAddress) {
        if (isBlank(externalAddress)) {
            log.debug("^ requested getVerifiedAccountByExternalAddress for BLANK externalAddress. Request rejected");
            throw new AccountFinanceManageException(ExceptionMessages.ACCOUNT_INFO_NOT_FOUND_ERROR, "Account with external address '" + externalAddress + "' 'was not found");
        }
        AccountInfoDto account = financialClientService.getAccountByExternalAddress(externalAddress);
        if (isNull(account)) {
            log.debug("^ Account for requested external address '{}' was not found. 'getVerifiedAccountByExternalAddress' in RestFinanceFacade request denied", externalAddress);
            throw new AccountFinanceManageException(ExceptionMessages.ACCOUNT_INFO_NOT_FOUND_ERROR, "Account with requested external address '" + externalAddress + "' was not found");
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
            log.debug("^ Account for requested GUID '{}' was not found. 'getVerifiedAccount' in RestFinanceFacade request denied", GUID);
            throw new AccountFinanceManageException(ExceptionMessages.ACCOUNT_INFO_NOT_FOUND_ERROR, "Account with requested id '" + GUID + "' was not found");
        }
        return account;
    }

    private AccountTransactionInfoDto getVerifiedTransactionByGUID(String transactionGUID) {
        AccountTransactionInfoDto existedTransaction = financialClientService.getTransactionByGUID(transactionGUID);
        if (isNull(existedTransaction)) {
            log.debug("^ Transaction with requested GUID '{}' is not found for 'getVerifiedTransactionByGUID' in RestFinanceFacade request denied",
                    transactionGUID);
            throw new AccountFinanceManageException(ExceptionMessages.TRANSACTION_NOT_FOUND_ERROR,
                    String.format("Transaction with requested GUID '%s' is not found. Request rejected.", transactionGUID));
        }
        return existedTransaction;
    }

    /**
     * Verify business logic of exchange currency order request.
     */
    private boolean verifyExchangeCurrencyOrderBusinessLogic(Double amountToBuy, Currency currencyToBuy, Currency currencyToSell) {
        if (amountToBuy < 0) {
            log.debug("^ requested to exchange negative amountToBuy '{}' of currency. " +
                    "'verifyExchangeCurrencyBusinessLogic' request return false", amountToBuy);
            return false;
        }
        return verifyExchangeCurrencyRateBusinessLogic(currencyToBuy, currencyToSell);
    }

    /**
     * Verify business logic of exchange currency rate request.
     * Available only exchanging TON to other currency
     */
    private boolean verifyExchangeCurrencyRateBusinessLogic(Currency currencyToBuy, Currency currencyToSell) {
        if (!(currencyToSell == Currency.TON || currencyToBuy == Currency.TON)) {
            log.debug("^ requested to exchange not permitted currency pair currencyToBuy '{}', currencyToSell '{}'." +
                    " 'verifyExchangeCurrencyBusinessLogic' request return false", currencyToBuy, currencyToSell);
            return false;
        }
        return true;
    }
}
