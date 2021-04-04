package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.EventOperationType;
import com.freetonleague.core.domain.enums.EventProducerModelType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@SuperBuilder
@Data
@RequiredArgsConstructor
public class EventDto implements Serializable {

    private String id;

    private String message;

    private EventProducerModelType eventTopic;

    private EventOperationType eventOperationType;

    private String modelId;

    private Map<String, Object> modelData;

    private LocalDateTime createdDate;

    public String getTopic() {
        return eventTopic.getTopicName();
    }
}
