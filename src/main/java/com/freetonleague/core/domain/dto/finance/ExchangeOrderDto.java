package com.freetonleague.core.domain.dto.finance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.finance.Currency;
import com.freetonleague.core.domain.enums.finance.ExchangeOrderStatus;
import com.freetonleague.core.domain.enums.finance.PaymentGatewayProviderType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExchangeOrderDto {

    private Long id;

    private Currency currencyToBuy;

    private Double amountToBuy;

    private Currency currencyToSell;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double amountToSell;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ExchangeRatioDto exchangeRatio;

    private ExchangeOrderStatus status;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String paymentUrl;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private PaymentGatewayProviderType paymentGateway;

    private AccountInfoDto clientAccount;

    private AccountTransactionInfoDto paymentTransaction;

    private LocalDateTime createdAt;

    private LocalDateTime expiredAt;

    private LocalDateTime finishedAt;
}
