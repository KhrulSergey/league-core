package com.freetonleague.core.domain.enums;

import java.util.List;

/**
 * Enum of possible purchase state
 */
public enum PurchaseStateType {
    CREATED,
    APPROVE,
    REJECT,
    CANCELLED;

    public static final List<PurchaseStateType> activeProposalStateList = List.of(
            APPROVE,
            CREATED
    );

    public static final List<PurchaseStateType> disabledProposalStateList = List.of(
            REJECT,
            CANCELLED
    );

    public boolean isApproved() {
        return this == PurchaseStateType.APPROVE;
    }

    public boolean isRejected() {
        return this == PurchaseStateType.REJECT;
    }
}
