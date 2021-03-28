package com.freetonleague.core.service.implementations;

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
import com.freetonleague.core.mapper.TeamParticipantMapper;
import com.freetonleague.core.service.RestTeamParticipantFacade;
import com.freetonleague.core.service.TeamParticipantService;
import com.freetonleague.core.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestTeamParticipantFacadeImpl implements RestTeamParticipantFacade {

    private final TeamParticipantService participantService;
    private final TeamService teamService;
    private final TeamInviteRequestMapper inviteRequestMapper;
    private final TeamParticipantMapper participantMapper;

    private final List<TeamParticipantStatusType> participantStatusAccessibleToInvite = Arrays.asList(
            TeamParticipantStatusType.ACTIVE,
            TeamParticipantStatusType.CAPTAIN
    );

    /**
     * Returns all invite requests for specified team.
     * Available only for a captain
     *
     * @param teamId specified team identifier
     * @param user   current user from Session
     * @return list of invite requests
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
     * Create new invite request for specified team.
     *
     * @param teamId specified team identifier
     * @param user   current user from Session
     * @return new invite request entity
     */
    @Override
    public TeamInviteRequestDto createInvite(long teamId, User user) {
        Team team = this.getVerifiedTeamById(teamId, user);
        TeamParticipant teamParticipant = teamService.getParticipantOfTeamByUser(team, user);
        //check participation status
        if (isNull(teamParticipant) || !participantStatusAccessibleToInvite.contains(teamParticipant.getStatus())) {
            log.debug("^ forbiddenException for getting invite list for user {} from team {}.", user, team);
            throw new TeamParticipantManageException(ExceptionMessages.TEAM_PARTICIPANT_INVITE_FORBIDDEN_ERROR,
                    "Only captain can get list of all invitation to a team.");
        }
        return inviteRequestMapper.toDto(participantService.createInviteRequest(team, teamParticipant));
    }

    /**
     * Returns new team participant by applying specified token
     *
     * @param inviteToken specified unique token from Invite Request entity
     * @param user        current user from Session
     * @return team participant Dto of new team member
     */
    @Override
    public TeamParticipantDto applyInviteRequest(String inviteToken, User user) {
        TeamInviteRequest teamInviteRequest = this.getVerifiedTeamInviteRequestByToken(inviteToken, user);
        if (nonNull(teamService.getUserParticipantStatusOfTeam(teamInviteRequest.getTeam(), user))) {
            log.debug("^ applyInviteRequest was denied for user {} that is already participate in a team {}.", user, teamInviteRequest.getTeam());
            throw new TeamParticipantManageException(ExceptionMessages.TEAM_PARTICIPANT_INVITE_DUPLICATE_ERROR,
                    "Double participation to one team is forbidden.");
        }
        if (teamInviteRequest.getStatus() != TeamInviteRequestStatusType.OPENED) {
            log.debug("^ applyInviteRequest was rejected by EXPIRED status of teamInviteRequest {} for user {}.", teamInviteRequest, user);
            throw new TeamParticipantManageException(ExceptionMessages.TEAM_PARTICIPANT_INVITE_EXPIRED_ERROR,
                    "Applying invitation to a team was rejected because of invite status: " + teamInviteRequest.getStatus());
        }
        return participantMapper.toDto(participantService.applyInviteRequest(teamInviteRequest, user));
    }

    /**
     * Delete invite request.
     * Accessible only for a captain of a team or creator of invite request
     *
     * @param inviteToken specified unique token from Invite Request entity
     * @param user        current user from Session
     */
    @Override
    public void deleteInviteRequest(String inviteToken, User user) {
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
        participantService.deleteInviteRequest(teamInviteRequest);
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
        Team team = teamService.getById(id);
        if (isNull(team)) {
            log.debug("^ Team with requested id {} was not found. 'getVerifiedTeamById' in RestTeamParticipantFacade request denied", id);
            throw new TeamManageException(ExceptionMessages.TEAM_NOT_FOUND_ERROR, "Team with requested id " + id + " was not found");
        }
        if (team.getStatus() != TeamStateType.ACTIVE) {
            log.debug("^ Team with requested id {} was {}. 'getVerifiedTeamById' in RestTeamParticipantFacade request denied", id, team.getStatus());
            throw new TeamManageException(ExceptionMessages.TEAM_DISABLE_ERROR, "Team with requested id " + id + " was not found");
        }
        return team;
    }
}
