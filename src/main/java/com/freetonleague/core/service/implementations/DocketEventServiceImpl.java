package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.EventDto;
import com.freetonleague.core.domain.enums.DocketStatusType;
import com.freetonleague.core.domain.model.Docket;
import com.freetonleague.core.service.DocketEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DocketEventServiceImpl implements DocketEventService {
    @Override
    public EventDto add(EventDto event) {
        return null;
    }

    /**
     * Process docket status changing
     */
    @Override
    public void processDocketStatusChange(Docket docket, DocketStatusType newDocketStatusType) {

    }
}
