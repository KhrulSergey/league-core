package com.freetonleague.core.domain.enums;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum TournamentWinnerPlaceType {
    NONE(0),
    FIRST(1),
    SECOND(2),
    THIRD(3),
    FOURTH(4),
    FIFTH(5),
    SIXTH(6),
    SEVENTH(7),
    EIGHTH(8);

    public static Comparator<TournamentWinnerPlaceType> winnerPlaceTypeComparator =
            Comparator.comparingInt(TournamentWinnerPlaceType::getPlaceNumber);
    private final int placeNumber;

    TournamentWinnerPlaceType(int placeNumber) {
        this.placeNumber = placeNumber;
    }

    public static List<TournamentWinnerPlaceType> getPlaceListWithLimit(int limit) {
        if (TournamentWinnerPlaceType.values().length < limit) {
            return null;
        }
        return Stream.of(TournamentWinnerPlaceType.values())
                .filter(value -> value.placeNumber > 0 && value.placeNumber <= limit)
                .collect(Collectors.toList());
    }

    public int getPlaceNumber() {
        return placeNumber;
    }

    public boolean isWinner() {
        return this == FIRST;
    }
}
