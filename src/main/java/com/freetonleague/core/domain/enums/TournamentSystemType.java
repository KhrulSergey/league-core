package com.freetonleague.core.domain.enums;

import java.util.List;

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

    public static final List<TournamentSystemType> fullyGeneratedTournamentSystem = List.of(
            SINGLE_ELIMINATION
    );
    public static final List<TournamentSystemType> partialGeneratedTournamentSystem = List.of(
            SURVIVAL_ELIMINATION
    );

    public boolean isAutoFinishSeriesEnabled() {
        return fullyGeneratedTournamentSystem.contains(this);
    }

    public boolean isGenerationRoundEnabled() {
        return fullyGeneratedTournamentSystem.contains(this) || partialGeneratedTournamentSystem.contains(this);
    }

    public boolean isAutoFinishRoundEnabled() {
        return fullyGeneratedTournamentSystem.contains(this) || partialGeneratedTournamentSystem.contains(this);
    }
}
