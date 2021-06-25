package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.ProductPurchaseDto;
import com.freetonleague.core.domain.dto.ProductPurchaseNotificationDto;
import com.freetonleague.core.domain.model.ProductPurchase;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserMapper.class})
public interface ProductPurchaseMapper {


    @Named(value = "toDto")
    @Mapping(target = "productId", source = "entity.product.id")
    @Mapping(target = "leagueId", expression = "java(entity.getUser().getLeagueId().toString())")
    ProductPurchaseDto toDto(ProductPurchase entity);

    @Mapping(target = "username", source = "entity.user.username")
    @Mapping(target = "leagueId", expression = "java(entity.getUser().getLeagueId().toString())")
    @Mapping(target = "productId", source = "entity.product.id")
    @Mapping(target = "productName", source = "entity.product.name")
    @Mapping(target = "purchaseState", source = "entity.state")
    ProductPurchaseNotificationDto toNotification(ProductPurchase entity);

    ProductPurchase fromDto(ProductPurchaseDto dto);

    @Named(value = "toDtoList")
    @IterableMapping(qualifiedByName = "toDto")
    List<ProductPurchaseDto> toDto(List<ProductPurchase> entity);
}
