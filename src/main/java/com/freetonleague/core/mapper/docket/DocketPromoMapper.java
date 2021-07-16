package com.freetonleague.core.mapper.docket;

import com.freetonleague.core.domain.filter.DocketPromoCreationFilter;
import com.freetonleague.core.domain.model.docket.DocketPromoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DocketPromoMapper {

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "usages")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "promoCode", source = "generatedCode")
    DocketPromoEntity fromFilter(DocketPromoCreationFilter filter, String generatedCode);

}
