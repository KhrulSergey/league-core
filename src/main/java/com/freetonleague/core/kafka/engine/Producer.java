package com.freetonleague.core.kafka.engine;

import com.freetonleague.core.domain.dto.EventDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class Producer {

    private final KafkaTemplate<String, EventDto> kafkaTemplate;

    public Producer(KafkaTemplate<String, EventDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public ListenableFuture<SendResult<String, EventDto>> sendMessage(String topic, String key, EventDto message) {

        return this.kafkaTemplate.send(topic, key, message);
    }
}
