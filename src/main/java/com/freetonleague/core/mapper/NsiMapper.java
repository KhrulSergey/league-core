package com.freetonleague.core.mapper;


import com.freetonleague.core.domain.dto.GameIndicatorTypeDto;
import com.freetonleague.core.domain.dto.MatchPropertyTypeDto;
import com.freetonleague.core.domain.enums.GameIndicatorType;
import com.freetonleague.core.domain.enums.MatchPropertyType;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NsiMapper {

    @Mapping(target = "name", expression = "java(indicatorType.name())")
    GameIndicatorTypeDto toDto(GameIndicatorType indicatorType);

    @Mapping(target = "name", expression = "java(propertyType.name())")
    MatchPropertyTypeDto toDto(MatchPropertyType propertyType);

    List<GameIndicatorTypeDto> toDto(GameIndicatorType[] indicatorType);

    List<MatchPropertyTypeDto> toDto(MatchPropertyType[] indicatorType);
}
