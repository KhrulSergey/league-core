package com.freetonleague.core.service.docket;

import com.freetonleague.core.domain.dto.finance.AccountTransactionInfoDto;
import com.freetonleague.core.domain.enums.ParticipationStateType;
import com.freetonleague.core.domain.enums.docket.DocketStatusType;
import com.freetonleague.core.domain.model.docket.Docket;
import com.freetonleague.core.domain.model.docket.DocketUserProposal;

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
