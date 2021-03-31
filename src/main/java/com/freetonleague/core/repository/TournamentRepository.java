package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TournamentRepository extends JpaRepository<Tournament, Long>,
        JpaSpecificationExecutor<Tournament> {

}
