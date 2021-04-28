package com.freetonleague.core.domain.enums;

public enum TournamentAccessType {
    FREE_ACCESS,
    PAID_ACCESS;

    public boolean isPaid() {
        return this == PAID_ACCESS;
    }
}
