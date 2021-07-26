package com.freetonleague.core.domain.dto.finance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.finance.Currency;
import com.freetonleague.core.domain.enums.finance.CurrencyMarketProviderType;
import com.freetonleague.core.domain.enums.finance.CurrencyPairType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExchangeRatioDto {

    private Long id;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private CurrencyMarketProviderType provider;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private CurrencyPairType currencyPairType;

    private Currency currencyToBuy;

    private Currency currencyToSell;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double ratio;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<ExchangeRatioDto> parentExchangeRatioList;

    private LocalDateTime createdAt;

    private LocalDateTime expiredAt;
}
