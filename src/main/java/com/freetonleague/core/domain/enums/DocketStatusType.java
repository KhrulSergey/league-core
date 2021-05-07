package com.freetonleague.core.domain.enums;

import java.util.List;

public enum DocketStatusType {
    CREATED,
    ACTIVE,
    FINISHED,
    DELETED;

    public boolean isCreated() {
        return this == CREATED;
    }

    /**
     * Returns "active" statuses for tournaments
     */
    public static List<DocketStatusType> activeStatusList = List.of(
            DocketStatusType.CREATED,
            DocketStatusType.ACTIVE
    );

    public boolean isFinished() {
        return this == FINISHED;
    }
}
