package com.freetonleague.core.service.kafka;

import com.freetonleague.core.domain.dto.EventDto;
import com.freetonleague.core.domain.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageProducer {

    private final KafkaTemplate<String, NotificationDto> notificationKafkaTemplate;

    private final KafkaTemplate<String, EventDto> eventKafkaTemplate;

    @Value(value = "${config.kafka.notifications.topic-name}")
    private String notificationTopicName;

    @Value(value = "${config.kafka.bootstrapAddress}")
    private String kafkaServer;

    public ListenableFuture<SendResult<String, EventDto>> sendEventMessage(String topic, EventDto message) {
        log.warn("~ kafka server is: {}", kafkaServer);
        return eventKafkaTemplate.send(topic, message);
    }

    public ListenableFuture<SendResult<String, NotificationDto>> sendNotificationMessage(NotificationDto notificationDto) {
        return notificationKafkaTemplate.send(notificationTopicName, notificationDto);
    }
}
