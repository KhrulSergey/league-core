package com.freetonleague.core.repository.tournament;

import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.tournament.TournamentMatch;
import com.freetonleague.core.domain.model.tournament.TournamentSeries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TournamentMatchRepository extends JpaRepository<TournamentMatch, Long>,
        JpaSpecificationExecutor<TournamentMatch> {

    Page<TournamentMatch> findAllByTournamentSeries(Pageable pageable, TournamentSeries tournamentSeries);

    //TODO use it with EventService. Delete until 01/09/2021
    int countByTournamentSeriesAndStatusIn(TournamentSeries tournamentSeries, List<TournamentStatusType> statusList);

    //TODO use it with EventService. Delete until 01/09/2021
    int countByTournamentSeries(TournamentSeries tournamentSeries);

    @Query(value = "select ts.selfHosted from TournamentSettings ts where :match in " +
            "(select m from TournamentMatch m where m.tournamentSeries.tournamentRound.tournament.tournamentSettings = ts)")
    Boolean isMatchModifiableByRival(@Param("match") TournamentMatch tournamentMatch);
}
