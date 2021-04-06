package com.freetonleague.core.domain.enums;

/**
 * All Possible game indicators
 */
public enum GameIndicatorType {
    // !--- if you are extends class type than update converter GameIndicatorConverter.convertAndValidate() ---!

    // tag::common indicators[]
    FRAG_COUNT("frag count", GameIndicatorValueClassType.INTEGER, null),
    FLAG_CAPTURED("flag captured", GameIndicatorValueClassType.BOOLEAN, null),
    FLAG_CAPTURED_COUNT("flag captured count", GameIndicatorValueClassType.DOUBLE, null),
    CHECK_POINT_PASSED("check point passed", GameIndicatorValueClassType.BOOLEAN, null),
    KEY_WORD_DETECTED("found key word", GameIndicatorValueClassType.STRING, null)
    // end::common indicators[]

    ;

    private final String description;
    private final GameIndicatorValueClassType valueClassType;
    private final Long gameDisciplineId;

    GameIndicatorType(String description, GameIndicatorValueClassType valueClassType, Long gameDisciplineId) {
        this.description = description;
        this.valueClassType = valueClassType;
        this.gameDisciplineId = gameDisciplineId;
    }

    public String getDescription() {
        return description;
    }

    public GameIndicatorValueClassType getValueClassType() {
        return valueClassType;
    }

    public Long getGameDisciplineId() {
        return gameDisciplineId;
    }
}
