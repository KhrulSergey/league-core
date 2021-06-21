package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.IndicatorValueClassType;
import com.freetonleague.core.domain.enums.IndicatorValueMultiplicityType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Dto for match property (enum) entry
 */
@Data
public class ProductPropertyTypeDto {

    private final String russianDescription;
    @ApiModelProperty(notes = "Possible type of indicator for product parameter description")
    private final IndicatorValueMultiplicityType valueMultiplicityType;
    @ApiModelProperty(notes = "Possible type of indicator for selected parameter in product purchase")
    private final IndicatorValueMultiplicityType selectedValueMultiplicityType;
    private String name;
    private String description;
    private IndicatorValueClassType valueClassType;
}
