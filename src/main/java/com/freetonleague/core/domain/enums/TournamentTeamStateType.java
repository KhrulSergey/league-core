package com.freetonleague.core.domain.enums;

import java.util.List;

/**
 * Enum of statuses of team participation in tournament
 */
public enum TournamentTeamStateType {
    CREATED,
    APPROVE,
    REJECT,
    CANCELLED;

    public static List<TournamentTeamStateType> activeProposalStateList = List.of(
            APPROVE,
            CREATED
    );

    public static List<TournamentTeamStateType> disabledProposalStateList = List.of(
            REJECT,
            CANCELLED
    );

    public boolean isApproved() {
        return this == TournamentTeamStateType.APPROVE;
    }

    public boolean isRejected() {
        return this == TournamentTeamStateType.REJECT;
    }
}
