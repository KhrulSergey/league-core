package com.freetonleague.core.repository.tournament;

import com.freetonleague.core.domain.model.tournament.TournamentTeamParticipant;
import com.freetonleague.core.domain.model.tournament.TournamentTeamProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface TournamentTeamParticipantRepository extends JpaRepository<TournamentTeamParticipant, Long>,
        JpaSpecificationExecutor<TournamentTeamParticipant> {

    @Query(value = "select s.user.discordId from TournamentTeamParticipant s where s.tournamentTeamProposal = :teamProposal")
    Set<String> findUserDiscordIdListForTournamentTeamProposal(@Param("teamProposal") TournamentTeamProposal tournamentTeamProposal);

}
