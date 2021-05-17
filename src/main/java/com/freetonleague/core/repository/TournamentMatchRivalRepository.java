package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.TournamentMatch;
import com.freetonleague.core.domain.model.TournamentMatchRival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TournamentMatchRivalRepository extends JpaRepository<TournamentMatchRival, Long>,
        JpaSpecificationExecutor<TournamentMatchRival> {

    List<TournamentMatchRival> findAllByTournamentMatch(TournamentMatch tournamentMatch);

}
