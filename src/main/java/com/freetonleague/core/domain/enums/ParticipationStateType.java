package com.freetonleague.core.domain.enums;

import java.util.List;

/**
 * Enum of participation statuses (for tournament and docket)
 */
public enum ParticipationStateType {
    CREATED, //just create proposal to enter tournament
    APPROVE, // able to play in tournament
    REJECT, // not qualified for enter tournament, decided by tournament managers
    QUIT, // quit from tournament by team captain
    CANCELLED; //cancelled like the whole tournament, initiated by tournament

    public static final List<ParticipationStateType> activeProposalStateList = List.of(
            APPROVE,
            CREATED
    );

    public static final List<ParticipationStateType> disabledProposalStateList = List.of(
            REJECT,
            QUIT,
            CANCELLED
    );

    public boolean isApproved() {
        return this == ParticipationStateType.APPROVE;
    }

    public boolean isRejected() {
        return this == ParticipationStateType.REJECT;
    }
}
