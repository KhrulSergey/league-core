package com.freetonleague.core.domain.enums;


public enum EventProducerModelType {
    TOURNAMENT("TOURNAMENT"),
    TEAM("TEAM"),
    USER("USER"),
    TEST_DATA("TEST_DATA"),
    INPUT_DATA("INPUT_DATA");

    private final String topicName;

    EventProducerModelType(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicName() {
        return topicName;
    }
}
