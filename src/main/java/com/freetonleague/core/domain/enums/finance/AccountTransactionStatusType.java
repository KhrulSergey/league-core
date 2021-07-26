package com.freetonleague.core.domain.enums.finance;

import com.google.common.collect.Lists;

import java.util.List;

public enum AccountTransactionStatusType {
    INITIATED,
    FROZEN,
    LOCKED,
    FINISHED,
    ABORTED,
    ;

    public boolean isFrozen() {
        return this == FROZEN;
    }

    public boolean isFinished() {
        return this == FINISHED;
    }

    public boolean isAborted() {
        return this == ABORTED;
    }

    public static final List<AccountTransactionStatusType> valueList = Lists.newArrayList(AccountTransactionStatusType.values());
}
