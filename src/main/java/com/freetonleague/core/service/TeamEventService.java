package com.freetonleague.core.service;

import com.freetonleague.core.domain.enums.TeamStateType;
import com.freetonleague.core.domain.model.Team;


public interface TeamEventService {

    /**
     * Process team status changing
     */
    void processTeamStatusChange(Team team, TeamStateType newTeamStateType);
}
