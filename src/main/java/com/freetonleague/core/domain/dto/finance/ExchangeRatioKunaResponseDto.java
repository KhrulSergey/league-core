package com.freetonleague.core.domain.dto.finance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.finance.CurrencyPairType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@SuperBuilder
@NoArgsConstructor
@Data
public class ExchangeRatioKunaResponseDto implements Serializable {

    @JsonIgnore
    private String currencyPairTypeCode;

    @JsonIgnore
    private CurrencyPairType currencyPairType;

    @JsonProperty(value = "at")
    private Long requestTimeRaw;

    @JsonProperty(value = "ticker")
    private ExchangeRatioKunaTickerDto exchangeRatioTicker;
}
