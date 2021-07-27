package com.freetonleague.core.mapper.finance;

import com.freetonleague.core.domain.dto.finance.ExchangeOrderDto;
import com.freetonleague.core.domain.dto.finance.ExchangeRatioDto;
import com.freetonleague.core.domain.model.finance.ExchangeOrder;
import com.freetonleague.core.domain.model.finance.ExchangeRatio;
import org.mapstruct.*;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {AccountFinUnitMapper.class, AccountTransactionFinUnitMapper.class})
public interface ExchangeOrderFinUnitMapper {

    @Named(value = "toDto")
    ExchangeRatioDto toDto(ExchangeRatio entity);

    @Mapping(target = "clientAccount", source = "entity.clientAccount", qualifiedByName = "toDto")
    @Mapping(target = "paymentTransaction", source = "entity.paymentTransaction", qualifiedByName = "toDto")
    @Named(value = "toDto")
    ExchangeOrderDto toDto(ExchangeOrder entity);
}
