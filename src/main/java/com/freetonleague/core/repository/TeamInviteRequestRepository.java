package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.TeamInviteRequest;
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
}
