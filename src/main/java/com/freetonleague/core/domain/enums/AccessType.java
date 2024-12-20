package com.freetonleague.core.domain.enums;

public enum AccessType {
    FREE_ACCESS,
    PAID_ACCESS;

    public boolean isPaid() {
        return this == PAID_ACCESS;
    }

    public boolean isFree() {
        return this == FREE_ACCESS;
    }
}
