package com.freetonleague.core.domain.enums;

public enum DocketStatusType {
    CREATED,
    ACTIVE,
    FINISHED,
    DELETED;

    public boolean isCreated() {
        return this == CREATED;
    }
}
