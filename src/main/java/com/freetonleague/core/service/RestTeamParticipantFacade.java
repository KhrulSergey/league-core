package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.TeamDetailedInviteListDto;
import com.freetonleague.core.domain.dto.TeamInviteRequestDto;
import com.freetonleague.core.domain.dto.TeamParticipantDto;
import com.freetonleague.core.domain.model.User;

import java.util.List;

/**
 * Service-facade for managing team participant and invitation
 */
public interface RestTeamParticipantFacade {

    /**
     * Returns all invite requests for specified team.
     *
     * @param teamId specified team identifier
     * @param user   current user from Session
     * @return list of invite requests
     */
    List<TeamInviteRequestDto> getInviteList(long teamId, User user);

    /**
     * Returns all invite requests with detailed info about invitation to teams for current user.
     *
     * @param user current user from Session
     * @return list of invite requests
     */
    List<TeamDetailedInviteListDto> getMyInviteList(User user);

    /**
     * Create new invite request for specified team for requsted User (by username or leagueId).
     *
     * @param teamId      specified team identifier
     * @param username    specified team identifier
     * @param leagueId    specified team identifier
     * @param currentUser current user from Session
     * @return new invite request entity
     */
    TeamInviteRequestDto createInvite(long teamId, User currentUser, String username, String leagueId);

    /**
     * Returns new team participant by applying specified token
     *
     * @param inviteToken specified unique token from Invite Request entity
     * @param user        current user from Session
     * @return team participant Dto of new team member
     */
    TeamParticipantDto applyInviteRequest(String inviteToken, User user);

    /**
     * Delete invite request.
     * Accessible only for a captain of a team or creator of invite request
     *
     * @param inviteToken specified unique token from Invite Request entity
     * @param user        current user from Session
     */
    void cancelInviteRequest(String inviteToken, User user);


    /**
     * Reject invite request by user.
     * Accessible only for user that was invited
     *
     * @param inviteToken specified unique token from Invite Request entity
     * @param user        current user from Session
     */
    void rejectInviteRequest(String inviteToken, User user);
}
