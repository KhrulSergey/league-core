package com.freetonleague.core.service;

import com.freetonleague.core.domain.enums.TeamParticipantStatusType;
import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.TeamParticipant;
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
     * Returns founded team by name
     *
     * @param teamName of team to search
     * @return team entity
     */
    Team getByName(String teamName);

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
     * Accessible only for a captain of the team
     *
     * @param team            goal team
     * @param teamParticipant entity to be excluded
     * @param isSelfQuit      sign of self quiting from team to be excluded
     * @return Edited team
     */
    Team expelParticipant(Team team, TeamParticipant teamParticipant, boolean isSelfQuit);

    /**
     * Disband (delete) all the band.
     * Accessible only for a captain of the team
     *
     * @param team entity to be deleted
     */
    void disband(Team team);

    /**
     * Returns participant entity by user in the specified team
     */
    TeamParticipant getParticipantOfTeamByUser(Team team, User user);

    /**
     * Returns sign of user participation in the specified team
     */
    TeamParticipantStatusType getUserParticipantStatusOfTeam(Team team, User user);
}
