package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.TournamentTeamProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TournamentTeamProposalRepository extends JpaRepository<TournamentTeamProposal, Long>,
        JpaSpecificationExecutor<TournamentTeamProposal> {

}
