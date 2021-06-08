package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.NotificationDto;

public interface NotificationService {

    void sendNotification(NotificationDto notificationDto);
}
