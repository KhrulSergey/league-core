package com.freetonleague.core.domain.enums;

import java.util.List;

public enum TeamParticipantStatusType {
    ACTIVE,
    DELETED,
    QUIT,
    CAPTAIN;

    public static final List<TeamParticipantStatusType> activeStatusList = List.of(
            TeamParticipantStatusType.ACTIVE,
            TeamParticipantStatusType.CAPTAIN
    );
}
