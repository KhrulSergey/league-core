package com.freetonleague.core.domain.enums;

/**
 * All Possible game indicators
 */
public enum GameIndicatorType {

    // tag::common indicators[]
    FRAG_COUNT("frag count", Integer.class, null),

    FLAG_CAPTURED("flag captured", Boolean.class, null),
    FLAG_CAPTURED_COUNT("flag captured count", Integer.class, null),

    CHECK_POINT_PASSED("check point passed", Boolean.class, null)
    // end::common indicators[]

    ;

    private final String name;
    private final Class<?> valueType;
    private final Long gameDisciplineId;

    GameIndicatorType(String name, Class<?> valueType, Long gameDisciplineId) {
        this.name = name;
        this.valueType = valueType;
        this.gameDisciplineId = gameDisciplineId;
    }

    public String getName() {
        return name;
    }

    public Class<?> getValueType() {
        return valueType;
    }

    public Long getGameDisciplineId() {
        return gameDisciplineId;
    }
}
