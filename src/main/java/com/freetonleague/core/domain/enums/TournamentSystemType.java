package com.freetonleague.core.domain.enums;

/**
 * Types of possible tournament grid generation algorithms of creating matches
 */
public enum TournamentSystemType {
    SINGLE_ELIMINATION,
    DOUBLE_ELIMINATION,
    SURVIVAL_ELIMINATION,
    LOBBY_ELIMINATION,
    TRIPLE_ELIMINATION,
    GROUP_FIXTURES,
    MULTI_STAGE;

    public boolean isAutoFinishSeriesEnabled() {
        return this != SURVIVAL_ELIMINATION;
    }

}
