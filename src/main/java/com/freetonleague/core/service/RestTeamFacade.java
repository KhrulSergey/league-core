package com.freetonleague.core.service;

import com.freetonleague.core.domain.model.Team;

public interface RestTeamFacade {
    /**
     * Add new team to DB.
     *
     * @param team Team to be added
     * @return Added team
     */
    Team add(Team team);

    /**
     * Edit team in DB.
     * Example - captain, logo, name
     *
     * @param team Team to be edited
     * @return Edited team
     */
    Team edit(Team team);
}
