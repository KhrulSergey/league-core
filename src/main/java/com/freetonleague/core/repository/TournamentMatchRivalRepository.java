package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.TournamentMatchRival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TournamentMatchRivalRepository extends JpaRepository<TournamentMatchRival, Long>,
        JpaSpecificationExecutor<TournamentMatchRival> {

}
