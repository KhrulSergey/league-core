package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.DocketDto;
import com.freetonleague.core.domain.model.Docket;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocketMapper {

    @Named(value = "toDto")
    @Mapping(target = "promoId", source = "promo.id")
    DocketDto toDto(Docket entity);

    Docket fromDto(DocketDto dto);

    @IterableMapping(qualifiedByName = "toDto")
    List<DocketDto> toDto(List<Docket> entities);

}
