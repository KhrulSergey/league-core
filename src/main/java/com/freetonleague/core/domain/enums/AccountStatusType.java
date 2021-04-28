package com.freetonleague.core.domain.enums;

public enum AccountStatusType {
    ACTIVE,
    DISABLED,
    FROZEN //Locked
    ;

    public boolean isActive() {
        return this == ACTIVE;
    }
}
