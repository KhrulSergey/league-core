package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.EventDto;
import com.freetonleague.core.domain.enums.DocketStatusType;
import com.freetonleague.core.domain.model.Docket;


public interface DocketEventService {

    EventDto add(EventDto event);

    /**
     * Process docket status changing
     */
    void processDocketStatusChange(Docket docket, DocketStatusType newDocketStatusType);
}
