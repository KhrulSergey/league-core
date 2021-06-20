package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.ProductDto;
import com.freetonleague.core.domain.model.Product;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Named(value = "toDto")
    ProductDto toDto(Product entity);

    Product fromDto(ProductDto dto);

    @IterableMapping(qualifiedByName = "toDto")
    List<ProductDto> toDto(List<Product> entities);
}
