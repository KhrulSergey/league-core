package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.IndicatorValueClassType;
import lombok.Data;

/**
 * Dto for match property (enum) entry
 */
@Data
public class MatchPropertyTypeDto {

    private String name;

    private String description;

    private IndicatorValueClassType valueClassType;
}
