package com.freetonleague.core.domain.enums;

/**
 * All Possible game indicators
 */
public enum GameIndicatorType {
    // !--- if you are extends class type than update converter GameIndicatorConverter.convertAndValidate() ---!

    // tag::common indicators[]
    FRAG_COUNT("frag count", IndicatorValueClassType.INTEGER, null),
    FLAG_CAPTURED("flag captured", IndicatorValueClassType.BOOLEAN, null),
    FLAG_CAPTURED_COUNT("flag captured count", IndicatorValueClassType.DOUBLE, null),
    CHECK_POINT_PASSED("check point passed", IndicatorValueClassType.BOOLEAN, null),
    KEY_WORD_DETECTED("found key word", IndicatorValueClassType.STRING, null)
    // end::common indicators[]

    ;

    private final String description;
    private final IndicatorValueClassType valueClassType;
    private final Long gameDisciplineId;

    GameIndicatorType(String description, IndicatorValueClassType valueClassType, Long gameDisciplineId) {
        this.description = description;
        this.valueClassType = valueClassType;
        this.gameDisciplineId = gameDisciplineId;
    }

    public String getDescription() {
        return description;
    }

    public IndicatorValueClassType getValueClassType() {
        return valueClassType;
    }

    public Long getGameDisciplineId() {
        return gameDisciplineId;
    }
}
