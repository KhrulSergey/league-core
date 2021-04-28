package com.freetonleague.core.service.financeUnit;

import com.freetonleague.core.domain.enums.AccountHolderType;
import com.freetonleague.core.domain.enums.AccountType;
import com.freetonleague.core.domain.model.Account;
import com.freetonleague.core.domain.model.AccountHolder;
import com.freetonleague.core.domain.model.AccountTransaction;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
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
    Account getAccountByHolderGUIDAndType(UUID externalHolderGUID, AccountHolderType holderType);

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
     * Get transaction by GUID.
     *
     * @param GUID to search transaction for
     * @return found transaction or null
     */
    AccountTransaction getTransaction(UUID GUID);

    /**
     * Get list of transactions for specified account. Search in both source and target.
     *
     * @param account to search transaction for
     * @return list of transactions with pageable params
     */
    Page<AccountTransaction> getTransactionList(Pageable pageable, Account account);

    /**
     * Save new transaction and update Accounts: source (if specified) and target
     *
     * @param accountTransaction data to save ind DB
     * @return saved transaction
     */
    AccountTransaction saveTransaction(AccountTransaction accountTransaction);

    /**
     * Validate token for operate with finance unit
     */
    boolean validateFinanceTokenForDeposit(String token);
}
