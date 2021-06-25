package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.ProductPropertyType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ProductPropertyDto implements Serializable {

    private static final long serialVersionUID = 1377506770579106009L;

    @ApiModelProperty(required = true)
    private ProductPropertyType productPropertyType;

    @ApiModelProperty(required = true)
    private boolean required = false;

    @ApiModelProperty(required = true)
    private Object productPropertyValue;
}
