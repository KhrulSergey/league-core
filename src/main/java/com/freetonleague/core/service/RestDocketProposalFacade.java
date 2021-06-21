package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.DocketUserProposalBonusDto;
import com.freetonleague.core.domain.dto.DocketUserProposalDto;
import com.freetonleague.core.domain.enums.ParticipationStateType;
import com.freetonleague.core.domain.model.DocketUserProposal;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service-facade for managing docket user proposal
 */
public interface RestDocketProposalFacade {


    /**
     * Get user proposal by user and docket
     *
     * @param docketId identify of docket
     * @param leagueId identify of user
     */
    DocketUserProposalDto getProposalByUserAndDocket(long docketId, String leagueId);

    /**
     * Get user proposal list by docket
     *
     * @param docketId identify of docket
     */
    Page<DocketUserProposalDto> getProposalListByDocket(Pageable pageable, long docketId);

    /**
     * Get user proposal list by docket for bonus payments
     *
     * @param accessToken access token to method
     * @param docketId    identify of docket
     */
    List<DocketUserProposalBonusDto> getProposalListByDocketForBonus(String accessToken, long docketId);

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
     * @param userProposalId           identify of user proposal
     * @param currentUserProposalState new status of user proposal
     * @return Modified user proposal
     */
    DocketUserProposalDto editProposalToDocket(Long userProposalId,
                                               ParticipationStateType currentUserProposalState, User currentUser);

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
    DocketUserProposal getVerifiedDocketProposalById(long id);
}


