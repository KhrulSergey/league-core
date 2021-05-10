package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.DocketUserProposalDto;
import com.freetonleague.core.domain.enums.DocketStatusType;
import com.freetonleague.core.domain.enums.ParticipationStateType;
import com.freetonleague.core.domain.model.Docket;
import com.freetonleague.core.domain.model.DocketUserProposal;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.*;
import com.freetonleague.core.mapper.DocketProposalMapper;
import com.freetonleague.core.security.permissions.CanManageDocket;
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
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class RestDocketProposalFacadeImpl implements RestDocketProposalFacade {

    private final RestDocketFacade restDocketFacade;
    private final RestUserFacade restUserFacade;
    private final DocketProposalService docketProposalService;
    private final DocketProposalMapper docketProposalMapper;
    private final Validator validator;

    /**
     * Get currentUser proposal for docket
     */
    @Override
    public DocketUserProposalDto getProposalFromUserForDocket(long docketId, String leagueId) {
        User user = restUserFacade.getVerifiedUserByLeagueId(leagueId);
        Docket docket = restDocketFacade.getVerifiedDocketById(docketId);
        DocketUserProposal docketUserProposal = docketProposalService.getProposalByUserAndDocket(user, docket);
        return docketProposalMapper.toDto(docketUserProposal);
    }

    /**
     * Get currentUser proposal list for docket
     */
    @Override
    public Page<DocketUserProposalDto> getProposalListForDocket(Pageable pageable, long docketId) {
        Docket docket = restDocketFacade.getVerifiedDocketById(docketId);
        return docketProposalService.getProposalListForDocket(pageable, docket).map(docketProposalMapper::toDto);
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
            log.warn("~ forbiddenException for create proposal to docket.id {} for user.leagueId {} from user {}.",
                    userProposalDto.getDocketId(), userProposalDto.getLeagueId(), currentUser);
            throw new DocketManageException(ExceptionMessages.DOCKET_USER_PROPOSAL_CREATION_ERROR,
                    "User can apply to docket only by himself. Session user not equals specified leagueId.");
        }

        //check if proposal already existed
        DocketUserProposal existedUserProposal = docketProposalService.getProposalByUserAndDocket(
                newUserProposal.getUser(), newUserProposal.getDocket());
        if (nonNull(existedUserProposal)) {
            log.warn("~ forbiddenException for create duplicate proposal from user {}. Already existed proposal.id {}.",
                    newUserProposal.getUser(), existedUserProposal.getId());
            throw new DocketManageException(ExceptionMessages.DOCKET_USER_PROPOSAL_EXIST_ERROR,
                    "Duplicate proposal from user to the one docket is prohibited. Request rejected.");
        }

        //check status of tournament
        if (!DocketStatusType.activeStatusList.contains(newUserProposal.getDocket().getStatus())) {
            log.warn("~ forbiddenException for create new proposal for user {} to docket.id {} with status {}. " +
                            "Docket is closed for new proposals",
                    userProposalDto.getLeagueId(), newUserProposal.getDocket().getId(), newUserProposal.getDocket().getStatus());
            throw new DocketManageException(ExceptionMessages.DOCKET_USER_PROPOSAL_VERIFICATION_ERROR,
                    String.format("Docket '%s' is closed for new proposals and have status '%s'. Request rejected.",
                            newUserProposal.getDocket().getId(), newUserProposal.getDocket().getStatus()));
        }

        if (newUserProposal.getDocket().hasTextLabel() && !newUserProposal.hasTextLabelAnswer()) {
            log.warn("~ parameter 'textLabelAnswer' is not set for proposal to createProposalToDocket");
            throw new ValidationException(ExceptionMessages.DOCKET_USER_PROPOSAL_VALIDATION_ERROR, "textLabelAnswer",
                    "parameter textLabelAnswer is required for createProposalToDocket");
        }

        //save proposal
        newUserProposal = docketProposalService.addProposal(newUserProposal);
        if (isNull(newUserProposal)) {
            log.error("!> error while creating user proposal to docket by user.id {} to docket.id {}.",
                    userProposalDto.getDocketId(), userProposalDto.getDocketId());
            throw new DocketManageException(ExceptionMessages.DOCKET_USER_PROPOSAL_CREATION_ERROR,
                    "Team proposal was not saved on Portal. Check requested params.");
        }
        return docketProposalMapper.toDto(newUserProposal);
    }

    /**
     * Edit currentUser proposal to docket (only state)
     */
    @CanManageDocket
    @Override
    public DocketUserProposalDto editProposalToDocket(Long docketId, String leagueId, Long userProposalId, ParticipationStateType currentUserProposalState, User currentUser) {
        //check if user is org
        DocketUserProposal userProposal;

        if (nonNull(userProposalId)) {
            userProposal = this.getVerifiedUserProposalById(userProposalId);
        } else if (nonNull(leagueId) && nonNull(docketId)) {
            User user = restUserFacade.getVerifiedUserByLeagueId(leagueId);
            Docket docket = restDocketFacade.getVerifiedDocketById(docketId);
            userProposal = docketProposalService.getProposalByUserAndDocket(user, docket);
        } else {
            log.warn("~ forbiddenException for modify proposal to docket for user {}. " +
                    "No valid parameters of user proposal to docket was specified", userProposalId);
            throw new TeamParticipantManageException(ExceptionMessages.DOCKET_USER_PROPOSAL_VERIFICATION_ERROR,
                    "No valid parameters of user proposal to docket was specified. Request rejected.");
        }
        if (isNull(userProposal)) {
            log.debug("^ User proposal to docket with requested parameters docketId {}, leagueId {}, userProposalId {} was not found. " +
                    "'editProposalToDocket' in RestDocketProposalFacade request denied", docketId, leagueId, userProposalId);
            throw new TeamManageException(ExceptionMessages.DOCKET_USER_PROPOSAL_NOT_FOUND_ERROR, "User proposal to docket with requested id " + userProposalId + " was not found");
        }

        userProposal.setState(currentUserProposalState);
        DocketUserProposal savedUserProposal = docketProposalService.editProposal(userProposal);
        if (isNull(savedUserProposal)) {
            log.error("!> error while modifying user proposal to docket {}.", userProposal);
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
    public DocketUserProposal getVerifiedUserProposalById(long id) {
        DocketUserProposal docketUserProposal = docketProposalService.getProposalById(id);
        if (isNull(docketUserProposal)) {
            log.debug("^ User proposal to docket with requested id {} was not found. 'getVerifiedTeamProposalById' in RestTournamentTeamFacadeImpl request denied", id);
            throw new DocketManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_NOT_FOUND_ERROR, "Tournament team proposal  with requested id " + id + " was not found");
        }
        //TODO check logic and make decision about need in restrict proposal modification for orgs
//        if (docketUserProposal.getState().isRejected()) {
//            log.debug("^ Docket user proposal with requested id {} was rejected by orgs. " +
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
            log.debug("^ transmitted DocketUserProposalDto: {} have constraint violations: {}", userProposalDto, violations);
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
