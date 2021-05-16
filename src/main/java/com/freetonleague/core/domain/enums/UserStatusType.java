package com.freetonleague.core.domain.enums;

import java.util.List;

public enum UserStatusType {
    INITIATED,
    ACTIVE,
    CREATED,
    BANNED,
    DELETED,
    HIDDEN;

    public static List<UserStatusType> activeUserStatusList = List.of(
            ACTIVE,
            CREATED,
            HIDDEN
    );

    public boolean isCreated() {
        return this == CREATED;
    }

    public boolean isHidden() {
        return this == HIDDEN;
    }

    public boolean isInitiated() {
        return this == INITIATED;
    }
}
