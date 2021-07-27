package com.freetonleague.core.cloudclient;

import com.freetonleague.core.domain.dto.finance.ExchangeRatioBroxusResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Rest client for importing data from a Broxus provider directly
 */
@FeignClient(name = "broxus-native-client", url = "${config.broxus-native-client.url}")
public interface BroxusClientCloud {

    String AUTH_TOKEN = "api-key";
    String CURRENT_TIMESTAMP = "nonce";
    String SIGNATURE = "sign";

    String EXCHANGE_RATE_PATH = "/v1/exchange/rate";

    @PostMapping(EXCHANGE_RATE_PATH)
    ExchangeRatioBroxusResponseDto getExchangeCurrencyRate(@RequestHeader(AUTH_TOKEN) String token,
                                                           @RequestHeader(CURRENT_TIMESTAMP) Long currentTimestamp,
                                                           @RequestHeader(SIGNATURE) String signature,
                                                           @RequestBody ExchangeRatioBroxusResponseDto request);
}
