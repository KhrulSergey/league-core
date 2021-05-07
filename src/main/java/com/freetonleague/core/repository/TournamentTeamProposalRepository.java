package com.freetonleague.core.repository;

import com.freetonleague.core.domain.enums.ParticipationStateType;
import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.domain.model.TournamentTeamProposal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.web.PageableDefault;

import java.util.List;

public interface TournamentTeamProposalRepository extends JpaRepository<TournamentTeamProposal, Long>,
        JpaSpecificationExecutor<TournamentTeamProposal> {


    TournamentTeamProposal findByTeamAndTournament(Team team, Tournament tournament);

    Page<TournamentTeamProposal> findAllByTournamentAndStateIn(@PageableDefault Pageable pageable, Tournament tournament, List<ParticipationStateType> state);

    List<TournamentTeamProposal> findAllByTournamentAndState(Tournament tournament, ParticipationStateType state);
}
