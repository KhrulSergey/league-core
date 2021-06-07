package com.freetonleague.core.repository;


import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.TeamParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamParticipantRepository extends JpaRepository<TeamParticipant, Long>,
        JpaSpecificationExecutor<TeamParticipant> {

    @Query(value = "select p from TeamParticipant p where p.team = :team " +
            "and (p.status = com.freetonleague.core.domain.enums.TeamParticipantStatusType.ACTIVE " +
            "or p.status = com.freetonleague.core.domain.enums.TeamParticipantStatusType.CAPTAIN)")
    List<TeamParticipant> findAllActiveParticipantByTeam(@Param("team") Team team);

}
