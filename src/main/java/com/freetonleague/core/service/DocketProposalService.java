package com.freetonleague.core.service;


import com.freetonleague.core.domain.model.Docket;
import com.freetonleague.core.domain.model.DocketUserProposal;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DocketProposalService {

    /**
     * Returns user proposal to docket by id.
     *
     * @param id of docket proposal to search
     * @return user docket proposal entity
     */
    DocketUserProposal getProposalById(long id);

    /**
     * Returns user proposal to docket by user and docket.
     *
     * @param user   of proposal to search
     * @param docket info to search proposal
     * @return docket proposal from user
     */
    List<DocketUserProposal> getProposalByUserAndDocket(User user, Docket docket);

    /**
     * Returns list of all user proposal to docket filtered by requested params
     *
     * @param pageable filtered params to search docket proposals
     * @param docket   params to search docket proposals
     * @return list of docket proposals
     */
    Page<DocketUserProposal> getProposalListForDocket(Pageable pageable, Docket docket);

    /**
     * Returns user proposal to docket.
     *
     * @param userProposal data to be saved id DB
     * @return new user proposal to docket
     */
    DocketUserProposal addProposal(DocketUserProposal userProposal);

    /**
     * Edit user proposal to docket in DB.
     *
     * @param userProposal to be edited
     * @return Edited user proposal to docket
     */
    DocketUserProposal editProposal(DocketUserProposal userProposal);

    /**
     * Quit requested user from docket.
     * DocketUserProposal marked as CANCELLED
     *
     * @param userProposal changed user proposal to docket
     */
    DocketUserProposal quitFromDocket(DocketUserProposal userProposal);

    /**
     * Returns list of approved user proposal list for specified docket with bonus-logic filtering.
     */
    List<DocketUserProposal> getProposalListByDocketForBonusService(Docket docket);

    /**
     * Returns count of approved user proposal's list for specified docket.
     */
    int countActiveUserProposalListByDocket(Docket docket);

    /**
     * Returns calculated participation fee for specified user proposal to docket
     */
    double calculateUserParticipationFee(DocketUserProposal userProposal);
}
