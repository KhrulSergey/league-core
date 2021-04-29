package com.freetonleague.core.domain.enums;

import java.util.List;

public enum UserStatusType {
    ACTIVE,
    CREATED,
    BANNED,
    DELETED;

    public static List<UserStatusType> activeUserStatusList = List.of(
            ACTIVE,
            CREATED
    );

    public boolean isCreated() {
        return this == CREATED;
    }
}
