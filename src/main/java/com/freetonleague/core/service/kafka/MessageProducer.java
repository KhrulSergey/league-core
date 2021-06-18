package com.freetonleague.core.service.kafka;

import com.freetonleague.core.domain.dto.EventDto;
import com.freetonleague.core.domain.dto.NotificationDto;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

public interface MessageProducer {

    ListenableFuture<SendResult<String, EventDto>> sendEventMessage(String topic, EventDto message);

    ListenableFuture<SendResult<String, NotificationDto>> sendNotificationMessage(NotificationDto notificationDto);

}
