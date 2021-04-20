package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.TournamentRound;
import com.freetonleague.core.domain.model.TournamentSeries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface TournamentSeriesRepository extends JpaRepository<TournamentSeries, Long>,
        JpaSpecificationExecutor<TournamentSeries> {

    Page<TournamentSeries> findAllByTournamentRound(Pageable pageable, TournamentRound tournamentRound);
}
