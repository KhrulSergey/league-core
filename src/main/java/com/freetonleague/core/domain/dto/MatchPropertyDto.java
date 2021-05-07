package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.MatchPropertyType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class MatchPropertyDto implements Serializable {

    private static final long serialVersionUID = 270113932224274376L;

    private MatchPropertyType matchPropertyType;

    private Object matchPropertyValue;
}
