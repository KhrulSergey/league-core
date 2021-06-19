package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.AccountTransactionInfoDto;
import com.freetonleague.core.domain.enums.DocketStatusType;
import com.freetonleague.core.domain.enums.ParticipationStateType;
import com.freetonleague.core.domain.model.Docket;
import com.freetonleague.core.domain.model.DocketUserProposal;

import java.util.List;


public interface DocketEventService {

    /**
     * Process docket status changing
     */
    void processDocketStatusChange(Docket docket, DocketStatusType newDocketStatusType);

    /**
     * Process user proposal to docket status changing
     */
    List<AccountTransactionInfoDto> processDocketUserProposalStateChange(DocketUserProposal docketUserProposal,
                                                                         ParticipationStateType newUserProposalState);
}
