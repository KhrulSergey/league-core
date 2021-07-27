package com.freetonleague.core.service.docket;

import com.freetonleague.core.domain.dto.docket.DocketDto;
import com.freetonleague.core.domain.enums.docket.DocketStatusType;
import com.freetonleague.core.domain.model.docket.Docket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service-facade for managing dockets
 */
public interface RestDocketFacade {

    /**
     * Returns founded docket by id
     *
     * @param id of docket to search
     * @return docket entity
     */
    DocketDto getDocket(long id);

    /**
     * Returns list of all teams filtered by requested params with detailed info
     *
     * @param pageable        filtered params to search docket
     * @param creatorLeagueId filter params
     * @param statusList      filter params
     * @return list of team entities
     */
    Page<DocketDto> getDocketList(Pageable pageable, String creatorLeagueId, List<DocketStatusType> statusList);

    /**
     * Add new docket to DB.
     *
     * @param docketDto to be added
     * @return Added docket
     */
    DocketDto addDocket(DocketDto docketDto);

    /**
     * Edit docket in DB.
     *
     * @param docketDto to be edited
     * @return Edited docket
     */
    DocketDto editDocket(DocketDto docketDto);

    /**
     * Delete docket in DB.
     *
     * @param id of docket to search
     * @return deleted docket
     */
    DocketDto deleteDocket(long id);


    /**
     * Getting docket by id and user with privacy check
     */
    Docket getVerifiedDocketById(long id);
}
