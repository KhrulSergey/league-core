package com.freetonleague.core.domain.enums;

public enum TeamStateType {
    CREATED,
    ACTIVE,
    BANNED,
    DELETED;

    public boolean isCreated() {
        return this == CREATED;
    }
}
