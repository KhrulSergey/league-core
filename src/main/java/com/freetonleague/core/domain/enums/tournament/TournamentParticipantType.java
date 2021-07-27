package com.freetonleague.core.domain.enums.tournament;

/**
 * Types of possible tournament grid generation algorithms of creating matches
 */
public enum TournamentParticipantType {
    USER,
    TEAM;

    public boolean isAccessibleToUser() {
        return this == USER;
    }

    public boolean isAccessibleToTeam() {
        return this == TEAM;
    }
}
