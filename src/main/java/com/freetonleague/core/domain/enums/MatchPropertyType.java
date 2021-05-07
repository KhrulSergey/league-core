package com.freetonleague.core.domain.enums;

/**
 * All Possible match properties
 */
public enum MatchPropertyType {
    // !--- if you are extends class type than update converter MatchPropertyConverter.convertAndValidate() ---!

    GAME_MAP_NAME("game map name for match", IndicatorValueClassType.STRING),
    GAME_LOCATION_NAME("game location for match", IndicatorValueClassType.STRING),
    NPC_MAX_COUNT("max count of NPC", IndicatorValueClassType.INTEGER),
    ;

    private final String description;
    private final IndicatorValueClassType valueClassType;

    MatchPropertyType(String description, IndicatorValueClassType valueClassType) {
        this.description = description;
        this.valueClassType = valueClassType;
    }

    public String getDescription() {
        return description;
    }

    public IndicatorValueClassType getValueClassType() {
        return valueClassType;
    }
}
