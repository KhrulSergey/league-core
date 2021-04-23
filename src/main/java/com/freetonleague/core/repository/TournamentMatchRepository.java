package com.freetonleague.core.repository;

import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.TournamentMatch;
import com.freetonleague.core.domain.model.TournamentSeries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TournamentMatchRepository extends JpaRepository<TournamentMatch, Long>,
        JpaSpecificationExecutor<TournamentMatch> {

    Page<TournamentMatch> findAllByTournamentSeries(Pageable pageable, TournamentSeries tournamentSeries);

    //TODO use it with EventService. Delete until 01/09/2021
    int countByTournamentSeriesAndStatusIn(TournamentSeries tournamentSeries, List<TournamentStatusType> statusList);

    //TODO use it with EventService. Delete until 01/09/2021
    int countByTournamentSeries(TournamentSeries tournamentSeries);
}
