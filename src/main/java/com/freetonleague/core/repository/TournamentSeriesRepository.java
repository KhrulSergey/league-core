package com.freetonleague.core.repository;

import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.domain.model.TournamentSeries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface TournamentSeriesRepository extends JpaRepository<TournamentSeries, Long>,
        JpaSpecificationExecutor<TournamentSeries> {

    Page<TournamentSeries> findAllByTournament(Pageable pageable, Tournament tournament);

    /**
     * Returns first tournament series with status in the list and specified tournament
     */
    TournamentSeries findByStatusInAndTournament(List<TournamentStatusType> activeStatusList, Tournament tournament);
}
