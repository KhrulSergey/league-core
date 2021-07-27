package com.freetonleague.core.repository.tournament;

import com.freetonleague.core.domain.model.tournament.TournamentMatch;
import com.freetonleague.core.domain.model.tournament.TournamentMatchRival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TournamentMatchRivalRepository extends JpaRepository<TournamentMatchRival, Long>,
        JpaSpecificationExecutor<TournamentMatchRival> {

    List<TournamentMatchRival> findAllByTournamentMatch(TournamentMatch tournamentMatch);

    @Query(value = "select case when count(r)> 0 then true else false end " +
            "from TournamentMatchRival r where r.tournamentMatch = :match "
            + "and r.status = com.freetonleague.core.domain.enums.tournament.TournamentMatchRivalParticipantStatusType.ACTIVE "
            + "and :leagueId in (select p.tournamentTeamParticipant.user.leagueId from TournamentMatchRivalParticipant p where p.tournamentMatchRival = r)"
    )
    boolean isUserParticipateInMatch(@Param("match") TournamentMatch tournamentMatch, @Param("leagueId") UUID leagueId);

}
