package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.enums.TeamInviteRequestStatusType;
import com.freetonleague.core.domain.enums.TeamParticipantStatusType;
import com.freetonleague.core.domain.enums.TeamStateType;
import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.TeamInviteRequest;
import com.freetonleague.core.domain.model.TeamParticipant;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.TeamParticipantManageException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.repository.TeamInviteRequestRepository;
import com.freetonleague.core.repository.TeamParticipantRepository;
import com.freetonleague.core.service.TeamParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Implementation of the service for accessing Team participation and invitation data from the repository.
 */
@Transactional(propagation = Propagation.REQUIRES_NEW)
@Service
@RequiredArgsConstructor
@Slf4j
public class TeamParticipantServiceImpl implements TeamParticipantService {
    private final TeamParticipantRepository teamParticipantRepository;
    private final TeamInviteRequestRepository teamInviteRequestRepository;

    @Value("${freetonleague.team.invitation.token-duration-in-sec:604800}")
    private Long invitationTokenDurationInSec;

    /**
     * Returns founded participant by id
     */
    @Override
    public TeamParticipant getParticipantById(long id) {
        log.debug("^ getting participant by id: '{}'", id);
        return teamParticipantRepository.findById(id).orElse(null);
    }

    /**
     * Expel participant from his team.
     * Changing status of participant to DELETED
     */
    public void expelParticipant(TeamParticipant teamParticipant, boolean isSelfQuit) {
        if (isNull(teamParticipant)) {
            log.error("!> requesting expel for null participant. Check evoking clients");
            return;
        }
        teamParticipant.setStatus(isSelfQuit ? TeamParticipantStatusType.QUIT : TeamParticipantStatusType.DELETED);
        teamParticipant.setDeletedAt(LocalDateTime.now());
        teamParticipantRepository.save(teamParticipant);
    }

    /**
     * Returns founded invite request by specified token
     */
    @Override
    public TeamInviteRequest getInviteRequestByToken(String inviteToken) {
        if (isBlank(inviteToken)) {
            log.error("!> requesting getInviteRequestByToken for 'Blank' inviteToken. Check evoking clients");
            return null;
        }
        log.debug("^ trying to get team invite request by invite token: '{}'", inviteToken);
        return teamInviteRequestRepository.findByInviteToken(inviteToken);
    }

    /**
     * Returns new invite request for specified team.
     */
    @Override
    public TeamInviteRequest createInviteRequest(Team team, TeamParticipant teamParticipant, User invitedUser) {
        if (isNull(team) || team.getStatus() != TeamStateType.ACTIVE
                || isNull(teamParticipant) || !teamParticipant.getTeam().getId().equals(team.getId())) {
            log.error("!> requesting createInviteRequest for NULL team '{}' or DISABLED team " +
                    "or NULL teamParticipant '{}' or teamParticipant not from this team. Check evoking clients", team, teamParticipant);
            return null;
        }
        LocalDateTime tokenExpiration = LocalDateTime.now().plusMinutes(invitationTokenDurationInSec);
        TeamInviteRequest teamInviteRequest = TeamInviteRequest.builder()
                .team(team)
                .participantCreator(teamParticipant)
                .status(TeamInviteRequestStatusType.OPENED)
                .inviteToken(this.generateInviteToken(team, tokenExpiration))
                .invitedUser(invitedUser)
                .expiration(tokenExpiration)
                .build();
        return teamInviteRequestRepository.save(teamInviteRequest);
    }

    /**
     * Returns all invite requests for specified team.
     */
    @Override
    public List<TeamInviteRequest> getInviteRequestList(Team team) {
        if (isNull(team)) {
            log.error("!> requesting getInviteRequestList for NULL team. Check evoking clients");
            return null;
        }
        return teamInviteRequestRepository.findAllByTeam(team);
    }

    /**
     * Returns all invite requests for specified user.
     */
    @Override
    public List<TeamInviteRequest> getInviteRequestListForUser(User user) {
        if (isNull(user)) {
            log.error("!> requesting getInviteRequestListForUser for NULL user. Check evoking clients");
            return null;
        }
        return teamInviteRequestRepository.findAllByInvitedUser(user);
    }

    /**
     * Returns new team participant by applying specified token
     */
    @Override
    public TeamParticipant applyInviteRequest(TeamInviteRequest inviteRequest, User user) {
        if (isNull(inviteRequest) || isNull(user)) {
            log.error("!> requesting applyInviteRequest for NULL team or NULL user. Check evoking clients");
            return null;
        }
        if (inviteRequest.isExpired()) {
            log.debug("^ applyInviteRequest was rejected because invitation is expired. Update and save in repo InviteRequest status.");
            inviteRequest.setStatus(TeamInviteRequestStatusType.EXPIRED);
            teamInviteRequestRepository.saveAndFlush(inviteRequest);
            throw new TeamParticipantManageException(ExceptionMessages.TEAM_PARTICIPANT_INVITE_EXPIRED_ERROR,
                    "Applying invitation to a team was rejected because of invite status: " + TeamInviteRequestStatusType.EXPIRED);
        }
        //add user to a team as a participant
        TeamParticipant teamParticipant = this.addUserToTeam(user, inviteRequest.getTeam());
        //updating inviteRequest status to closed
        if (nonNull(teamParticipant)) {
            inviteRequest.setStatus(TeamInviteRequestStatusType.CLOSED);
            inviteRequest.setParticipantApplied(teamParticipant);
            teamInviteRequestRepository.save(inviteRequest);
        }
        return teamParticipant;
    }

    /**
     * Cancel specified invite request in DB.
     * Entity will be only marked as cancelled
     */
    @Override
    public void cancelInviteRequest(TeamInviteRequest inviteRequest) {
        if (isNull(inviteRequest)) {
            log.error("!> requesting deleteInviteRequest for NULL TeamInviteRequest. Check evoking clients");
            return;
        }
        inviteRequest.setStatus(TeamInviteRequestStatusType.CANCELLED);
        teamInviteRequestRepository.save(inviteRequest);
    }

    /**
     * Reject specified invite request in DB.
     * Entity will be only marked as rejected
     */
    @Override
    public void rejectInviteRequest(TeamInviteRequest inviteRequest) {
        if (isNull(inviteRequest)) {
            log.error("!> requesting rejectInviteRequest for NULL TeamInviteRequest. Check evoking clients");
            return;
        }
        inviteRequest.setStatus(TeamInviteRequestStatusType.REJECTED);
        teamInviteRequestRepository.save(inviteRequest);
    }

    /**
     * Returns sign of active invite request existence for specified user.
     */
    @Override
    public boolean isExistsActiveInviteRequestByInvitedUser(User user) {
        if (isNull(user)) {
            log.error("!> requesting isExistsActiveInviteRequestByInvitedUser for NULL user. Check evoking clients");
            return false;
        }
        return teamInviteRequestRepository.existsByInvitedUserAndStatus(user, TeamInviteRequestStatusType.OPENED);
    }

    /**
     * Returns list of active TeamParticipant for specified Team
     */
    @Override
    public List<TeamParticipant> getActiveParticipantByTeam(Team team) {
        if (isNull(team)) {
            log.error("!> requesting getActiveParticipantByTeam for NULL team. Check evoking clients");
            return null;
        }
        return teamParticipantRepository.findAllActiveParticipantByTeam(team);
    }

    /**
     * Returns list of filtered TeamParticipant list to view in public
     */
    @Override
    public List<TeamParticipant> filterTeamParticipantFoPublic(List<TeamParticipant> teamParticipantList) {
        // Check conditionals: TeamParticipant is active participant, Team is active, Team is not virtual
        return isNotEmpty(teamParticipantList) ? teamParticipantList.parallelStream()
                .filter(p -> TeamParticipantStatusType.activeStatusList.contains(p.getStatus()))
                .filter(p -> TeamStateType.activeStatusList.contains(p.getTeam().getStatus()))
                .filter(p -> !p.getTeam().getIsVirtual())
                .collect(Collectors.toList())
                : null;
    }

    /**
     * Add user as a participant to team.
     */
    private TeamParticipant addUserToTeam(User user, Team team) {
        if (isNull(team) || team.getStatus() != TeamStateType.ACTIVE
                || isNull(user)) {
            log.error("!> requesting addToTeam for NULL team '{}' or DISABLED team " +
                    "or NULL user '{}'. Check evoking clients", team, user);
            return null;
        }
        TeamParticipant teamParticipant = TeamParticipant.builder()
                .team(team)
                .user(user)
                .status(TeamParticipantStatusType.ACTIVE)
                .joinAt(LocalDateTime.now())
                .build();
        return teamParticipantRepository.save(teamParticipant);
    }

    /**
     * Returns invite token by time-slicing of team model
     */
    private String generateInviteToken(Team team, LocalDateTime date) {
        byte[] uniqueTeamTimeSlice = team.toString().concat(date.toString()).getBytes();
        return UUID.nameUUIDFromBytes(uniqueTeamTimeSlice).toString();
    }
}
