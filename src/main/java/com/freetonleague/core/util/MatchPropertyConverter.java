package com.freetonleague.core.util;

import com.freetonleague.core.domain.dto.tournament.TournamentMatchPropertyDto;
import com.freetonleague.core.exception.GameDisciplineManageException;
import com.freetonleague.core.exception.config.ExceptionMessages;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MatchPropertyConverter {

    /**
     * Verified MatchProperty List field entry form DB (from jsonB type) to correct type entity
     */
    public static List<TournamentMatchPropertyDto> convertAndValidate(List<TournamentMatchPropertyDto> matchPropertyList) {
        if (matchPropertyList == null) {
            return null;
        }
        AtomicReference<TournamentMatchPropertyDto> currentProperty = new AtomicReference<>();
        try {
            matchPropertyList.forEach(matchProperty -> {
                currentProperty.set(matchProperty);
                String entryValue = String.valueOf(matchProperty.getMatchPropertyValue());
                switch (matchProperty.getMatchPropertyType().getValueClassType()) {
                    case BOOLEAN:
                        matchProperty.setMatchPropertyValue(Boolean.valueOf(entryValue));
                        break;
                    case DOUBLE:
                        matchProperty.setMatchPropertyValue(Double.valueOf(entryValue));
                        break;
                    case INTEGER:
                        matchProperty.setMatchPropertyValue(Integer.valueOf(entryValue));
                        break;
                    case LONG:
                        matchProperty.setMatchPropertyValue(Long.valueOf(entryValue));
                        break;
                    case STRING:
                    default:
                        matchProperty.setMatchPropertyValue(entryValue);
                }
            });
        } catch (Exception exc) {
            throw new GameDisciplineManageException(ExceptionMessages.TOURNAMENT_MATCH_PROPERTIES_CONVERTED_ERROR,
                    "Problem occurred with value: ' " + currentProperty + "'. Details: " + exc.getMessage());
        }
        return matchPropertyList;
    }
}
