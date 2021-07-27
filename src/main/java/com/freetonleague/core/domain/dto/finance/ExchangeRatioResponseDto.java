package com.freetonleague.core.domain.dto.finance;

import com.freetonleague.core.domain.enums.finance.Currency;
import com.freetonleague.core.domain.enums.finance.CurrencyMarketProviderType;
import com.freetonleague.core.domain.enums.finance.CurrencyPairType;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

import static java.util.Objects.nonNull;

@Data
public class ExchangeRatioResponseDto {

    private CurrencyMarketProviderType provider;

    private Currency currencyToBuy;

    private Currency currencyToSell;

    private Double ratio;

    private CurrencyPairType currencyPairType;

    private Long requestTimeRaw;

    private Object rawData;

    public LocalDateTime getRequestTime() {
        return nonNull(requestTimeRaw) ? LocalDateTime.ofInstant(Instant.ofEpochSecond(requestTimeRaw),
                TimeZone.getDefault().toZoneId()) : null;
    }
}
