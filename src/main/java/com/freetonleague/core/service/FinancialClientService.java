package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.AccountInfoDto;
import com.freetonleague.core.domain.dto.AccountTransactionInfoDto;
import com.freetonleague.core.domain.dto.CouponInfoDto;
import com.freetonleague.core.domain.enums.AccountHolderType;
import com.freetonleague.core.domain.model.User;

import java.util.UUID;

/**
 * Service interface to interact with Financial unit
 * In future it will call feign-client to interact over api
 */
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
