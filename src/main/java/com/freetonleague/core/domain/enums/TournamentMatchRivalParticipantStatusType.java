package com.freetonleague.core.domain.enums;

public enum TournamentMatchRivalParticipantStatusType {
    ACTIVE,
    DISABLED,
    AFK, //away from keyboard
    BANNED;

    public boolean isAFK() {
        return this == AFK;
    }
}
