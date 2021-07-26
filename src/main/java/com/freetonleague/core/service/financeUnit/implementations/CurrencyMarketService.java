package com.freetonleague.core.service.financeUnit.implementations;

import com.freetonleague.core.cloudclient.BroxusClientService;
import com.freetonleague.core.domain.dto.finance.ExchangeRatioResponseDto;
import com.freetonleague.core.domain.enums.finance.CurrencyPairType;
import com.freetonleague.core.mapper.finance.ExchangeRatioResponseMapper;
import com.freetonleague.core.service.financeUnit.cloud.KunaMarketInfoProviderCloud;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

/**
 * Service interface for get information about currency quotes, prices, tickers
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CurrencyMarketService {

    private final KunaMarketInfoProviderCloud kunaMarketInfoProviderCloud;
    private final BroxusClientService broxusClientService;
    private final ExchangeRatioResponseMapper responseMapper;

    @Autowired
    @Qualifier("kunaMarketProviderMock")
    private KunaMarketInfoProviderCloud kunaMarketInfoProviderMock;

    @Value("${config.kuna-client.mock:true}")
    private Boolean isMarketInfoProviderMockEnabled;

    public ExchangeRatioResponseDto getExchangeRateForCurrencies(CurrencyPairType currencyPair) {
        if (isNull(currencyPair)) {
            log.error("!> requesting getExchangeRateForCurrencies for NULL currencyPair. Check evoking clients");
            return null;
        }
        log.debug("^ try to getExchangeRateForCurrencies in CurrencyMarketClientService for currencyPair '{}'", currencyPair);
        ExchangeRatioResponseDto exchangeRatioResponse = null;
        switch (currencyPair.getPreferredProvider()) {
            case BROXUS:
                exchangeRatioResponse = broxusClientService.getExchangeCurrencyRate(currencyPair);
                break;
//            case KUNA:
//            default:
//                exchangeRatioResponse = this.getExchangeCurrencyRateFromKuna(currencyPair);
//                break;
        }
        if (isNull(exchangeRatioResponse) || isNull(exchangeRatioResponse.getRatio())) {
            log.error("!!> Error in response from market provider while getExchangeRateForCurrencies in " +
                            "CurrencyMarketClientService. Transmitted currencyPair: '{}'. Received data: '{}' ",
                    currencyPair, exchangeRatioResponse);
            return null;
        }
        log.debug("^ received response for currencyPair '{}' in getExchangeRateForCurrencies of data '{}'",
                currencyPair, exchangeRatioResponse);
        return exchangeRatioResponse;
    }

//    private ExchangeRatioResponseDto getExchangeCurrencyRateFromKuna(CurrencyPairType currencyPair) {
//        log.debug("^ try to get exchange currency rate from Kuna in CurrencyMarketClientService for currencyPair '{}'", currencyPair);
//        ExchangeRatioKunaResponseDto exchangeRatioKunaResponse = this.getCurrentKunaClient()
//                .getExchangeCurrencyRate(currencyPair.getCode());
//        log.debug("^ received response from Kuna for currencyPair '{}' in getExchangeCurrencyRateFromKuna of data '{}'", currencyPair, exchangeRatioKunaResponse);
//        exchangeRatioKunaResponse.setCurrencyPairTypeCode(currencyPair.getCode());
//        exchangeRatioKunaResponse.setCurrencyPairType(currencyPair);
//        ExchangeRatioResponseDto exchangeRatioResponse = responseMapper.fromRaw(exchangeRatioKunaResponse);
//        Double ratio = currencyPair.getPairDirection().isForward() ?
//                1 / exchangeRatioKunaResponse.getExchangeRatioTicker().getAskPrice()
//                : exchangeRatioKunaResponse.getExchangeRatioTicker().getBidPrice();
//        exchangeRatioResponse.setRatio(ratio);
//        return exchangeRatioResponse;
//    }

//    public KunaMarketInfoProviderCloud getCurrentKunaClient() {
//        if (isMarketInfoProviderMockEnabled) {
//            log.warn("~ kuna MOCK client enabled. Work with kunaMarketInfoProviderMock service implementation in CurrencyMarketClientService");
//            return kunaMarketInfoProviderMock;
//        }
//        return kunaMarketInfoProviderCloud;
//    }
}
