package com.freetonleague.core.domain.enums;

public enum UserStatusType {
    ACTIVE,
    CREATED,
    BANNED,
    DELETED;

    public boolean isCreated() {
        return this == CREATED;
    }
}
