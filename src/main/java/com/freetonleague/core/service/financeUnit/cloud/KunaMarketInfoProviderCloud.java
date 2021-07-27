package com.freetonleague.core.service.financeUnit.cloud;

import com.freetonleague.core.domain.dto.finance.ExchangeRatioKunaResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Rest client for interact with Third-party Currency Market Info Provider "Kuna"
 */
@FeignClient(name = "kuna-client", url = "${config.kuna-client.url}")
public interface KunaMarketInfoProviderCloud {
    String CURRENCY_PAIR_CODE = "symbols";

    @GetMapping("/v2/tickers/{symbols}")
    ExchangeRatioKunaResponseDto getExchangeCurrencyRate(@PathVariable(CURRENCY_PAIR_CODE) String currencyPairCode);
}
