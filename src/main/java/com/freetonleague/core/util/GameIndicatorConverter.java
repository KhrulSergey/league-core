package com.freetonleague.core.util;

import com.freetonleague.core.domain.enums.GameIndicatorType;
import com.freetonleague.core.exception.ExceptionMessages;
import com.freetonleague.core.exception.GameDisciplineManageException;

import java.util.HashMap;
import java.util.Map;

public class GameIndicatorConverter {

    /**
     * Convert HashMap field entry form DB (from hstore type) to correct type entity
     */
    public static Map<GameIndicatorType, Object> convertAndValidate(Map<?, Object> objectMap) {
        if (objectMap == null) {
            return null;
        }
        HashMap<GameIndicatorType, Object> map = new HashMap<>();
        try {
            objectMap.forEach((key, value) -> {
                //find enum value
                GameIndicatorType keyMap = GameIndicatorType.valueOf(String.valueOf(key));
                Object valueMap = null;
                String entryValue = String.valueOf(value);
                // trying to parse MapValue to specified type from GameIndicatorType->valueType
                if (Boolean.class.equals(keyMap.getValueType())) {
                    valueMap = Boolean.valueOf(entryValue);
                } else if (Integer.class.equals(keyMap.getValueType())) {
                    valueMap = Integer.valueOf(entryValue);
                } else if (Double.class.equals(keyMap.getValueType())) {
                    valueMap = Double.valueOf(entryValue);
                } else if (String.class.equals(keyMap.getValueType())) {
                    valueMap = String.valueOf(entryValue);
                } else if (Long.class.equals(keyMap.getValueType())) {
                    valueMap = Long.valueOf(entryValue);
                }
                map.put(keyMap, valueMap);
            });
        } catch (Exception exc) {
            throw new GameDisciplineManageException(ExceptionMessages.GAME_DISCIPLINE_SETTINGS_CONVERTED_ERROR, exc.getMessage());
        }
        return map;
    }
}
