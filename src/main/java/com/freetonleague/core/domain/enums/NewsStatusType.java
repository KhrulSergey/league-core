package com.freetonleague.core.domain.enums;

public enum NewsStatusType {
    ACTIVE,
    ARCHIVED,
    DELETED;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isArchived() {
        return this == ARCHIVED;
    }

    public boolean isDeleted() {
        return this == DELETED;
    }
}
