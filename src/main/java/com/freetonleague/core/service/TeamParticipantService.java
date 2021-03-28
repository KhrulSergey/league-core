package com.freetonleague.core.service;

import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.TeamInviteRequest;
import com.freetonleague.core.domain.model.TeamParticipant;
import com.freetonleague.core.domain.model.User;

import java.util.List;

/**
 * Service interface for managing participants and invitations in teams
 */
public interface TeamParticipantService {

    //TODO удалить метод до 01.04.2021
//    TeamParticipant save(TeamParticipant teamParticipant);
//    /**
//     * Returns all participation info for requested user
//     *
//     * @param user requested user data
//     * @return list of participant-info
//     */
//    List<TeamParticipant> getAllParticipation(User user);

    /**
     * Returns founded participant by id
     *
     * @param id of team to search
     * @return team entity
     */
    TeamParticipant getParticipantById(long id);

    /**
     * Expel (exclude) participant from his team.
     * Changing status of participant to DELETED
     *
     * @param teamParticipant to expel
     */
    void expelParticipant(TeamParticipant teamParticipant);


    /**
     * Returns founded invite request by specified token
     *
     * @param inviteToken specified unique token from Invite Request entity
     * @return invite request entity or null is it's not exists
     */
    TeamInviteRequest getInviteRequestByToken(String inviteToken);

    /**
     * Returns new invite request for specified team.
     *
     * @param team            for which invite will bew created
     * @param teamParticipant will be used as reference for 'creator' of invite
     * @return new invite request entity
     */
    TeamInviteRequest createInviteRequest(Team team, TeamParticipant teamParticipant);

    /**
     * Returns all invite requests for specified team.
     *
     * @param team for filter invite requests
     * @return list of invite requests
     */
    List<TeamInviteRequest> getInviteRequestList(Team team);

    /**
     * Returns new team participant by applying specified token
     *
     * @param inviteRequest specified Invite Request entity
     * @param user          who apply to enter a team from Invite Request entity
     * @return team participant entity of new team member
     */
    TeamParticipant applyInviteRequest(TeamInviteRequest inviteRequest, User user);

    /**
     * Delete specified invite request in DB.
     * Entity will be only marked as deleted
     *
     * @param inviteRequest entity to delete
     */
    void deleteInviteRequest(TeamInviteRequest inviteRequest);
}
