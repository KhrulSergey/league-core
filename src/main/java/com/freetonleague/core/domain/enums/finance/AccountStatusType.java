package com.freetonleague.core.domain.enums.finance;

public enum AccountStatusType {
    ACTIVE,
    DISABLED,
    FROZEN, //Locked
    NOT_TRACKING,
    ;

    public boolean isActive() {
        return this == ACTIVE;
    }
}
