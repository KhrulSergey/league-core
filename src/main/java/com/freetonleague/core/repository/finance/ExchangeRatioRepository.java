package com.freetonleague.core.repository.finance;

import com.freetonleague.core.domain.enums.finance.Currency;
import com.freetonleague.core.domain.model.finance.ExchangeRatio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ExchangeRatioRepository extends JpaRepository<ExchangeRatio, Long>,
        JpaSpecificationExecutor<ExchangeRatio> {

    ExchangeRatio findByCurrencyToBuyAndCurrencyToSellAndExpiredAtAfter(Currency currencyToBuy, Currency currencyToSell, LocalDateTime date);

    @Query(value = "select r from ExchangeRatio r where r.currencyToBuy = :currencyToBuy and r.currencyToSell = :currencyToSell" +
            " and r.expiredAt > CURRENT_TIMESTAMP")
    ExchangeRatio getActiveRatioByCurrencies(@Param("currencyToBuy") Currency currencyToBuy,
                                             @Param("currencyToSell") Currency currencyToSell);

//    CURRENT_DATE - is evaluated to the current date (a java.sql.Date instance).
//    CURRENT_TIME - is evaluated to the current time (a java.sql.Time instance).
//    CURRENT_TIMESTAMP - is evaluated to the current timestamp, i.e. date and time (a java.sql.Timestamp instance).
}
