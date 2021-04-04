package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.TeamParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TeamParticipantRepository extends JpaRepository<TeamParticipant, Long>,
        JpaSpecificationExecutor<TeamParticipant> {

}
