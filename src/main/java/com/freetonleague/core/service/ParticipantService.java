package com.freetonleague.core.service;

import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.domain.model.Participant;
import com.freetonleague.core.domain.model.Team;

import java.util.List;

public interface ParticipantService {
    /**
     * Add participant to team by his id and team id.
     *
     * @param user user what will be added
     * @param team        team where participant will be added
     * @return added participant
     */
    Participant addToTeam(User user, Team team);

    /**
     * Delete participant from team.
     *
     * @param participant participant what will be deleted
     * @return deleted participant
     */
    Participant deleteFromTeam(Participant participant);

    /**
     * Get all participation info for requested user
     *
     * @param user requested user data
     * @return list of participant-info
     */
    List<Participant> getAllParticipation(User user);
}
