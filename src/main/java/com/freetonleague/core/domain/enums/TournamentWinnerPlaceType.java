package com.freetonleague.core.domain.enums;

public enum TournamentWinnerPlaceType {
    NONE(0),
    FIRST(1),
    SECOND(2),
    THIRD(3),
    FOURTH(4);
    private final int value;

    TournamentWinnerPlaceType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
