package com.freetonleague.core.service.financeUnit;

import com.freetonleague.core.domain.enums.AccountStatusType;
import com.freetonleague.core.domain.enums.AccountTransactionStatusType;
import com.freetonleague.core.domain.model.Account;
import com.freetonleague.core.domain.model.AccountTransaction;


public interface FinanceEventService {

    /**
     * Process transaction status changing
     */
    void processAccountStatusChange(Account account, AccountStatusType newAccountStatusType);

    /**
     * Process transaction status changing
     */
    void processTransactionStatusChange(AccountTransaction accountTransaction,
                                        AccountTransactionStatusType newAccountTransactionStatusType);
}
