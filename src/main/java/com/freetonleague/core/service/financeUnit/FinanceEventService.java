package com.freetonleague.core.service.financeUnit;

import com.freetonleague.core.domain.enums.finance.AccountStatusType;
import com.freetonleague.core.domain.enums.finance.AccountTransactionStatusType;
import com.freetonleague.core.domain.model.finance.Account;
import com.freetonleague.core.domain.model.finance.AccountTransaction;


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
