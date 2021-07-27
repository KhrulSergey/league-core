package com.freetonleague.core.service.financeUnit;

import com.freetonleague.core.domain.dto.finance.*;
import com.freetonleague.core.domain.enums.finance.AccountHolderType;
import com.freetonleague.core.domain.enums.finance.AccountTransactionStatusType;
import com.freetonleague.core.domain.enums.finance.BankProviderType;
import com.freetonleague.core.domain.enums.finance.Currency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
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
     * Returns account info for specified account guid
     *
     * @param externalAddress account external address for account to search
     * @return account info
     */
    AccountInfoDto findAccountByExternalAddress(String externalAddress);

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
     * Returns transaction info for specified guid
     *
     * @param transactionGUID unique identifier of transaction to search
     * @return transaction info
     */
    AccountTransactionInfoDto findTransactionByGUID(String transactionGUID);

    /**
     * Get list of transactions for specified account and filtered by status list. Search in both source and target.
     *
     * @param accountDto to search transaction for
     * @param statusList status list to filter transactions
     * @return list of transactions with pageable params
     */
    Page<AccountTransactionInfoDto> findTransactionListByAccountAndStatusList(Pageable pageable, List<AccountTransactionStatusType> statusList, AccountInfoDto accountDto);

    /**
     * Get exchange rate for specified currencies
     *
     * @return exchange rate data or specified currencies
     */
    ExchangeRatioDto getExchangeRateForCurrencies(Currency currencyToBuy, Currency currencyToSell);

    /**
     * Returns created transaction info for specified data
     *
     * @param accountTransactionInfoDto specified transaction data to process
     * @return transaction info
     */
    AccountTransactionInfoDto createTransferTransaction(AccountTransactionInfoDto accountTransactionInfoDto);

    /**
     * Approve exchange order by specified GUID and make payment to client Account
     */
    ExchangeOrderDto approveExchangeOrder(String GUID);

    /**
     * Create exchange order and transaction for specified currencies and account
     *
     * @param amountToBuy                amount of currencies to buy
     * @param userAccountExternalAddress external address of account to transfer bought currency or to withdraw currency
     * @return created exchange transaction with paymentUrl
     */
    ExchangeOrderDto createExchangeCurrencyOrder(Double amountToBuy, Currency currencyToBuy,
                                                 Currency currencyToSell, String userAccountExternalAddress);

    /**
     * Returns modified transaction info for specified data
     *
     * @return transaction info
     */
    AccountTransactionInfoDto editTransferTransaction(AccountTransactionInfoDto accountTransactionInfoDto);

    /**
     * Returns aborted transaction info for specified GUID
     *
     * @param transactionGUID specified transaction GUID to abort
     * @return transaction info
     */
    AccountTransactionInfoDto abortTransaction(String transactionGUID);

    /**
     * Returns sign if specified holder made deposit transaction at least once
     *
     * @param holderExternalGUID unique external identifier of holder
     * @param holderType         type of holder
     * @return sign if holder made deposit transaction at least once
     */
    boolean isHolderMadeDeposit(UUID holderExternalGUID, AccountHolderType holderType);

//    /**
//     * Returns created withdraw transaction info (with pause status) for specified data
//     *
//     * @param accountTransactionInfoDto specified withdraw transaction data to process
//     * @return transaction info
//     */
//    AccountTransactionInfoDto createWithdrawTransaction(AccountTransactionInfoDto accountTransactionInfoDto);
}
