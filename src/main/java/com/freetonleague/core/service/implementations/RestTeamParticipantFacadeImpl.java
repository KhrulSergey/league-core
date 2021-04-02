package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.TeamDetailedInviteListDto;
import com.freetonleague.core.domain.dto.TeamInviteRequestDto;
import com.freetonleague.core.domain.dto.TeamParticipantDto;
import com.freetonleague.core.domain.enums.TeamInviteRequestStatusType;
import com.freetonleague.core.domain.enums.TeamParticipantStatusType;
import com.freetonleague.core.domain.enums.TeamStateType;
import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.TeamInviteRequest;
import com.freetonleague.core.domain.model.TeamParticipant;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.*;
import com.freetonleague.core.mapper.TeamInviteRequestMapper;
import com.freetonleague.core.mapper.TeamMapper;
import com.freetonleague.core.mapper.TeamParticipantMapper;
import com.freetonleague.core.service.RestTeamParticipantFacade;
import com.freetonleague.core.service.RestUserFacade;
import com.freetonleague.core.service.TeamParticipantService;
import com.freetonleague.core.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestTeamParticipantFacadeImpl implements RestTeamParticipantFacade {

    private final TeamParticipantService participantService;
    private final TeamService teamService;
    private final RestUserFacade restUserFacade;

    private final TeamInviteRequestMapper inviteRequestMapper;
    private final TeamParticipantMapper participantMapper;
    private final TeamMapper teamMapper;

    private final List<TeamParticipantStatusType> participantStatusAccessibleToInvite = Arrays.asList(
            TeamParticipantStatusType.ACTIVE,
            TeamParticipantStatusType.CAPTAIN
    );

    /**
     * Returns all invite requests for specified team.
     * Available only for a captain
     */
    @Override
    public List<TeamInviteRequestDto> getInviteList(long teamId, User user) {
        Team team = this.getVerifiedTeamById(teamId, user);
        if (!team.isCaptain(user)) {
            log.warn("~ forbiddenException for getting invite list for user {} from team {}.", user, team);
            throw new TeamParticipantManageException(ExceptionMessages.TEAM_PARTICIPANT_INVITE_FORBIDDEN_ERROR,
                    "Only captain can get list of all invitation to a team.");
        }
        return inviteRequestMapper.toDto(participantService.getInviteRequestList(team));
    }

    /**
     * Returns all invite requests with detailed info about invitation to teams for current user.
     */
    @Override
    public List<TeamDetailedInviteListDto> getMyInviteList(User user) {
        if (isNull(user)) {
            log.debug("^ user is not authenticate. 'getMyInviteList' in RestTeamParticipantFacade request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'getMyInviteList' request denied");
        }
        List<TeamInviteRequest> inviteRequests = participantService.getInviteRequestListForUser(user);
        if (inviteRequests.isEmpty()) {
            return null;
        }
        Map<Team, List<TeamInviteRequest>> teamDetailedInviteMap = new HashMap<>();
        for (TeamInviteRequest inviteRequest : inviteRequests) {
            teamDetailedInviteMap.computeIfAbsent(inviteRequest.getTeam(), k -> new ArrayList<>()).add(inviteRequest);
        }

        List<TeamDetailedInviteListDto> detailedInviteList = new ArrayList<>();
        for (Map.Entry<Team, List<TeamInviteRequest>> entry : teamDetailedInviteMap.entrySet()) {
            detailedInviteList.add(
                    new TeamDetailedInviteListDto(
                            teamMapper.toDto(entry.getKey()),
                            inviteRequestMapper.toDto(entry.getValue())));
        }
        return detailedInviteList;
    }

    /**
     * Create new invite request for specified team for requsted User (by username or leagueId).
     */
    @Override
    public TeamInviteRequestDto createInvite(long teamId, User currentUser, String username, String leagueId) {
        Team team = this.getVerifiedTeamById(teamId, currentUser);
        TeamParticipant teamParticipant = teamService.getParticipantOfTeamByUser(team, currentUser);
        //check participation status
        if (isNull(teamParticipant) || !participantStatusAccessibleToInvite.contains(teamParticipant.getStatus())) {
            log.debug("^ forbiddenException for creating invitation for user {} for team {}.", currentUser, team);
            throw new TeamParticipantManageException(ExceptionMessages.TEAM_PARTICIPANT_INVITE_FORBIDDEN_ERROR,
                    "Only participant of the team can create invitation.");
        }

        User invitedUser = null;
        if (!isBlank(leagueId)) {
            invitedUser = restUserFacade.getVerifiedUserByLeagueId(leagueId);
        } else if (!isBlank(username)) {
            invitedUser = restUserFacade.getVerifiedUserByUsername(username);
        }

        if (nonNull(invitedUser)) {
            if (participantService.isExistsActiveInviteRequestByInvitedUser(invitedUser)) {
                log.debug("^ Invitation to requested user with leagueId '{}' is already existed. Request was denied", leagueId);
                throw new TeamParticipantManageException(ExceptionMessages.TEAM_PARTICIPANT_INVITE_DUPLICATE_ERROR,
                        String.format("Invitation to user with leagueId '%s' is already existed.", leagueId));
            }
            if (nonNull(teamService.getUserParticipantStatusOfTeam(team, invitedUser))) {
                log.debug("^ createInvite was denied for user {} that is already participate in a team {}.", invitedUser, team);
                throw new TeamParticipantManageException(ExceptionMessages.TEAM_PARTICIPANT_INVITE_REJECTED_ERROR,
                        "Double participation to one team is forbidden.");
            }
        }

        return inviteRequestMapper.toDto(participantService.createInviteRequest(team, teamParticipant, invitedUser));
    }

    /**
     * Returns new team participant by applying specified token
     *
     * @param inviteToken specified unique token from Invite Request entity
     * @param currentUser current user from Session
     * @return team participant Dto of new team member
     */
    @Override
    public TeamParticipantDto applyInviteRequest(String inviteToken, User currentUser) {
        TeamInviteRequest teamInviteRequest = this.getVerifiedTeamInviteRequestByToken(inviteToken, currentUser);
        if (nonNull(teamService.getUserParticipantStatusOfTeam(teamInviteRequest.getTeam(), currentUser))) {
            log.debug("^ applyInviteRequest was denied for user {} that is already participate in a team {}.", currentUser, teamInviteRequest.getTeam());
            throw new TeamParticipantManageException(ExceptionMessages.TEAM_PARTICIPANT_INVITE_REJECTED_ERROR,
                    "Double participation to one team is forbidden.");
        }
        if (teamInviteRequest.getStatus() != TeamInviteRequestStatusType.OPENED) {
            log.debug("^ applyInviteRequest was rejected by EXPIRED status of teamInviteRequest {} for user {}.", teamInviteRequest, currentUser);
            throw new TeamParticipantManageException(ExceptionMessages.TEAM_PARTICIPANT_INVITE_EXPIRED_ERROR,
                    "Applying invitation to a team was rejected because of invite status: " + teamInviteRequest.getStatus());
        }
        if (nonNull(teamInviteRequest.getInvitedUser()) && !teamInviteRequest.getInvitedUser().equals(currentUser)) {
            log.debug("^ applyInviteRequest was rejected because invite request assigned for user with ID {}, not for current user {}.",
                    teamInviteRequest.getInvitedUser().getLeagueId(), Objects.requireNonNull(currentUser).getLeagueId());
            throw new TeamParticipantManageException(ExceptionMessages.TEAM_PARTICIPANT_INVITE_ASSIGNED_ERROR,
                    "Applying invitation to a team was rejected because of wrong assigned");
        }

        return participantMapper.toDto(participantService.applyInviteRequest(teamInviteRequest, currentUser));
    }

    /**
     * Cancel invite request.
     * Accessible only for a captain of a team or creator of invite request
     *
     * @param inviteToken specified unique token from Invite Request entity
     * @param user        current user from Session
     */
    @Override
    public void cancelInviteRequest(String inviteToken, User user) {
        TeamInviteRequest teamInviteRequest = this.getVerifiedTeamInviteRequestByToken(inviteToken, user);
        TeamParticipant teamParticipant = teamService.getParticipantOfTeamByUser(teamInviteRequest.getTeam(), user);
        //check rights of user to a requested team (in TeamInviteRequest)
        if (isNull(teamParticipant)
                || (teamParticipant.getStatus() != TeamParticipantStatusType.CAPTAIN)
                || !teamInviteRequest.getParticipantCreator().getId().equals(teamParticipant.getId())) {
            log.warn("~ forbiddenException for deleting invitation for user {} from team {}.", user, teamInviteRequest.getTeam());
            throw new TeamParticipantManageException(ExceptionMessages.TEAM_PARTICIPANT_INVITE_FORBIDDEN_ERROR,
                    "Only captain or author can delete this invitation to a team.");
        }
        participantService.cancelInviteRequest(teamInviteRequest);
    }

    /**
     * Reject invite request by user.
     * Accessible only for user that was invited
     */
    @Override
    public void rejectInviteRequest(String inviteToken, User user) {
        TeamInviteRequest teamInviteRequest = this.getVerifiedTeamInviteRequestByToken(inviteToken, user);
        if (teamInviteRequest.getStatus() != TeamInviteRequestStatusType.OPENED) {
            log.debug("^ applyInviteRequest was rejected by EXPIRED status of teamInviteRequest {} for user {}.", teamInviteRequest, user);
            throw new TeamParticipantManageException(ExceptionMessages.TEAM_PARTICIPANT_INVITE_EXPIRED_ERROR,
                    "Applying invitation to a team was rejected because of invite status: " + teamInviteRequest.getStatus());
        }
        //check rights to reject personal invitation to participate team
        if (!teamInviteRequest.getInvitedUser().equals(user)) {
            log.warn("~ forbiddenException for rejecting invitation for user {} from team {}. Current user {}",
                    teamInviteRequest.getInvitedUser(), teamInviteRequest.getTeam(), user);
            throw new TeamParticipantManageException(ExceptionMessages.TEAM_PARTICIPANT_INVITE_FORBIDDEN_ERROR,
                    "Only invited user can reject this invitation to a team.");
        }
        participantService.rejectInviteRequest(teamInviteRequest);
    }

    /**
     * Verify invite token and user with privacy check
     */
    private TeamInviteRequest getVerifiedTeamInviteRequestByToken(String inviteToken, User user) {
        if (isNull(user)) {
            log.debug("^ user is not authenticate. 'getVerifiedTeamInviteRequestByToken' in RestTeamParticipantFacade request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'getVerifiedTeamInviteRequestByToken' request denied");
        }
        if (isBlank(inviteToken)) {
            log.warn("~ parameter 'inviteToken' is not set for getVerifiedTeamInviteRequestByToken");
            throw new ValidationException(ExceptionMessages.VALIDATION_ERROR, "inviteToken", "parameter is not set for getVerifiedTeamInviteRequestByToken");
        }
        TeamInviteRequest teamInviteRequest = participantService.getInviteRequestByToken(inviteToken);
        if (isNull(teamInviteRequest)) {
            log.debug("^ Invitation to a team with requested token {} was not found. 'getVerifiedTeamInviteRequestByToken' request denied for user {}", inviteToken, user);
            throw new TeamParticipantManageException(ExceptionMessages.TEAM_PARTICIPANT_INVITE_NOT_FOUND_ERROR,
                    "Invitation to a team with requested token " + inviteToken + " was not found");
        }
        return teamInviteRequest;
    }

    /**
     * Getting team by id and user with privacy check
     */
    private Team getVerifiedTeamById(long id, User user) {
        if (isNull(user)) {
            log.debug("^ user is not authenticate. 'getVerifiedTeamById' in RestTeamParticipantFacade request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'getVerifiedTeamById' request denied");
        }
        Team team = teamService.getTeamById(id);
        if (isNull(team)) {
            log.debug("^ Team with requested id {} was not found. 'getVerifiedTeamById' in RestTeamParticipantFacade request denied", id);
            throw new TeamManageException(ExceptionMessages.TEAM_NOT_FOUND_ERROR, "Team with requested id " + id + " was not found");
        }
        if (team.getStatus() != TeamStateType.ACTIVE) {
            log.debug("^ Team with requested id {} was {}. 'getVerifiedTeamById' in RestTeamParticipantFacade request denied", id, team.getStatus());
            throw new TeamManageException(ExceptionMessages.TEAM_DISABLE_ERROR, "Active team with requested id " + id + " was not found");
        }
        return team;
    }
}
