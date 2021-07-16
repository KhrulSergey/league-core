package com.freetonleague.core.repository.tournament;

import com.freetonleague.core.domain.model.tournament.TournamentSeries;
import com.freetonleague.core.domain.model.tournament.TournamentSeriesRival;
import com.freetonleague.core.domain.model.tournament.TournamentTeamProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;


public interface TournamentSeriesRivalRepository extends JpaRepository<TournamentSeriesRival, Long>,
        JpaSpecificationExecutor<TournamentSeriesRival> {

    TournamentSeriesRival findByTournamentSeriesAndTeamProposal(TournamentSeries tournamentSeries,
                                                                TournamentTeamProposal tournamentTeamProposal);

    @Query(value = "select case when count(r)> 0 then true else false end " +
            "from TournamentSeriesRival r where r.tournamentSeries = :series "
            + "and r.status = com.freetonleague.core.domain.enums.TournamentMatchRivalParticipantStatusType.ACTIVE "
            + "and :leagueId in ((select p.user.leagueId from TournamentTeamParticipant p where p.tournamentTeamProposal = r.teamProposal))"
    )
    boolean isUserParticipateInSeries(@Param("series") TournamentSeries tournamentSeries, @Param("leagueId") UUID leagueId);
}
