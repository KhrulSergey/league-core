package com.freetonleague.core.service.financeUnit.cloud;

import com.freetonleague.core.domain.dto.finance.ExchangeRatioKunaResponseDto;
import com.freetonleague.core.domain.dto.finance.ExchangeRatioKunaTickerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Rest MOCK client for simulate interaction with Third-party Currency Market Info Provider "Kuna"
 */
@Slf4j
@RequiredArgsConstructor
@Component("kunaMarketProviderMock")
public class CurrencyMarketInfoProviderMockImpl implements KunaMarketInfoProviderCloud {

    @Override
    public ExchangeRatioKunaResponseDto getExchangeCurrencyRate(String currencyPairCode) {
        ExchangeRatioKunaTickerDto exchangeRatioKunaTicker = ExchangeRatioKunaTickerDto.builder()
                .bidPrice(72.8)
                .askPrice(73.33)
                .priceChangeInQuotedCurrencyBy24hr(0.709999999999994)
                .lastPrice(72.79)
                .tradingVolumeInBaseCurrencyBy24hr(708627.83)
                .maximumPriceIn24hr(73.5)
                .minimumPriceFor24hr(71.57)
                .build();

        return ExchangeRatioKunaResponseDto.builder()
                .currencyPairTypeCode(currencyPairCode)
                .exchangeRatioTicker(exchangeRatioKunaTicker)
                .requestTimeRaw(Instant.now().getEpochSecond())
                .build();
    }
}
