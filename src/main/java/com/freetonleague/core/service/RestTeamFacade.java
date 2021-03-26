package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.TeamBaseDto;
import com.freetonleague.core.domain.dto.TeamDto;
import com.freetonleague.core.domain.dto.TeamExtendedDto;
import com.freetonleague.core.domain.model.User;

import java.util.List;

/**
 * Service-facade for managing teams
 */
public interface RestTeamFacade {


    /**
     * Returns founded team by id
     *
     * @param id of team to search
     * @return team entity
     */
    TeamDto getByUd(long id);

    /**
     * Registry new team on platform
     *
     * @param team Team to be added
     * @return Added team
     */
    TeamDto add(TeamDto team, User user);

    /**
     * Edit team on Portal.
     * Editable fields only logo, name
     *
     * @param id   Identity of a team
     * @param team Team data to be saved
     * @param user current user from Session
     * @return Edited team
     */
    TeamExtendedDto edit(Long id, TeamBaseDto team, User user);

    /**
     * Expel from requested team the specified participant.
     * Accessible only for a capitan of the team
     *
     * @param id           Identity of a team
     * @param participantId Identity of a participant
     * @param user         current user from Session
     * @return Edited team
     */
    TeamExtendedDto expel(Long id, Long participantId, User user);

    /**
     * Disband all the band.
     * Accessible only for a capitan of the team
     *
     * @param id   Identity of a team
     * @param user current user from Session
     */
    void disband(Long id, User user);

    /**
     * Quit current user from specified team
     *
     * @param user current user from Session
     */
    void quitUserFromTeam(Long id, User user);

    /**
     * Get the list of teams for current user
     *
     * @param user current user from Session
     */
    List<TeamExtendedDto> getUserTeamList(User user);
}
