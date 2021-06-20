package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.ProductPropertyType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ProductPropertyDto implements Serializable {

    private static final long serialVersionUID = 1377506770579106009L;

    private ProductPropertyType productPropertyType;

    private boolean required = false;

    private Object productPropertyValue;
}
