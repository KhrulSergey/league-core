package com.freetonleague.core.domain.enums.finance;

public enum CurrencyPairDirectionType {
    FORWARD,
    BACKWARD;

    public boolean isForward() {
        return this == FORWARD;
    }

    public boolean isBackward() {
        return this == BACKWARD;
    }
}
