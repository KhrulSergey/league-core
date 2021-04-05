package com.freetonleague.core.service.implementations;


import com.freetonleague.core.domain.dto.EventDto;
import com.freetonleague.core.service.EventService;
import com.freetonleague.core.service.kafka.Producer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {

    private final Producer producer;

    @Override
    public EventDto processEvent(EventDto event) {
        log.info("! handle add EventDto");
        return null;
    }

    @Override
    public void sendEvent(EventDto eventDto) throws ExecutionException, InterruptedException {
        ListenableFuture<SendResult<String, EventDto>> listenableFuture = this.producer.sendMessage(eventDto.getTopic(), "IN_KEY", eventDto);

        SendResult<String, EventDto> result = listenableFuture.get();
        log.info("Produced: event id {} message: {} \ntopic: {}, offset: {}, partition: {}, value size: {}",
                eventDto.getId(),
                eventDto.getMessage(),
                eventDto.getTopic(), result.getRecordMetadata().offset(),
                result.getRecordMetadata().partition(), result.getRecordMetadata().serializedValueSize());
    }
}
