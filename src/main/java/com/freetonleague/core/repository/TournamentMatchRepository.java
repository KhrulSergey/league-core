package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.TournamentMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TournamentMatchRepository extends JpaRepository<TournamentMatch, Long>,
        JpaSpecificationExecutor<TournamentMatch> {

}
