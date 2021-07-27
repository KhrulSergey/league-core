package com.freetonleague.core.service.financeUnit;

import com.freetonleague.core.domain.enums.finance.Currency;
import com.freetonleague.core.domain.model.finance.ExchangeOrder;
import com.freetonleague.core.domain.model.finance.ExchangeRatio;

import java.util.UUID;

public interface FinancialExchangeCurrencyService {
    /**
     * Calculate rates for specified currency by request new info from external provider
     */
    ExchangeRatio getExchangeCurrencyRate(Currency currencyToBuy, Currency currencyToSell);

    /**
     * Returns found exchange order by guid
     */
    ExchangeOrder getExchangeOrderByGuid(UUID orderGUID);

    /**
     * Save exchange currency order with setting additional system settings
     */
    ExchangeOrder createExchangeOrder(ExchangeOrder exchangeOrder);

    /**
     * Approve exchange currency order with creating transaction of fund
     *
     * @return ExchangeOrder with status FINISHED and embedded transaction
     * of fund to client account OR order with CANCELED status if some error occured
     */
    ExchangeOrder approveExchangeOrder(ExchangeOrder exchangeOrder);
}
