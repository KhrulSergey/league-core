package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.EventDto;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.TournamentMatch;
import com.freetonleague.core.domain.model.TournamentRound;
import com.freetonleague.core.domain.model.TournamentSeries;


public interface TournamentEventService {

    EventDto add(EventDto event);

    /**
     * Process match status changing
     */
    void processMatchStatusChange(TournamentMatch tournamentMatch, TournamentStatusType newTournamentMatchStatus);

    /**
     * Process series status changing
     */
    void processSeriesStatusChange(TournamentSeries tournamentSeries, TournamentStatusType newTournamentSeriesStatus);

    /**
     * Process round status changing
     */
    void processRoundStatusChange(TournamentRound tournamentRound, TournamentStatusType newTournamentRoundStatus);

    /**
     * Process series dead head for rivals
     */
    void processSeriesDeadHead(TournamentSeries tournamentSeries);
}
