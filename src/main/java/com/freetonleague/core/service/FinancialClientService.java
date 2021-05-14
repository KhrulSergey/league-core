package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.AccountInfoDto;
import com.freetonleague.core.domain.dto.AccountTransactionInfoDto;
import com.freetonleague.core.domain.enums.AccountHolderType;

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
    AccountTransactionInfoDto createTransactionFromSourceToTargetHolder(AccountTransactionInfoDto accountTransactionInfoDto);
}
