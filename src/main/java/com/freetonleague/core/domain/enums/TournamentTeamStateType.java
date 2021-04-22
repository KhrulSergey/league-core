package com.freetonleague.core.domain.enums;

/**
 * Enum of statuses of team participation in tournament
 */
public enum TournamentTeamStateType {
    CREATED,
    APPROVE,
    REJECT,
    CANCELLED;

    public boolean isApproved() {
        return this == TournamentTeamStateType.APPROVE;
    }

    public boolean isRejected() {
        return this == TournamentTeamStateType.REJECT;
    }
}
