package com.freetonleague.core.domain.enums;

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
}
