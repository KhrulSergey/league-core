package com.freetonleague.core.service.financeUnit;

import com.freetonleague.core.domain.dto.AccountDepositFinUnitDto;
import com.freetonleague.core.domain.dto.AccountInfoDto;
import com.freetonleague.core.domain.dto.AccountTransactionInfoDto;
import com.freetonleague.core.domain.enums.AccountHolderType;
import com.freetonleague.core.domain.enums.BankProviderType;

import java.util.UUID;

/**
 * Service-facade interface for provide data from inner DB and process requests (incl. callback from bank-providers) to save data
 */
public interface RestFinancialUnitFacade {

    /**
     * Process deposit transfer request to user account
     *
     * @param token              security code to third-party trusted service
     * @param accountDepositInfo deposit info
     */
    void processDeposit(String token, AccountDepositFinUnitDto accountDepositInfo, BankProviderType providerType);

    /**
     * Returns account info for specified holder
     *
     * @param holderGUID unique identifier of holder to search
     * @param holderType type of holder to search
     * @return account info
     */
    AccountInfoDto findAccountByHolder(UUID holderGUID, AccountHolderType holderType);

    /**
     * Returns account info for specified account guid
     *
     * @param GUID unique identifier of account to search
     * @return account info
     */
    AccountInfoDto findAccountByGUID(String GUID);

    /**
     * Returns created account info for specified holder
     *
     * @param holderExternalGUID unique external identifier of holder
     * @param holderType         type of holder
     * @param holderName         name of holder
     * @return account info
     */
    AccountInfoDto createAccountForHolder(UUID holderExternalGUID, AccountHolderType holderType, String holderName);

    /**
     * Returns created transaction info for specified data
     *
     * @param accountTransactionInfoDto specified transaction data to process
     * @return transaction info
     */
    AccountTransactionInfoDto createTransaction(AccountTransactionInfoDto accountTransactionInfoDto);
}