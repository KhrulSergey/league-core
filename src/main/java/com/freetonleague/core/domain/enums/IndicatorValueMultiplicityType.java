package com.freetonleague.core.domain.enums;

public enum IndicatorValueMultiplicityType {
    SELECT,
    MULTISELECT,
    TEXT;

    public boolean isArrayable() {
        return this == MULTISELECT;
    }
}
