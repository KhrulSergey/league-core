package com.freetonleague.core.mapper;


import com.freetonleague.core.domain.dto.GameIndicatorTypeDto;
import com.freetonleague.core.domain.enums.GameIndicatorType;
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

    List<GameIndicatorTypeDto> toDto(GameIndicatorType[] indicatorType);
}
