package com.freetonleague.core.domain.dto.finance;

import com.freetonleague.core.domain.enums.finance.PaymentGatewayProviderType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@SuperBuilder
@NoArgsConstructor
@Data
public class PaymentInvoiceDto {

   private PaymentGatewayProviderType gatewayType;

   private String paymentUrl;

   private Double amount;

   private String description;

   private UUID productGUID;
}
