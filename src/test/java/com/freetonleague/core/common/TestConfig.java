package com.freetonleague.core.common;

import com.freetonleague.core.domain.dto.EventDto;
import com.freetonleague.core.domain.dto.NotificationDto;
import com.freetonleague.core.service.kafka.MessageProducer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

@TestConfiguration
public class TestConfig {

    @Bean
    public MessageProducer testMessageProducer() {
        return new MessageProducer() {
            @Override
            public ListenableFuture<SendResult<String, EventDto>> sendEventMessage(String topic, EventDto message) {
                throw new UnsupportedOperationException();
            }

            @Override
            public ListenableFuture<SendResult<String, NotificationDto>> sendNotificationMessage(NotificationDto notificationDto) {
                throw new UnsupportedOperationException();
            }
        };
    }

}
