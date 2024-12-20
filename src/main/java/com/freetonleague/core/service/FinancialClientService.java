package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.CouponInfoDto;
import com.freetonleague.core.domain.dto.finance.AccountInfoDto;
import com.freetonleague.core.domain.dto.finance.AccountTransactionInfoDto;
import com.freetonleague.core.domain.enums.finance.AccountHolderType;
import com.freetonleague.core.domain.enums.finance.AccountTransactionStatusType;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface to interact with Financial unit
 * In future it will call feign-client to interact over api
 */
//TODO delete and user directly RestFinancialUnitFacade from RestFinanceFacade
public interface FinancialClientService {

    /**
     * Returns account info by requested Holder type and GUID from request to Finance Unit
     */
    AccountInfoDto getAccountByHolderInfo(UUID holderGUID, AccountHolderType holderType);

    /**
     * Returns account info by requested account GUID from request to Finance Unit
     */
    AccountInfoDto getAccountByGUID(String GUID);

    /**
     * Returns account info by requested external address of account from request to Finance Unit
     */
    AccountInfoDto getAccountByExternalAddress(String externalAddress);

    /**
     * Returns new account info by requested Holder type and GUID from request to Finance Unit
     */
    AccountInfoDto createAccountByHolderInfo(UUID holderGUID, AccountHolderType holderType, String holderName);

    /**
     * Returns found transaction by specified GUID
     */
    AccountTransactionInfoDto getTransactionByGUID(String transactionGUID);

    /**
     * Returns found transaction history (list) for specified account and/or status list
     *
     * @param pageable       filtered params to search transactions
     * @param statusList     status list to filter transactions
     * @param accountInfoDto account to filter transactions
     * @return transaction list filtered by specified params
     */
    Page<AccountTransactionInfoDto> getTransactionsHistory(Pageable pageable, List<AccountTransactionStatusType> statusList, AccountInfoDto accountInfoDto);

    /**
     * Returns info for created transfer transaction from source to target account
     */
    AccountTransactionInfoDto applyPurchaseTransaction(AccountTransactionInfoDto accountTransactionInfoDto);

    /**
     * Returns info for created withdraw transaction from user to target (external) account
     */
    AccountTransactionInfoDto applyWithdrawTransaction(AccountTransactionInfoDto accountTransactionInfoDto);

    /**
     * Returns updated info for modified withdraw transaction
     */
    AccountTransactionInfoDto editWithdrawTransaction(AccountTransactionInfoDto accountTransactionInfoDto);

    /**
     * Returns updated info for aborted transaction
     */
    AccountTransactionInfoDto abortTransaction(AccountTransactionInfoDto accountTransactionInfoDto);

    /**
     * Returns sign if specified user made deposit transaction at least once to his account
     *
     * @param user specified user to search account and deposit transaction
     * @return sign if user made deposit transaction at least once
     */
    boolean isUserMadeDepositToHisAccount(User user);

    /**
     * Apply coupon by advertisement company hash for user from session
     *
     * @param couponInfo coupon info with ref to bonusAccount
     * @param user       from current session
     * @return updated Account Balance
     */
    AccountInfoDto applyCouponForUser(CouponInfoDto couponInfo, User user);

    /**
     * Returns verified advertisement company by coupon hash
     *
     * @param couponHash advertisement company hash
     * @return coupon info with ref to bonusAccount
     */
    CouponInfoDto getVerifiedAdvertisementCompany(String couponHash);
}
