package com.freetonleague.core.repository;

import com.freetonleague.core.domain.enums.TeamInviteRequestStatusType;
import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.TeamInviteRequest;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TeamInviteRequestRepository extends JpaRepository<TeamInviteRequest, Long>,
        JpaSpecificationExecutor<TeamInviteRequest> {

    /**
     * Find invite request by unique token string
     */
    TeamInviteRequest findByInviteToken(String inviteToken);

    /**
     * Find invite requests by team
     */
    List<TeamInviteRequest> findAllByTeam(Team team);

    /**
     * Find invite requests by user
     */
    List<TeamInviteRequest> findAllByInvitedUser(User user);

    /**
     * Check if inviteRequest exists for specified user and status
     */
    boolean existsByInvitedUserAndStatus(User user, TeamInviteRequestStatusType requestStatus);
}
