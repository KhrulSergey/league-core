package com.freetonleague.core.domain.enums.finance;

public enum AccountHolderType {
    USER,
    ORGANIZER,
    TEAM,
    TOURNAMENT,
    DOCKET,
    COMMISSION_FEES;

    public boolean isUser() {
        return this == USER;
    }
}
