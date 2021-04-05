package com.freetonleague.core.service.kafka;

import com.freetonleague.core.domain.dto.EventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@RequiredArgsConstructor
@Service
public class Producer {

    private final KafkaTemplate<String, EventDto> kafkaTemplate;

    public ListenableFuture<SendResult<String, EventDto>> sendMessage(String topic, String key, EventDto message) {

        return this.kafkaTemplate.send(topic, key, message);
    }
}
