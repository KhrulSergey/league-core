package com.freetonleague.core.service;

import com.freetonleague.core.domain.enums.ParticipantStatusType;
import com.freetonleague.core.domain.model.Participant;
import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.User;

import java.util.List;

public interface TeamService {
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

    /**
     * Returns founded team by id
     *
     * @param id of team to search
     * @return team entity
     */
    Team getById(long id);

    /**
     * Returns list of all teams
     *
     * @return list of team entities
     */
    List<Team> getList();

    /**
     * Get the list of teams for current user
     *
     * @param user current user from Session
     */
    List<Team> getListByUser(User user);

    /**
     * Expel (exclude) from requested team the specified participant.
     * Accessible only for a capitan of the team
     *
     * @param team        goal team
     * @param participant entity to be excluded
     * @return Edited team
     */
    Team expel(Team team, Participant participant);

    /**
     * Delete (disband) all the band.
     * Accessible only for a capitan of the team
     *
     * @param team entity to be deleted
     */
    void delete(Team team);


    /**
     * Returns participant entity by user in the specified team
     */
    Participant getParticipantOfTeamByUser(Team team, User user);

    /**
     * Returns sign of user participation in the specified team
     */
    ParticipantStatusType getUserParticipantStatusOfTeam(Team team, User user);
}
