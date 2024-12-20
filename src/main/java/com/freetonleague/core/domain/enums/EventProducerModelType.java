package com.freetonleague.core.domain.enums;


public enum EventProducerModelType {
    TOURNAMENT("TOURNAMENT"),
    TOURNAMENT_ROUND("TOURNAMENT_ROUND"),
    TOURNAMENT_SERIES("TOURNAMENT_SERIES"),
    TOURNAMENT_MATCH("TOURNAMENT_MATCH"),
    TEAM("TEAM"),
    USER("USER"),
    PRODUCT_PURCHASE("PRODUCT_PURCHASE"),
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
