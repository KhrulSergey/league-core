package com.freetonleague.core.domain.enums;

import java.util.List;

/**
 * Types of possible docket types
 */
public enum DocketSystemType {
    DEFAULT,
    AUCTION;

    public static final List<DocketSystemType> extendedCapabilitiesDocketTypes = List.of(
            AUCTION
    );

    public boolean isProposalDuplicatesProhibited() {
        return !extendedCapabilitiesDocketTypes.contains(this);
    }

    public boolean isCustomParticipantFeeEnabled() {
        return extendedCapabilitiesDocketTypes.contains(this);
    }
}
