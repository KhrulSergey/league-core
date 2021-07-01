package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.DocketUserProposalBonusDto;
import com.freetonleague.core.domain.dto.DocketUserProposalDto;
import com.freetonleague.core.domain.enums.DocketStatusType;
import com.freetonleague.core.domain.enums.ParticipationStateType;
import com.freetonleague.core.domain.model.Docket;
import com.freetonleague.core.domain.model.DocketUserProposal;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.DocketManageException;
import com.freetonleague.core.exception.ValidationException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.mapper.DocketProposalMapper;
import com.freetonleague.core.security.permissions.CanManageDepositFinUnit;
import com.freetonleague.core.security.permissions.CanManageDocket;
import com.freetonleague.core.service.DocketPromoService;
import com.freetonleague.core.service.DocketProposalService;
import com.freetonleague.core.service.RestDocketFacade;
import com.freetonleague.core.service.RestDocketProposalFacade;
import com.freetonleague.core.service.RestUserFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Slf4j
@RequiredArgsConstructor
@Service
public class RestDocketProposalFacadeImpl implements RestDocketProposalFacade {

    private final RestDocketFacade restDocketFacade;
    private final RestUserFacade restUserFacade;
    private final DocketProposalService docketProposalService;
    private final DocketProposalMapper docketProposalMapper;
    private final DocketPromoService docketPromoService;
    private final Validator validator;

    /**
     * Get currentUser proposal for docket
     */
    @Override
    public DocketUserProposalDto getProposalByUserAndDocket(long docketId, String leagueId) {
        User user = restUserFacade.getVerifiedUserByLeagueId(leagueId);
        Docket docket = restDocketFacade.getVerifiedDocketById(docketId);
        List<DocketUserProposal> docketUserProposalList = docketProposalService.getProposalByUserAndDocket(user, docket);
        DocketUserProposal docketUserProposal = isNotEmpty(docketUserProposalList) ? docketUserProposalList.get(0) : null;
        return docketProposalMapper.toDto(docketUserProposal);
    }

    /**
     * Get currentUser proposal list for docket
     */
    @Override
    public Page<DocketUserProposalDto> getProposalListByDocket(Pageable pageable, long docketId) {
        Docket docket = restDocketFacade.getVerifiedDocketById(docketId);
        return docketProposalService.getProposalListForDocket(pageable, docket).map(docketProposalMapper::toDto);
    }

    /**
     * Get user proposal list by docket for bonus payments
     */
    @Override
    @CanManageDepositFinUnit
    public List<DocketUserProposalBonusDto> getProposalListByDocketForBonus(String accessToken, long docketId) {
        Docket docket = restDocketFacade.getVerifiedDocketById(docketId);
        List<DocketUserProposal> docketUserProposals = docketProposalService.getProposalListByDocketForBonusService(docket);
        return docketProposalMapper.toBonusDto(docketUserProposals);
    }

    /**
     * Registry new currentUser to docket
     */
    @Override
    public DocketUserProposalDto createProposalToDocket(DocketUserProposalDto userProposalDto,
                                                        User currentUser) {

        userProposalDto.setId(null);
        userProposalDto.setState(ParticipationStateType.APPROVE);
        DocketUserProposal newUserProposal = this.getVerifiedUserProposalByDto(userProposalDto);

        if (!newUserProposal.getUser().equals(currentUser)) {
            log.warn("~ forbiddenException for create proposal to docket.id '{}' for user.leagueId '{}' from user '{}'.",
                    userProposalDto.getDocketId(), userProposalDto.getLeagueId(), currentUser);
            throw new DocketManageException(ExceptionMessages.DOCKET_USER_PROPOSAL_CREATION_ERROR,
                    "User can apply to docket only by himself. Session user not equals specified leagueId.");
        }

        Docket docket = newUserProposal.getDocket();

        //check for proposal duplicates
        if (docket.getSystemType().isProposalDuplicatesProhibited()) {
            List<DocketUserProposal> existedUserProposal = docketProposalService.getProposalByUserAndDocket(
                    newUserProposal.getUser(), docket);
            if (isNotEmpty(existedUserProposal)) {
                log.warn("~ forbiddenException for create duplicate proposal from user '{}'. Already existed proposal.id '{}'.",
                        newUserProposal.getUser(), existedUserProposal.get(0).getId());
                throw new DocketManageException(ExceptionMessages.DOCKET_USER_PROPOSAL_EXIST_ERROR,
                        "Duplicate proposal from user to the one docket is prohibited. Request rejected.");
            }
        }

        //check status of docket
        if (!DocketStatusType.activeStatusList.contains(docket.getStatus())) {
            log.warn("~ forbiddenException for create new proposal for user '{}' to docket.id '{}' with status '{}'. " +
                            "Docket is closed for new proposals",
                    userProposalDto.getLeagueId(), docket.getId(), docket.getStatus());
            throw new DocketManageException(ExceptionMessages.DOCKET_USER_PROPOSAL_VERIFICATION_ERROR,
                    String.format("Docket '%s' is closed for new proposals and have status '%s'. Request rejected.",
                            docket.getId(), docket.getStatus()));
        }

        //check exceed max proposal count to docket
        if (docket.hasProposalCountLimit()) {
            int docketProposalCount = docketProposalService.countActiveUserProposalListByDocket(docket);
            log.warn("~ For docket '{}' with limit '{}' we have proposalList.size '{}', from repo we have proposal count '{}', " +
                            "proposal size is '{}' equal",
                    docket.getId(), docket.getMaxProposalCount(), docket.getUserProposalList().size(),
                    docketProposalCount, docketProposalCount == docket.getUserProposalList().size());
            if (docketProposalCount > docket.getMaxProposalCount()) {
                log.warn("~ forbiddenException for create new proposal for user '{}' to docket.id '{}'. " +
                                "User proposal's to Docket can't be created. Docket proposal's limit exceeded",
                        userProposalDto.getLeagueId(), docket.getId());
                throw new DocketManageException(ExceptionMessages.DOCKET_USER_PROPOSAL_LIMIT_EXCEED_ERROR,
                        String.format("Docket '%s' is closed for new proposals because limit '%s' is exceeded. Request rejected.",
                                docket.getId(), docket.getMaxProposalCount()));
            }
        }

        //validate and define participant fee amount
        Double defaultParticipantFee = docket.getParticipationFee();
        if (docket.getSystemType().isCustomParticipantFeeEnabled()) {
            Double customParticipantFee = newUserProposal.getParticipationFee();
            if (customParticipantFee < defaultParticipantFee || customParticipantFee > docket.getMaxParticipationFee()) {
                log.debug("^ forbiddenException for create new proposal for user '{}' to docket.id '{}'. " +
                                "Specified participant fee '{}' in user's proposal to Docket exceed limits from {} to {}",
                        userProposalDto.getLeagueId(), docket.getId(), customParticipantFee, defaultParticipantFee,
                        docket.getMaxParticipationFee());
                throw new ValidationException(ExceptionMessages.DOCKET_USER_PROPOSAL_VALIDATION_ERROR, "participationFee",
                        "parameter 'participationFee' is exceed Docket participation fee limits ");
            }
        } else {
            newUserProposal.setParticipationFee(defaultParticipantFee);
        }

        if (docket.hasTextLabel() && !newUserProposal.hasTextLabelAnswer()) {
            log.warn("~ parameter 'textLabelAnswer' is not set for proposal to createProposalToDocket");
            throw new ValidationException(ExceptionMessages.DOCKET_USER_PROPOSAL_VALIDATION_ERROR, "textLabelAnswer",
                    "parameter textLabelAnswer is required for createProposalToDocket");
        }

        if (docket.getPromo() != null) {
            try {
                docketPromoService.usePromo(userProposalDto.getPromoCode(), currentUser);
            } catch (Exception e) {
                log.warn("~ parameter 'promoCode' is invalid");
                throw new ValidationException(ExceptionMessages.DOCKET_USER_PROPOSAL_VALIDATION_ERROR, "promo",
                        "parameter 'promoCode' is invalid");
            }
        }

        //save proposal
        newUserProposal = docketProposalService.addProposal(newUserProposal);
        if (isNull(newUserProposal)) {
            log.error("!> error while creating user proposal to docket by user.id '{}' to docket.id '{}'.",
                    userProposalDto.getDocketId(), userProposalDto.getDocketId());
            throw new DocketManageException(ExceptionMessages.DOCKET_USER_PROPOSAL_CREATION_ERROR,
                    "Docket proposal was not saved on Portal. Check requested params.");
        }
        return docketProposalMapper.toDto(newUserProposal);
    }

    /**
     * Edit currentUser proposal to docket (only state)
     */
    @CanManageDocket
    @Override
    public DocketUserProposalDto editProposalToDocket(Long userProposalId, ParticipationStateType currentUserProposalState, User currentUser) {
        //check if user is org
        DocketUserProposal userProposal = this.getVerifiedDocketProposalById(userProposalId);

        if (isNull(userProposal)) {
            log.debug("^ User proposal to docket with requested parameters userProposalId '{}' was not found. " +
                    "'editProposalToDocket' in RestDocketProposalFacade request denied", userProposalId);
            throw new DocketManageException(ExceptionMessages.DOCKET_USER_PROPOSAL_NOT_FOUND_ERROR, "User proposal to docket with requested id " + userProposalId + " was not found");
        }

        userProposal.setState(currentUserProposalState);
        DocketUserProposal savedUserProposal = docketProposalService.editProposal(userProposal);
        if (isNull(savedUserProposal)) {
            log.error("!> error while modifying user proposal to docket '{}'.", userProposal);
            throw new DocketManageException(ExceptionMessages.DOCKET_USER_PROPOSAL_MODIFICATION_ERROR,
                    "User proposal to docket was not saved on Portal. Check requested params.");
        }
        return docketProposalMapper.toDto(savedUserProposal);
    }

    /**
     * Quit currentUser from docket
     */
    @Override
    public void quitFromDocket(long docketId, long leagueId, User currentUser) {
        //Not implemented
    }

    /**
     * Returns docket currentUser proposal by id and currentUser with privacy check
     */
    @Override
    public DocketUserProposal getVerifiedDocketProposalById(long id) {
        DocketUserProposal docketUserProposal = docketProposalService.getProposalById(id);
        if (isNull(docketUserProposal)) {
            log.debug("^ User proposal to docket with requested id '{}' was not found. 'getVerifiedDocketProposalById' in RestTournamentDocketFacadeImpl request denied", id);
            throw new DocketManageException(ExceptionMessages.DOCKET_USER_PROPOSAL_NOT_FOUND_ERROR, "Docket proposal with requested id " + id + " was not found");
        }
        //TODO check logic and make decision about need in restrict proposal modification for orgs
        // or delete until 01/10/21
//        if (docketUserProposal.getState().isRejected()) {
//            log.debug("^ Docket user proposal with requested id '{}' was rejected by orgs. " +
//                            "'getVerifiedUserProposalById' in RestDocketProposalFacade request denied", id);
//            throw new DocketManageException(ExceptionMessages.DOCKET_USER_PROPOSAL_VISIBLE_ERROR,
//                    "Docket user proposal with requested id " + id + " was rejected by orgs. Modifying request denied");
//        }
        return docketUserProposal;
    }

    /**
     * Getting docket by DTO with deep validation and privacy check
     */
    private DocketUserProposal getVerifiedUserProposalByDto(DocketUserProposalDto userProposalDto) {
        // Verify Docket information
        Set<ConstraintViolation<DocketUserProposalDto>> violations = validator.validate(userProposalDto);
        if (!violations.isEmpty()) {
            log.debug("^ transmitted DocketUserProposalDto: '{}' have constraint violations: '{}'", userProposalDto, violations);
            throw new ConstraintViolationException(violations);
        }
        DocketUserProposal docketUserProposal = docketProposalMapper.fromDto(userProposalDto);

        User user = restUserFacade.getVerifiedUserByLeagueId(userProposalDto.getLeagueId());
        Docket docket = restDocketFacade.getVerifiedDocketById(userProposalDto.getDocketId());
        docketUserProposal.setDocket(docket);
        docketUserProposal.setUser(user);

        return docketUserProposal;
    }
}
