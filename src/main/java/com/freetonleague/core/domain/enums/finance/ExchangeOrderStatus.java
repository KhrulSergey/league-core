package com.freetonleague.core.domain.enums.finance;

public enum ExchangeOrderStatus {

    OPEN("Open", 0),
    FINISHED("Finished", 1),
    FROZEN("Frozen", 2),
    CANCELED("Canceled", 3);

    private final String name;
    private final int value;

    ExchangeOrderStatus(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public boolean isOpen() {
        return this == OPEN;
    }
}
