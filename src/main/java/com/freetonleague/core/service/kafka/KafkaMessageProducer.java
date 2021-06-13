package com.freetonleague.core.service.kafka;

import com.freetonleague.core.config.properties.KafkaProperties;
import com.freetonleague.core.domain.dto.EventDto;
import com.freetonleague.core.domain.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessageProducer implements MessageProducer {

    private final KafkaTemplate<String, NotificationDto> notificationKafkaTemplate;
    private final KafkaTemplate<String, EventDto> eventKafkaTemplate;
    private final KafkaProperties kafkaProperties;

    @Override
    public ListenableFuture<SendResult<String, EventDto>> sendEventMessage(String topic, EventDto message) {
        log.warn("~ kafka server is: {}", kafkaProperties.getBootstrapAddress());
        return eventKafkaTemplate.send(topic, message);
    }

    @Override
    public ListenableFuture<SendResult<String, NotificationDto>> sendNotificationMessage(NotificationDto notificationDto) {
        return notificationKafkaTemplate.send(kafkaProperties.getNotifications().getTopicName(), notificationDto);
    }

}
