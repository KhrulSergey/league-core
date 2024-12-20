package com.freetonleague.core.domain.enums.docket;

import java.util.List;

public enum DocketStatusType {
    CREATED,
    ACTIVE,
    FINISHED,
    DELETED;

    public boolean isCreated() {
        return this == CREATED;
    }

    public boolean isDeleted() {
        return this == DELETED;
    }

    public boolean isFinished() {
        return this == FINISHED;
    }

    /**
     * Returns "active" statuses for dockets
     */
    public static final List<DocketStatusType> activeStatusList = List.of(
            DocketStatusType.CREATED,
            DocketStatusType.ACTIVE
    );
}
