package com.freetonleague.core.domain.enums.product;

import java.util.List;

/**
 * Enum of possible purchase state
 */
public enum ProductPurchaseStateType {
    CREATED,
    APPROVE,
    FROZEN,
    REJECT,
    CANCELLED;

    public static final List<ProductPurchaseStateType> activeProposalStateList = List.of(
            APPROVE,
            CREATED
    );

    public static final List<ProductPurchaseStateType> disabledProposalStateList = List.of(
            REJECT,
            CANCELLED
    );

    public boolean isApproved() {
        return this == ProductPurchaseStateType.APPROVE;
    }

    public boolean isRejected() {
        return this == ProductPurchaseStateType.REJECT;
    }
}
