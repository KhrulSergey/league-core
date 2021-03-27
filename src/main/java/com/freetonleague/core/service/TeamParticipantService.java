package com.freetonleague.core.service;

import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.TeamParticipant;
import com.freetonleague.core.domain.model.User;

import java.util.List;

public interface TeamParticipantService {

    TeamParticipant save(TeamParticipant teamParticipant);

    /**
     * Add participant to team by his id and team id.
     *
     * @param user user what will be added
     * @param team team where participant will be added
     * @return added participant
     */
    TeamParticipant addToTeam(User user, Team team);

    /**
     * Delete participant from team.
     *
     * @param teamParticipant participant what will be deleted
     * @return deleted participant
     */
    TeamParticipant deleteFromTeam(TeamParticipant teamParticipant);

    /**
     * Get all participation info for requested user
     *
     * @param user requested user data
     * @return list of participant-info
     */
    List<TeamParticipant> getAllParticipation(User user);

    /**
     * Returns founded participant by id
     *
     * @param id of team to search
     * @return team entity
     */
    TeamParticipant getById(long id);

    /**
     * Expel participant from his team.
     * Changing status of participant to DELETED
     *
     * @param teamParticipant to expel
     */
    void expel(TeamParticipant teamParticipant);
}
