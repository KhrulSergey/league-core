package com.freetonleague.core.repository;

import com.freetonleague.core.domain.enums.ParticipationStateType;
import com.freetonleague.core.domain.model.Docket;
import com.freetonleague.core.domain.model.DocketUserProposal;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.web.PageableDefault;

import java.util.List;

public interface DocketUserProposalRepository extends JpaRepository<DocketUserProposal, Long>,
        JpaSpecificationExecutor<DocketUserProposal> {

    DocketUserProposal findByUserAndDocket(User user, Docket docket);

    Page<DocketUserProposal> findAllByDocketAndStateIn(@PageableDefault Pageable pageable,
                                                       Docket docket, List<ParticipationStateType> state);

    Page<DocketUserProposal> findAllByDocketAndState(@PageableDefault Pageable pageable, Docket docket, ParticipationStateType state);

    Integer countByDocketAndState(Docket docket, ParticipationStateType state);
}
