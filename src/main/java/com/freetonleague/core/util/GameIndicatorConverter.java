package com.freetonleague.core.util;

import com.freetonleague.core.domain.enums.GameIndicatorType;
import com.freetonleague.core.exception.ExceptionMessages;
import com.freetonleague.core.exception.GameDisciplineManageException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class GameIndicatorConverter {

    /**
     * Convert HashMap field entry form DB (from hstore type) to correct type entity
     */
    public static Map<GameIndicatorType, Object> convertAndValidate(Map<?, Object> objectMap) {
        if (objectMap == null) {
            return null;
        }
        HashMap<GameIndicatorType, Object> map = new HashMap<>();
        AtomicReference<String> currentMap = new AtomicReference<>();
        try {
            objectMap.forEach((key, value) -> {
                //find enum value
                GameIndicatorType keyMap = GameIndicatorType.valueOf(String.valueOf(key));
                Object valueMap;
                String entryValue = String.valueOf(value);
                currentMap.set("type: ".concat(keyMap.name()).concat(", value:").concat(entryValue));
                // trying to parse MapValue to specified type from GameIndicatorType->valueType
                switch (keyMap.getValueClassType()) {
                    case BOOLEAN:
                        valueMap = Boolean.valueOf(entryValue);
                        break;
                    case DOUBLE:
                        valueMap = Double.valueOf(entryValue);
                        break;
                    case INTEGER:
                        valueMap = Integer.valueOf(entryValue);
                        break;
                    case LONG:
                        valueMap = Long.valueOf(entryValue);
                        break;
                    case STRING:
                    default:
                        valueMap = entryValue;
                }
                map.put(keyMap, valueMap);
            });
        } catch (Exception exc) {
            throw new GameDisciplineManageException(ExceptionMessages.GAME_DISCIPLINE_SETTINGS_CONVERTED_ERROR, "Problem occurred with value: ' " + currentMap + "'. Details: " + exc.getMessage());
        }
        return map;
    }
}
