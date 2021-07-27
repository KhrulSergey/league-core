package com.freetonleague.core.domain.enums.finance;

public enum AccountTransactionTemplateType {
    DEFAULT,
    PRODUCT_PURCHASE,
    TOURNAMENT_ENTRANCE_FEE,
    TOURNAMENT_ENTRANCE_COMMISSION,
    TOURNAMENT_QUIT_PENALTY,
    DOCKET_ENTRANCE_FEE,
    EXTERNAL_BANK,
    PAYMENT_GATEWAY, //aka FIAT_DEPOSIT OR/AND CURRENCY_EXCHANGE
    EXTERNAL_PROVIDER,
}