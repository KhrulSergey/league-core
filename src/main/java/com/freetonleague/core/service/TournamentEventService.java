package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.EventDto;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.TournamentMatch;


public interface TournamentEventService {

    EventDto add(EventDto event);

    /**
     * Process match status changing
     */
    void processMatchStatusChange(TournamentMatch tournamentMatch, TournamentStatusType newTournamentMatchStatus);
}
