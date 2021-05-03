package com.freetonleague.core.service;

import com.freetonleague.core.domain.enums.TeamParticipantStatusType;
import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.TeamParticipant;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeamService {
    /**
     * Add new team to DB.
     *
     * @param team data to be added
     * @return Added team
     */
    Team addTeam(Team team);

    /**
     * Edit team in DB.
     * Example - captain, logo, name
     *
     * @param team date to be edited
     * @return Edited team
     */
    Team editTeam(Team team);

    /**
     * Returns founded team by id
     *
     * @param id of team to search
     * @return team entity
     */
    Team getTeamById(long id);

    /**
     * Returns founded team by name
     *
     * @param teamName of team to search
     * @return team entity
     */
    Team getTeamByName(String teamName);

    /**
     * Returns list of all teams
     *
     * @return list of team entities
     */
    Page<Team> getTeamList(Pageable pageable);

    /**
     * Get the list of teams for current user
     *
     * @param user current user from Session
     */
    Page<Team> getTeamListByUser(Pageable pageable, User user);

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
    void disbandTeam(Team team);

    /**
     * Returns a sign of team activity on active tournaments on platform
     */
    boolean isTeamParticipateInActiveTournament(Team team);

    /**
     * Returns participant entity by user in the specified team
     */
    TeamParticipant getParticipantOfTeamByUser(Team team, User user);

    /**
     * Returns sign of user participation in the specified team
     */
    TeamParticipantStatusType getUserParticipantStatusOfTeam(Team team, User user);
}
