package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.TournamentMatch;
import com.freetonleague.core.domain.model.TournamentSeries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TournamentMatchRepository extends JpaRepository<TournamentMatch, Long>,
        JpaSpecificationExecutor<TournamentMatch> {

    Page<TournamentMatch> findAllByTournamentSeries(Pageable pageable, TournamentSeries tournamentSeries);
}
