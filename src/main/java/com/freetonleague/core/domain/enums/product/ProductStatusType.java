package com.freetonleague.core.domain.enums.product;

public enum ProductStatusType {
    ACTIVE,
    ARCHIVED,
    DELETED;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isDeleted() {
        return this == DELETED;
    }

    public boolean isArchived() {
        return this == ARCHIVED;
    }
}
