package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.AccountInfoDto;
import com.freetonleague.core.domain.enums.UserStatusType;
import com.freetonleague.core.domain.model.User;


public interface UserEventService {

    /**
     * Process user status changing
     */
    AccountInfoDto processUserStatusChange(User user, UserStatusType newUserStatusType);
}
