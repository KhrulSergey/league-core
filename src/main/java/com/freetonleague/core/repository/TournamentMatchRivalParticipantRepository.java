package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.TournamentMatchRivalParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TournamentMatchRivalParticipantRepository extends JpaRepository<TournamentMatchRivalParticipant, Long>,
        JpaSpecificationExecutor<TournamentMatchRivalParticipant> {

}
