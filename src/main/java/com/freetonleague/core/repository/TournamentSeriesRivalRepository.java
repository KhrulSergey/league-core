package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.TournamentSeries;
import com.freetonleague.core.domain.model.TournamentSeriesRival;
import com.freetonleague.core.domain.model.TournamentTeamProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface TournamentSeriesRivalRepository extends JpaRepository<TournamentSeriesRival, Long>,
        JpaSpecificationExecutor<TournamentSeriesRival> {

    TournamentSeriesRival findByTournamentSeriesAndTeamProposal(TournamentSeries tournamentSeries,
                                                                TournamentTeamProposal tournamentTeamProposal);
}
