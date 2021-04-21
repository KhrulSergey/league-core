package com.freetonleague.core.repository;

import com.freetonleague.core.domain.enums.TournamentTeamStateType;
import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.domain.model.TournamentTeamProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TournamentTeamProposalRepository extends JpaRepository<TournamentTeamProposal, Long>,
        JpaSpecificationExecutor<TournamentTeamProposal> {


    List<TournamentTeamProposal> findAllByTournamentAndState(Tournament tournament, TournamentTeamStateType state);
}
