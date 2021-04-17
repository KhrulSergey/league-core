package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.TournamentTeamParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TournamentTeamParticipantRepository extends JpaRepository<TournamentTeamParticipant, Long>,
        JpaSpecificationExecutor<TournamentTeamParticipant> {


}
