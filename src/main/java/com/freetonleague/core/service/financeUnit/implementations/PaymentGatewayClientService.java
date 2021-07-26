package com.freetonleague.core.service.financeUnit.implementations;

import com.freetonleague.core.domain.dto.finance.PaymentInvoiceDto;
import com.freetonleague.core.domain.enums.finance.PaymentGatewayProviderType;
import com.freetonleague.core.service.financeUnit.cloud.BroxusAccountingClientCloud;
import com.freetonleague.core.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

/**
 * Service-client for interact with Third-party Payment Gateway
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentGatewayClientService {

    private final BroxusAccountingClientCloud broxusAccountingClientCloud;

    @Value("${config.gateway-client.token:}")
    private String gatewayClientToken;

    @Value("${config.gateway-client.debug:true}")
    private Boolean isGatewayClientMock;

    /**
     * Returns new external bank account for specified account
     */
    public PaymentInvoiceDto createPaymentInvoice(PaymentInvoiceDto paymentInvoiceRequest) {
        if (isNull(paymentInvoiceRequest) || isNull(paymentInvoiceRequest.getProductGUID())) {
            log.error("!> requesting createPaymentInvoice for NULL paymentInvoiceRequest '{}' or " +
                    "NULL paymentInvoiceRequest.productGUID '{}'. Check evoking clients", paymentInvoiceRequest, null);
            return null;
        }
        return PaymentInvoiceDto.builder()
                .gatewayType(PaymentGatewayProviderType.YOO_KASSA)
                .paymentUrl(PaymentGatewayProviderType.YOO_KASSA.getGatewayUrl()
                        + "/?product=" + paymentInvoiceRequest.getProductGUID()
                        + "&hash=" + StringUtil.generateRandomName())
                .amount(paymentInvoiceRequest.getAmount())
                .description(paymentInvoiceRequest.getDescription())
                .productGUID(paymentInvoiceRequest.getProductGUID())
                .build();
    }
}
