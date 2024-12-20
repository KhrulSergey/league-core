package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.EventDto;

import java.util.concurrent.ExecutionException;


public interface EventService {

    EventDto processEvent(EventDto event);

    void sendEvent(EventDto eventDto) throws ExecutionException, InterruptedException;
}
