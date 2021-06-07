package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.enums.DocketStatusType;
import com.freetonleague.core.domain.model.Docket;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.repository.DocketRepository;
import com.freetonleague.core.service.DocketEventService;
import com.freetonleague.core.service.DocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Slf4j
@RequiredArgsConstructor
@Service
public class DocketServiceImpl implements DocketService {

    private final DocketRepository docketRepository;
    private final Validator validator;

    @Autowired
    private DocketEventService docketEventService;

    /**
     * Returns founded docket by id
     */
    @Override
    public Docket getDocket(long id) {
        log.debug("^ trying to get docket by id: '{}'", id);
        return docketRepository.findById(id).orElse(null);
    }

    /**
     * Returns list of all dockets filtered by requested params
     */
    @Override
    public Page<Docket> getDocketList(Pageable pageable, User creatorUser, List<DocketStatusType> statusList) {
        if (isNull(pageable)) {
            log.error("!> requesting getDocketList for NULL pageable. Check evoking clients");
            return null;
        }
        log.debug("^ trying to get docket list with pageable params: '{}' and status list '{}'", pageable, statusList);
        boolean filterByStatusEnabled = isNotEmpty(statusList);
        boolean filterByCreatorEnabled = nonNull(creatorUser);

        if (filterByStatusEnabled && filterByCreatorEnabled) {
            return docketRepository.findAllByStatusInAndCreatedBy(pageable, statusList, creatorUser);
        } else if (filterByStatusEnabled) {
            return docketRepository.findAllByStatusIn(pageable, statusList);
        } else if (filterByCreatorEnabled) {
            return docketRepository.findAllByCreatedBy(pageable, creatorUser);
        }
        return docketRepository.findAll(pageable);
    }

    /**
     * Returns list of all Dockets on portal
     */
    @Override
    public List<Docket> getAllActiveDocket() {
        return docketRepository.findAllActive(DocketStatusType.activeStatusList);
    }

    /**
     * Add new Docket to DB.
     */
    @Override
    public Docket addDocket(Docket docket) {
        if (!this.verifyDocket(docket)) {
            return null;
        }
        log.debug("^ trying to add new docket '{}'", docket);
        docket = docketRepository.save(docket);
        docketEventService.processDocketStatusChange(docket, docket.getStatus());
        return docket;
    }

    /**
     * Edit docket in DB.
     */
    @Override
    public Docket editDocket(Docket docket) {
        if (!this.verifyDocket(docket)) {
            return null;
        }
        if (!this.isExistsDocketById(docket.getId())) {
            log.error("!> requesting modify docket.id '{}' and name '{}' for non-existed docket. Check evoking clients", docket.getId(), docket.getName());
            return null;
        }
        log.debug("^ trying to modify docket '{}'", docket);

        if (docket.getStatus().isFinished()) {
            docket.setFinishedDate(LocalDateTime.now());
            // if docket was automatically finished by EventService (not manually-forced)
        }
        if (docket.isStatusChanged()) {
            this.handleDocketStatusChanged(docket);
        }
        return docketRepository.save(docket);
    }

    /**
     * Mark 'deleted' docket in DB.
     */
    @Override
    public Docket deleteDocket(Docket docket) {
        if (!this.verifyDocket(docket)) {
            return null;
        }
        if (!this.isExistsDocketById(docket.getId())) {
            log.error("!> requesting delete docket for non-existed docket. Check evoking clients");
            return null;
        }
        log.debug("^ trying to set 'deleted' mark to docket '{}'", docket);
        docket.setStatus(DocketStatusType.DELETED);
        docket = docketRepository.save(docket);
        this.handleDocketStatusChanged(docket);
        return docket;
    }

    /**
     * Returns sign of docket existence for specified id.
     */
    @Override
    public boolean isExistsDocketById(long id) {
        return docketRepository.existsById(id);

    }

    /**
     * Validate docket parameters and settings to modify
     */
    private boolean verifyDocket(Docket docket) {
        if (isNull(docket)) {
            log.error("!> requesting modify docket with verifyDocket for NULL docket. Check evoking clients");
            return false;
        }
        Set<ConstraintViolation<Docket>> violations = validator.validate(docket);
        if (!violations.isEmpty()) {
            log.error("!> requesting modify docket id '{}' name '{}' with verifyDocket for docket with ConstraintViolations. Check evoking clients",
                    docket.getId(), docket.getName());
            return false;
        }
        return true;
    }

    /**
     * Prototype for handle docket status
     */
    private void handleDocketStatusChanged(Docket docket) {
        log.warn("~ status for docket id '{}' was changed from '{}' to '{}' ",
                docket.getId(), docket.getPrevStatus(), docket.getStatus());
        docket.setPrevStatus(docket.getStatus());
    }
}
