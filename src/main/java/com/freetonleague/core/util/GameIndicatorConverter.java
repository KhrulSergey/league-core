package com.freetonleague.core.util;

import com.freetonleague.core.domain.dto.GameDisciplineIndicatorDto;
import com.freetonleague.core.exception.GameDisciplineManageException;
import com.freetonleague.core.exception.config.ExceptionMessages;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class GameIndicatorConverter {

    /**
     * Verified GameDisciplineIndicator List field entry form DB (from jsonB type) to correct type entity
     */
    public static List<GameDisciplineIndicatorDto> convertAndValidate(List<GameDisciplineIndicatorDto> gameDisciplineIndicatorList) {
        if (gameDisciplineIndicatorList == null) {
            return null;
        }
        AtomicReference<GameDisciplineIndicatorDto> currentIndicator = new AtomicReference<>();
        try {
            gameDisciplineIndicatorList.forEach(gameIndicator -> {
                currentIndicator.set(gameIndicator);
                String entryValue = String.valueOf(gameIndicator.getGameIndicatorValue());
                switch (gameIndicator.getGameIndicatorType().getValueClassType()) {
                    case BOOLEAN:
                        gameIndicator.setGameIndicatorValue(Boolean.valueOf(entryValue));
                        break;
                    case DOUBLE:
                        gameIndicator.setGameIndicatorValue(Double.valueOf(entryValue));
                        break;
                    case INTEGER:
                        gameIndicator.setGameIndicatorValue(Integer.valueOf(entryValue));
                        break;
                    case LONG:
                        gameIndicator.setGameIndicatorValue(Long.valueOf(entryValue));
                        break;
                    case STRING:
                    default:
                        gameIndicator.setGameIndicatorValue(entryValue);
                }
            });
        } catch (Exception exc) {
            throw new GameDisciplineManageException(ExceptionMessages.GAME_DISCIPLINE_SETTINGS_CONVERTED_ERROR,
                    "Problem occurred with value: ' " + currentIndicator + "'. Details: " + exc.getMessage());
        }
        return gameDisciplineIndicatorList;
    }
}
