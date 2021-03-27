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
     * @param id   of team to search
     * @param user current user from Session
     * @return team entity
     */
    TeamBaseDto getTeamById(long id, User user);

    /**
     * Returns list of all teams
     * Available only base info
     *
     * @return list of team entities
     */
    List<TeamBaseDto> getTeamList(User user);

    /**
     * Registry new team on platform
     *
     * @param teamDto Team to be added
     * @return Added team
     */
    TeamDto addTeam(TeamBaseDto teamDto, User user);

    /**
     * Edit team on Portal.
     * Editable fields only logo, name
     *
     * @param id      Identity of a team
     * @param teamDto Team data to be saved
     * @param user    current user from Session
     * @return Edited team
     */
    TeamExtendedDto editTeam(long id, TeamBaseDto teamDto, User user);

    /**
     * Expel (exclude) from requested team the specified participant.
     * Accessible only for a captain of the team
     *
     * @param id            Identity of a team
     * @param participantId Identity of a participant
     * @param user          current user from Session
     * @return Edited team
     */
    TeamExtendedDto expel(long id, long participantId, User user);

    /**
     * Disband all the band.
     * Accessible only for a captain of the team
     *
     * @param id   Identity of a team
     * @param user current user from Session
     */
    void disband(long id, User user);

    /**
     * Quit current user from specified team
     *
     * @param user current user from Session
     */
    void quitUserFromTeam(long id, User user);

    /**
     * Get the list of teams for current user
     *
     * @param user current user from Session
     */
    List<TeamExtendedDto> getUserTeamList(User user);
}
