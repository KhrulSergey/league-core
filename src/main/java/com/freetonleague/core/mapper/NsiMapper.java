package com.freetonleague.core.mapper;


import com.freetonleague.core.domain.dto.product.ProductPropertyTypeDto;
import com.freetonleague.core.domain.dto.tournament.GameIndicatorTypeDto;
import com.freetonleague.core.domain.dto.tournament.TournamentMatchPropertyTypeDto;
import com.freetonleague.core.domain.enums.MatchPropertyType;
import com.freetonleague.core.domain.enums.product.ProductPropertyType;
import com.freetonleague.core.domain.enums.tournament.GameIndicatorType;
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
    TournamentMatchPropertyTypeDto toDto(MatchPropertyType propertyType);

    @Mapping(target = "name", expression = "java(propertyType.name())")
    ProductPropertyTypeDto toDto(ProductPropertyType propertyType);

    List<GameIndicatorTypeDto> toDto(GameIndicatorType[] indicatorType);

    List<TournamentMatchPropertyTypeDto> toDto(MatchPropertyType[] indicatorType);

    List<ProductPropertyTypeDto> toDto(ProductPropertyType[] indicatorType);
}
