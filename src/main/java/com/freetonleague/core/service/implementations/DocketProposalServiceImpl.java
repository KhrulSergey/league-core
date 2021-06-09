package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.AccountTransactionInfoDto;
import com.freetonleague.core.domain.enums.ParticipationStateType;
import com.freetonleague.core.domain.model.Docket;
import com.freetonleague.core.domain.model.DocketUserProposal;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.repository.DocketUserProposalRepository;
import com.freetonleague.core.service.DocketEventService;
import com.freetonleague.core.service.DocketProposalService;
import com.freetonleague.core.service.FinancialClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Transactional
@Slf4j
@RequiredArgsConstructor
@Service
public class DocketProposalServiceImpl implements DocketProposalService {

    private final DocketUserProposalRepository docketProposalRepository;
    private final FinancialClientService financialClientService;

    @Lazy
    @Autowired
    private DocketEventService docketEventService;

    /**
     * Returns user proposal to docket by id.
     */
    @Override
    public DocketUserProposal getProposalById(long id) {
        log.debug("^ trying to get user proposal to docket by id '{}'", id);
        return docketProposalRepository.findById(id).orElse(null);
    }

    /**
     * Returns user proposal to docket by user and docket.
     */
    @Override
    public DocketUserProposal getProposalByUserAndDocket(User user, Docket docket) {
        if (isNull(user) || isNull(docket)) {
            log.error("!> requesting getProposalByUserAndDocket for NULL user '{}' or NULL docket '{}'. Check evoking clients",
                    user, docket);
            return null;
        }
        log.debug("^ trying to get uer proposal to docket for user: '{}' and docket '{}'",
                user.getId(), docket.getId());
        return docketProposalRepository.findByUserAndDocket(user, docket);
    }

    /**
     * Returns list of all user proposal to docket filtered by requested params
     */
    @Override
    public Page<DocketUserProposal> getProposalListForDocket(Pageable pageable, Docket docket) {
        if (isNull(pageable) || isNull(docket)) {
            log.error("!> requesting getProposalListForDocket for NULL pageable '{}' or NULL docket '{}'. Check evoking clients",
                    pageable, docket);
            return null;
        }
        List<ParticipationStateType> filteredProposalStateList = List.of(ParticipationStateType.values());
        log.debug("^ trying to get user proposal to docket list with pageable params: '{}' and for docket '{}'",
                pageable, docket.getId());
        return docketProposalRepository.findAllByDocketAndStateIn(pageable, docket, filteredProposalStateList);
    }

    /**
     * Returns user proposal to docket.
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public DocketUserProposal addProposal(DocketUserProposal userProposal) {
        if (isNull(userProposal) || isNull(userProposal.getDocket())) {
            log.error("!> requesting addProposal for NULL userProposal '{}' or NULL userProposal.docket. Check evoking clients",
                    userProposal);
            return null;
        }
        Docket docket = userProposal.getDocket();
        if (docket.hasProposalCountLimit() && this.countActiveUserProposalListByDocket(docket) > docket.getMaxProposalCount()) {
            log.error("!> requesting addProposal for docket with proposal's limit exceeded. Check evoking clients");
            return null;
        }
        log.debug("^ trying to add new user proposal to docket '{}'", userProposal);
        List<AccountTransactionInfoDto> paymentList = docketEventService.processDocketUserProposalStateChange(
                userProposal, userProposal.getState());
        userProposal.setParticipatePaymentList(paymentList);
        return docketProposalRepository.save(userProposal);
    }

    /**
     * Edit user proposal to docket in DB.
     */
    @Override
    public DocketUserProposal editProposal(DocketUserProposal userProposal) {
        if (isNull(userProposal)) {
            log.error("!> requesting modify user proposal to docket with editProposal for NULL userProposal. Check evoking clients");
            return null;
        }
        if (!isExistsDocketUserProposalById(userProposal.getId())) {
            log.error("!> requesting modify user proposal to docket or non-existed proposal.id '{}'. Check evoking clients",
                    userProposal.getId());
            return null;
        }
        log.debug("^ trying to modify user proposal to docket '{}'", userProposal);
        if (userProposal.isStateChanged()) {
            this.handleDocketUserProposalStateChanged(userProposal);
        }
        return docketProposalRepository.save(userProposal);
    }

    /**
     * Quit requested user from docket.
     * DocketUserProposal marked as CANCELLED
     */
    @Override
    public DocketUserProposal quitFromDocket(DocketUserProposal userProposal) {
        return null;
    }

    /**
     * Returns list of approved user proposal list for specified docket with bonus-logic filtering.
     */
    @Override
    public List<DocketUserProposal> getProposalListByDocketForBonusService(Docket docket) {
        if (isNull(docket)) {
            log.error("!> requesting getActiveUserProposalListByDocket for NULL docket. Check evoking clients");
            return null;
        }
        log.debug("^ trying to get Approved user proposal list by docket with id: '{}'", docket.getId());
        List<DocketUserProposal> docketUserProposals =
                docketProposalRepository.findAllByDocketAndState(docket, ParticipationStateType.APPROVE);
        // "возвращались те пользователи только, которые не вносили ни разу депозит"
        // delete proposals, whose user made deposit transaction to his account at least once
        docketUserProposals.removeIf(p -> financialClientService.isUserMadeDepositToHisAccount(p.getUser()));
        return docketUserProposals;
    }

    /**
     * Returns count of approved user proposal's list for specified docket.
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public int countActiveUserProposalListByDocket(Docket docket) {
        if (isNull(docket)) {
            log.error("!> requesting countActiveUserProposalListByDocket for NULL docket. Check evoking clients");
            return 0;
        }
        log.debug("^ trying to count Approved user proposal list by docket with id: '{}'", docket.getId());
        return docketProposalRepository.countByDocketAndState(docket, ParticipationStateType.APPROVE);
    }

    /**
     * Returns calculated participation fee for specified user proposal to docket
     */
    @Override
    public double calculateUserParticipationFee(DocketUserProposal userProposal) {
        Docket docket = userProposal.getDocket();
        return nonNull(docket.getParticipationFee()) ? docket.getParticipationFee() : 0.0;
    }

    private boolean isExistsDocketUserProposalById(long id) {
        return docketProposalRepository.existsById(id);
    }

    /**
     * Prototype for handle tournament team proposal state
     */
    private void handleDocketUserProposalStateChanged(DocketUserProposal userProposal) {
        log.warn("~ status for user proposal to docket with id '{}' was changed from '{}' to '{}' ",
                userProposal.getId(), userProposal.getPrevState(), userProposal.getState());
//        tournamentEventService.processTournamentTeamProposalStateChange(tournamentTeamProposal, tournamentTeamProposal.getState());
        userProposal.setPrevState(userProposal.getState());
    }
}
