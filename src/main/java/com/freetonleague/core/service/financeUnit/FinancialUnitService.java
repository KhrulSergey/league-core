package com.freetonleague.core.service.financeUnit;

import com.freetonleague.core.domain.enums.AccountHolderType;
import com.freetonleague.core.domain.enums.AccountTransactionStatusType;
import com.freetonleague.core.domain.enums.AccountType;
import com.freetonleague.core.domain.model.finance.Account;
import com.freetonleague.core.domain.model.finance.AccountHolder;
import com.freetonleague.core.domain.model.finance.AccountTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for save transactions and accounts in DB
 */
public interface FinancialUnitService {

    /**
     * Get account by GUID.
     *
     * @param GUID to search account for
     * @return found account or null
     */
    Account getAccountByGUID(UUID GUID);

    /**
     * Get account by holder.
     *
     * @param holder to search account for
     * @return found account or null
     */
    Account getAccountByHolder(AccountHolder holder);

    /**
     * Get account by external holder GUID and Holder type.
     *
     * @param externalHolderGUID to search account for
     * @param holderType         to search account for
     * @return found account or null
     */
    Account getAccountByHolderExternalGUIDAndType(UUID externalHolderGUID, AccountHolderType holderType);

    /**
     * Get account by external address.
     *
     * @param externalAddress to search account for
     * @return found account or null
     */
    Account getAccountByExternalAddress(String externalAddress);

    /**
     * Get account holder by external GUID and Holder type.
     *
     * @param externalHolderGUID to search account for
     * @param holderType         to search account for
     * @return found account holder or null
     */
    AccountHolder getAccountHolderByExternalGUID(UUID externalHolderGUID, AccountHolderType holderType);

    /**
     * Save new account holder with embedded Account
     *
     * @param accountHolder data to save ind DB
     * @param accountType   type of account to save ind DB
     * @return saved transaction
     */
    Account createAccountHolderWithAccount(AccountHolder accountHolder, AccountType accountType);

    /**
     * Save new notTracking account
     *
     * @param account data to save ind DB
     * @return saved transaction
     */
    Account createNotTrackingAccount(Account account);

    /**
     * Get transaction by GUID.
     *
     * @param GUID to search transaction for
     * @return found transaction or null
     */
    AccountTransaction getTransaction(UUID GUID);

    /**
     * Get list of transactions for specified account and filtered by status list. Search in both source and target.
     *
     * @param account    to search transaction for
     * @param statusList status list to filter transactions
     * @return list of transactions with pageable params
     */
    Page<AccountTransaction> findTransactionListByAccountAndStatus(Pageable pageable, List<AccountTransactionStatusType> statusList, Account account);

    /**
     * Save new transaction and update Accounts: source (if specified) and target
     *
     * @param accountTransaction data to save ind DB
     * @return saved transaction
     */
    AccountTransaction createTransaction(AccountTransaction accountTransaction);

    /**
     * Edit transaction and update Accounts: source (if specified) and target
     *
     * @param accountTransaction data to save ind DB
     * @return saved transaction
     */
    AccountTransaction editTransaction(AccountTransaction accountTransaction);

    /**
     * Abort transaction and update Accounts: source (if specified) and target
     */
    AccountTransaction abortTransaction(AccountTransaction transaction);

    /**
     * Validate token for operate with finance unit
     */
    boolean validateFinanceTokenForDeposit(String token);

    /**
     * Returns sign of transaction existence for specified GUID.
     */
    boolean isExistsTransactionByGUID(UUID GUID);

    /**
     * Returns sign of transaction aborted for specified GUID.
     */
    boolean isAbortedTransactionByGUID(UUID GUID);

    /**
     * Returns sign if with specified account was made deposit transaction at least once
     *
     * @param account specified account to search deposit transaction
     * @return sign if with account was made deposit transaction at least once
     */
    boolean isHolderMadeDeposit(Account account);
}
