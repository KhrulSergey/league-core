package com.freetonleague.core.service;


import com.freetonleague.core.domain.enums.DocketStatusType;
import com.freetonleague.core.domain.model.Docket;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DocketService {

    /**
     * Returns founded docket by id
     *
     * @param id of docket to search
     * @return docket entity
     */
    Docket getDocket(long id);

    /**
     * Returns list of all dockets filtered by requested params
     *
     * @param pageable   filtered params to search docket
     * @param statusList filtered params to search docket
     * @return list of dockets entities
     */
    Page<Docket> getDocketList(Pageable pageable, User creatorUser, List<DocketStatusType> statusList);

    /**
     * Returns list of all Dockets on portal
     *
     * @return list of Dockets entities
     */
    List<Docket> getAllActiveDocket();

    /**
     * Add new Docket to DB.
     *
     * @param docket to be added
     * @return Added Docket
     */
    Docket addDocket(Docket docket);

    /**
     * Edit docket in DB.
     *
     * @param docket to be edited
     * @return Edited docket
     */
    Docket editDocket(Docket docket);

    /**
     * Mark 'deleted' docket in DB.
     *
     * @param docket to be deleted
     * @return docket with updated fields and deleted status
     */
    Docket deleteDocket(Docket docket);

    /**
     * Returns sign of docket existence for specified id.
     *
     * @param id for which docket will be find
     * @return true if docket exists, false - if not
     */
    boolean isExistsDocketById(long id);
}
