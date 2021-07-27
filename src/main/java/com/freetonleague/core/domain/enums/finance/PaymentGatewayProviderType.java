package com.freetonleague.core.domain.enums.finance;

public enum PaymentGatewayProviderType {
    YOO_KASSA("https://yookassa.ru"),
    BROXUS_PAY("https://broxus.com");

    private final String paymentGateWayUrl;

    PaymentGatewayProviderType(String paymentGateWayUrl) {
        this.paymentGateWayUrl = paymentGateWayUrl;
    }

    public String getGatewayUrl() {
        return paymentGateWayUrl;
    }
}
