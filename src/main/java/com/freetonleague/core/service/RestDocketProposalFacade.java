package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.DocketUserProposalDto;
import com.freetonleague.core.domain.enums.ParticipationStateType;
import com.freetonleague.core.domain.model.DocketUserProposal;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service-facade for managing docket user proposal
 */
public interface RestDocketProposalFacade {


    /**
     * Get user proposal for docket
     *
     * @param docketId identify of docket
     * @param leagueId identify of user
     */
    DocketUserProposalDto getProposalFromUserForDocket(long docketId, String leagueId);

    /**
     * Get user proposal list for docket
     *
     * @param docketId identify of docket
     */
    Page<DocketUserProposalDto> getProposalListForDocket(Pageable pageable, long docketId);

    /**
     * Registry new user to docket
     *
     * @param userProposalDto Team proposal data to be added
     * @param user            current user from session
     * @return Added user proposal
     */
    DocketUserProposalDto createProposalToDocket(DocketUserProposalDto userProposalDto, User user);

    /**
     * Edit user proposal to docket (only state)
     *
     * @param docketId          identify of docket
     * @param leagueId          identify of user
     * @param userProposalId    identify of user proposal
     * @param userProposalState new status of user proposal
     * @return Modified user proposal
     */
    DocketUserProposalDto editProposalToDocket(Long docketId, String leagueId, Long userProposalId,
                                               ParticipationStateType userProposalState, User user);

    /**
     * Quit user from docket
     *
     * @param docketId identify of docket
     * @param leagueId identify of user
     * @param user     current user from Session
     */
    void quitFromDocket(long docketId, long leagueId, User user);

    /**
     * Returns docket user proposal by id and user with privacy check
     */
    DocketUserProposal getVerifiedUserProposalById(long id);
}


