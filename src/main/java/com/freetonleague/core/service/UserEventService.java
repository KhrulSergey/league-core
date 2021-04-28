package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.EventDto;
import com.freetonleague.core.domain.enums.UserStatusType;
import com.freetonleague.core.domain.model.User;


public interface UserEventService {

    EventDto add(EventDto event);

    /**
     * Process user status changing
     */
    void processUserStatusChange(User user, UserStatusType newUserStatusType);
}
