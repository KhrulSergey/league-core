package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.DocketUserProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DocketUserProposalRepository extends JpaRepository<DocketUserProposal, Long>,
        JpaSpecificationExecutor<DocketUserProposal> {


//    TournamentTeamProposal findByTeamAndTournament(Team team, Tournament tournament);
//
//    Page<TournamentTeamProposal> findAllByTournamentAndStateIn(@PageableDefault Pageable pageable, Tournament tournament, List<ParticipationStateType> state);
//
//    List<TournamentTeamProposal> findAllByTournamentAndState(Tournament tournament, ParticipationStateType state);
}
