package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.DocketDto;
import com.freetonleague.core.domain.enums.DocketStatusType;
import com.freetonleague.core.domain.model.Docket;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.DocketManageException;
import com.freetonleague.core.exception.ExceptionMessages;
import com.freetonleague.core.exception.TournamentManageException;
import com.freetonleague.core.exception.ValidationException;
import com.freetonleague.core.mapper.DocketMapper;
import com.freetonleague.core.security.permissions.CanManageDocket;
import com.freetonleague.core.service.DocketService;
import com.freetonleague.core.service.RestDocketFacade;
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
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@RequiredArgsConstructor
@Service
public class RestDocketFacadeImpl implements RestDocketFacade {

    private final DocketService docketService;
    private final DocketMapper docketMapper;
    private final RestUserFacade restUserFacade;
    private final Validator validator;

    /**
     * Returns founded docket by id
     */
    @Override
    public DocketDto getDocket(long id) {
        return docketMapper.toDto(this.getVerifiedDocketById(id));
    }

    /**
     * Returns list of all teams filtered by requested params with detailed info
     */
    @Override
    public Page<DocketDto> getDocketList(Pageable pageable, String creatorLeagueId, List<DocketStatusType> statusList) {
        User creatorUser = null;
        if (!isBlank(creatorLeagueId)) {
            creatorUser = restUserFacade.getVerifiedUserByLeagueId(creatorLeagueId);
        }
        return docketService.getDocketList(pageable, creatorUser, statusList).map(docketMapper::toDto);
    }

    /**
     * Add new docket to DB.
     */
    @CanManageDocket
    @Override
    public DocketDto addDocket(DocketDto docketDto) {
        docketDto.setId(null);
        docketDto.setStatus(DocketStatusType.CREATED);

        Docket docket = this.getVerifiedDocketByDto(docketDto);
        docket = docketService.addDocket(docket);

        if (isNull(docket)) {
            log.error("!> error while creating docket from dto {}.", docketDto);
            throw new DocketManageException(ExceptionMessages.DOCKET_CREATION_ERROR,
                    "Docket was not saved on Portal. Check requested params.");
        }
        return docketMapper.toDto(docket);
    }

    /**
     * Edit docket in DB.
     */
    @CanManageDocket
    @Override
    public DocketDto editDocket(DocketDto docketDto) {
        Docket modifiedDocket = this.getVerifiedDocketByDto(docketDto);

        if (isNull(docketDto.getId())) {
            log.warn("~ parameter 'docket.id' is not set for editDocket");
            throw new ValidationException(ExceptionMessages.DOCKET_VALIDATION_ERROR, "docket id",
                    "parameter 'docket id' is not set for editTournament");
        }

        if (docketDto.getStatus().isDeleted()) {
            log.warn("~ docket deleting was declined in editDocket. This operation should be done with specific method.");
            throw new DocketManageException(ExceptionMessages.DOCKET_STATUS_DELETE_ERROR,
                    "Modifying docket was rejected. Check requested params and method.");
        }

        modifiedDocket = docketService.editDocket(modifiedDocket);
        if (isNull(modifiedDocket)) {
            log.error("!> error while modifying tournament from dto {}.", docketDto);
            throw new DocketManageException(ExceptionMessages.DOCKET_MODIFICATION_ERROR,
                    "Tournament was not updated on Portal. Check requested params.");
        }
        return docketMapper.toDto(modifiedDocket);
    }

    /**
     * Delete docket in DB.
     */
    @CanManageDocket
    @Override
    public DocketDto deleteDocket(long id) {
        Docket docket = this.getVerifiedDocketById(id);
        docket = docketService.deleteDocket(docket);

        if (isNull(docket)) {
            log.error("!> error while deleting docket with id {}.", id);
            throw new TournamentManageException(ExceptionMessages.DOCKET_MODIFICATION_ERROR,
                    "Docket was not deleted on Portal. Check requested params.");
        }
        return docketMapper.toDto(docket);
    }

    /**
     * Getting docket by id and user with privacy check
     */
    @Override
    public Docket getVerifiedDocketById(long id) {
        Docket docket = docketService.getDocket(id);
        if (isNull(docket)) {
            log.debug("^ Docket with requested id {} was not found. 'getVerifiedDocketById' in RestDocketFacadeImpl request denied", id);
            throw new DocketManageException(ExceptionMessages.TOURNAMENT_NOT_FOUND_ERROR, "Tournament with requested id " + id + " was not found");
        }
        if (docket.getStatus().isDeleted()) {
            log.debug("^ Docket with requested id {} was {}. 'getVerifiedDocketById' in RestDocketFacadeImpl request denied", id, docket.getStatus());
            throw new DocketManageException(ExceptionMessages.TOURNAMENT_VISIBLE_ERROR, "Visible docket with requested id " + id + " was not found");
        }
        return docket;
    }

    /**
     * Getting docket by DTO with deep validation and privacy check
     */
    private Docket getVerifiedDocketByDto(DocketDto docketDto) {
        // Verify Docket information
        Set<ConstraintViolation<DocketDto>> violations = validator.validate(docketDto);
        if (!violations.isEmpty()) {
            log.debug("^ transmitted DocketDto: {} have constraint violations: {}", docketDto, violations);
            throw new ConstraintViolationException(violations);
        }
        return docketMapper.fromDto(docketDto);
    }
}
