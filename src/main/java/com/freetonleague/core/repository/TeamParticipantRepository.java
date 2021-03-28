package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.TeamParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamParticipantRepository extends JpaRepository<TeamParticipant, Long> {

}
