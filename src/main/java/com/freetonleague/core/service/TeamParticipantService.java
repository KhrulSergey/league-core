package com.freetonleague.core.service;

import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.TeamInviteRequest;
import com.freetonleague.core.domain.model.TeamParticipant;
import com.freetonleague.core.domain.model.User;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Service interface for managing participants and invitations in teams
 */
public interface TeamParticipantService {
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
     * @param isSelfQuit      sign of self quiting from team to be excluded
     */
    void expelParticipant(TeamParticipant teamParticipant, boolean isSelfQuit);

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
     * @param team            for which invite will be created
     * @param teamParticipant will be used as reference for 'creator' of invite
     * @param invitedUser     the person who will get this personal invitation. May be null
     * @return new invite request entity
     */
    TeamInviteRequest createInviteRequest(Team team, TeamParticipant teamParticipant, @Nullable User invitedUser);

    /**
     * Returns all invite requests for specified team.
     *
     * @param team for filter invite requests
     * @return list of invite requests
     */
    List<TeamInviteRequest> getInviteRequestList(Team team);

    /**
     * Returns all invite requests for specified user.
     *
     * @param user for filter invite requests
     * @return list of invite requests
     */
    List<TeamInviteRequest> getInviteRequestListForUser(User user);

    /**
     * Returns new team participant by applying specified token
     *
     * @param inviteRequest specified Invite Request entity
     * @param user          who apply to enter a team from Invite Request entity
     * @return team participant entity of new team member
     */
    TeamParticipant applyInviteRequest(TeamInviteRequest inviteRequest, User user);

    /**
     * Cancel specified invite request in DB.
     * Entity will be only marked as deleted
     *
     * @param inviteRequest entity to update
     */
    void cancelInviteRequest(TeamInviteRequest inviteRequest);

    /**
     * Reject specified invite request in DB.
     * Entity will be only marked as rejected
     *
     * @param inviteRequest entity to update
     */
    void rejectInviteRequest(TeamInviteRequest inviteRequest);

    /**
     * Returns sign of active invite request existence for specified user.
     *
     * @param user for which invite will be find
     * @return true is Active invite request exists, false - if not
     */
    boolean isExistsActiveInviteRequestByInvitedUser(User user);
}
