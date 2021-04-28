package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.EventDto;
import com.freetonleague.core.domain.enums.TeamStateType;
import com.freetonleague.core.domain.model.Team;


public interface TeamEventService {

    EventDto add(EventDto event);

    /**
     * Process team status changing
     */
    void processTeamStatusChange(Team team, TeamStateType newTeamStateType);
}
