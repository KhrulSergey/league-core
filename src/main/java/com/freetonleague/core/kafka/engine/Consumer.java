package com.freetonleague.core.kafka.engine;


import com.freetonleague.core.domain.dto.EventDto;
import com.freetonleague.core.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
@ConditionalOnProperty(value = "example.kafka.consumer-enabled", havingValue = "true")
public class Consumer {

    private final EventService eventService;
    private final String[] kafkaTopicList;


    @KafkaListener(topics = "TOURNAMENT")
    public void handleEvent(final @Payload EventDto data,
                            final @Header(KafkaHeaders.OFFSET) Integer offset,
                            final @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                            final @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                            final @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                            final @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts,
                            final Acknowledgment acknowledgment
    ) {
        log.debug("#### -> Consumed message -> TIMESTAMP: {}, Message:{}\noffset: {}, key: {}, partition: {} topic: {}",
                ts, data, offset, key, partition, topic);
        acknowledgment.acknowledge();
        eventService.processEvent(data);
    }
}

