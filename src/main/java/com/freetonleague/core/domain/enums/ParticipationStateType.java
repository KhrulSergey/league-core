package com.freetonleague.core.domain.enums;

import java.util.List;

/**
 * Enum of participation statuses (for tournament and docket)
 */
public enum ParticipationStateType {
    CREATED,
    APPROVE,
    REJECT,
    CANCELLED;

    public static final List<ParticipationStateType> activeProposalStateList = List.of(
            APPROVE,
            CREATED
    );

    public static final List<ParticipationStateType> disabledProposalStateList = List.of(
            REJECT,
            CANCELLED
    );

    public boolean isApproved() {
        return this == ParticipationStateType.APPROVE;
    }

    public boolean isRejected() {
        return this == ParticipationStateType.REJECT;
    }
}
