package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.NotificationPublicDto;

import java.util.List;
import java.util.UUID;

public interface RestNotificationFacade {
    boolean createMassNotification(NotificationPublicDto notificationDto, List<UUID> leagueIdList);
}
