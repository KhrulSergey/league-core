package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.freetonleague.core.domain.enums.EventOperationType;
import com.freetonleague.core.domain.enums.EventProducerModelType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;

@SuperBuilder
@Data
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDto implements Serializable {

    private String id;

    private String message;

    private EventProducerModelType eventTopic;

    private EventOperationType eventOperationType;

    private String modelId;

    @JsonIgnore
    private Map<String, Object> modelData;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate createdDate;

    public String getTopic() {
        return eventTopic.getTopicName();
    }
}
