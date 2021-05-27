package com.freetonleague.core.domain.enums;

import java.util.List;

public enum TeamStateType {
    CREATED,
    ACTIVE,
    BANNED,
    DELETED;

    public boolean isCreated() {
        return this == CREATED;
    }

    public static final List<TeamStateType> activeStatusList = List.of(
            TeamStateType.CREATED,
            TeamStateType.ACTIVE
    );
}
