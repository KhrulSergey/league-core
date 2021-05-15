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
     * Returns new account info by requested Holder type and GUID from request to Finance Unit
     */
    AccountInfoDto createAccountByHolderInfo(UUID holderGUID, AccountHolderType holderType, String holderName);

    /**
     * Returns info for created transaction from source to target holder GUID
     */
    AccountTransactionInfoDto applyTransactionFromSourceToTargetHolder(AccountTransactionInfoDto accountTransactionInfoDto);

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
