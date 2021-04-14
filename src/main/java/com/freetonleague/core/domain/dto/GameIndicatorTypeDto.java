package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.GameIndicatorValueClassType;
import lombok.Data;

/**
 * Dto for game indicators entry
 */
@Data
public class GameIndicatorTypeDto {

    private String name;

    private String description;

    private GameIndicatorValueClassType valueClassType;

    private Long gameDisciplineId;
}
