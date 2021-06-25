package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.TournamentRound;
import com.freetonleague.core.domain.model.TournamentSeries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface TournamentSeriesRepository extends JpaRepository<TournamentSeries, Long>,
        JpaSpecificationExecutor<TournamentSeries> {

    Page<TournamentSeries> findAllByTournamentRound(Pageable pageable, TournamentRound tournamentRound);

    @Query(value = "select ts.selfHosted from TournamentSettings ts where :series in (select s from TournamentSeries s where s.tournamentRound.tournament.tournamentSettings = ts) ")
    Boolean isSeriesModifiableByRival(@Param("series") TournamentSeries tournamentSeries);
}
