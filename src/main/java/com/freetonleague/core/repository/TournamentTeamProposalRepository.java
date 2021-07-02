package com.freetonleague.core.repository;

import com.freetonleague.core.domain.enums.ParticipationStateType;
import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.domain.model.TournamentTeamProposal;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;

import java.util.List;

public interface TournamentTeamProposalRepository extends JpaRepository<TournamentTeamProposal, Long>,
        JpaSpecificationExecutor<TournamentTeamProposal> {


    TournamentTeamProposal findByTeamAndTournament(Team team, Tournament tournament);

    Page<TournamentTeamProposal> findAllByTournamentAndStateIn(@PageableDefault Pageable pageable,
                                                               Tournament tournament, List<ParticipationStateType> state);

    Page<TournamentTeamProposal> findAllByTournamentAndConfirmedAndStateIn(@PageableDefault Pageable pageable, Tournament tournament,
                                                                           boolean confirmed, List<ParticipationStateType> state);

    List<TournamentTeamProposal> findAllByTournamentAndStateIn(Tournament tournament, List<ParticipationStateType> state);

    @Query(value = "select p from TournamentTeamProposal p where p.tournament = :tournament and p.confirmed = true " +
            "and p.state = com.freetonleague.core.domain.enums.ParticipationStateType.APPROVE")
    List<TournamentTeamProposal> findAllApprovedProposalsByTournament(@Param("tournament") Tournament tournament);

    @Query(value = "select p from TournamentTeamProposal p where p.tournament = :tournament and p.team.captain.user = :userCapitan")
    List<TournamentTeamProposal> findProposalByUserCapitanAndTournament(@Param("userCapitan") User userCapitan,
                                                                        @Param("tournament") Tournament tournament);
}
