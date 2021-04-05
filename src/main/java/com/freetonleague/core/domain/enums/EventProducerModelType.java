package com.freetonleague.core.domain.enums;


public enum EventProducerModelType {
    TOURNAMENT("TOURNAMENT"),
    TEAM("TEAM"),
    USER("USER");

    private final String topicName;

    EventProducerModelType(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicName() {
        return topicName;
    }
}
