package com.freetonleague.core.domain.dto.finance;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@SuperBuilder
@NoArgsConstructor
@Data
public class ExchangeRatioKunaTickerDto implements Serializable {

    @JsonProperty(value = "buy")
    private Double bidPrice;

    @JsonProperty(value = "sell")
    private Double askPrice;

    @JsonProperty(value = "price")
    private Double priceChangeInQuotedCurrencyBy24hr;

    @JsonProperty(value = "last")
    private Double lastPrice;

    @JsonProperty(value = "vol")
    private Double tradingVolumeInBaseCurrencyBy24hr;

    @JsonProperty(value = "high")
    private Double maximumPriceIn24hr;

    @JsonProperty(value = "low")
    private Double minimumPriceFor24hr;
}
