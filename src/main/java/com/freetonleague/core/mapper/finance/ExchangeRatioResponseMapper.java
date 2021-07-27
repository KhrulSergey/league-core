package com.freetonleague.core.mapper.finance;

import com.freetonleague.core.domain.dto.finance.ExchangeRatioBroxusResponseDto;
import com.freetonleague.core.domain.dto.finance.ExchangeRatioKunaResponseDto;
import com.freetonleague.core.domain.dto.finance.ExchangeRatioResponseDto;
import com.freetonleague.core.domain.enums.finance.CurrencyMarketProviderType;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {CurrencyMarketProviderType.class})
public interface ExchangeRatioResponseMapper {

    @Mapping(target = "provider", expression = "java(CurrencyMarketProviderType.BROXUS)")
    @Mapping(target = "currencyToBuy", source = "rawData.to")
    @Mapping(target = "currencyToSell", source = "rawData.from")
    @Mapping(target = "ratio", source = "rawData.rate")
    @Mapping(target = "rawData", expression = "java((Object)rawData)")
    ExchangeRatioResponseDto fromRaw(ExchangeRatioBroxusResponseDto rawData);

    @Mapping(target = "provider", expression = "java(CurrencyMarketProviderType.KUNA)")
    @Mapping(target = "currencyToBuy", expression = "java(rawData.getCurrencyPairType().getCurrencyToBuy())")
    @Mapping(target = "currencyToSell", expression = "java(rawData.getCurrencyPairType().getCurrencyToSell())")
    @Mapping(target = "rawData", expression = "java((Object)rawData)")
    ExchangeRatioResponseDto fromRaw(ExchangeRatioKunaResponseDto rawData);
}
