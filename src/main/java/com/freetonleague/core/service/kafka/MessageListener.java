package com.freetonleague.core.service.kafka;


import com.freetonleague.core.domain.dto.EventDto;
import com.freetonleague.core.domain.dto.NotificationDto;
import com.freetonleague.core.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {

    private final EventService eventService;

    @KafkaListener(topics = {"TOURNAMENT", "TOURNAMENT_SERIES", "TOURNAMENT_MATCH", "USER_CORE", "TEAM"},
            containerFactory = "eventKafkaListenerContainerFactory")
    public void eventsListener(final @Payload EventDto eventDto,
                               final @Header(KafkaHeaders.OFFSET) Integer offset,
                               final @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                               final @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts) {
        log.debug("#### -> Consumed message -> TIMESTAMP: '{}', Message:'{}'\noffset: '{}', topic: '{}'",
                ts, eventDto, offset, topic);
    }

    @KafkaListener(topics = "!(#kafkaProperties.notifications.topicName)",
            containerFactory = "notificationKafkaListenerContainerFactory",
            autoStartup = "!(#kafkaProperties.notifications.startDebug)")
    public void notificationListener(final @Payload NotificationDto notificationDto,
                                     final @Header(KafkaHeaders.OFFSET) Integer offset,
                                     final @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                     final @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts) {
        log.warn("Received notification message: {}", notificationDto);
        log.debug("#### -> Consumed message -> TIMESTAMP: '{}', Message:'{}'\noffset: '{}', topic: '{}'",
                ts, notificationDto, offset, topic);
    }

    //TODO delete until 01/10/21
//    @KafkaListener(topics = {"TOURNAMENT", "TOURNAMENT_SERIES", "TOURNAMENT_MATCH", "USER", "TEAM"}, groupId = "core")
//    public void handleEvent(final @Payload EventDto data,
//                            final @Header(KafkaHeaders.OFFSET) Integer offset,
//                            final @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
//                            final @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
//                            final @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
//                            final @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts,
//                            final Acknowledgment acknowledgment
//    ) {
//        log.debug("#### -> Consumed message -> TIMESTAMP: '{}', Message:'{}'\noffset: '{}', key: '{}', partition: '{}' topic: '{}'",
//                ts, data, offset, key, partition, topic);
//        acknowledgment.acknowledge();
//        eventService.processEvent(data);
//    }
}
