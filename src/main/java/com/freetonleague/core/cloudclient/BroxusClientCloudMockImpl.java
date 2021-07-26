package com.freetonleague.core.cloudclient;

import com.freetonleague.core.domain.dto.finance.ExchangeRatioBroxusResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Mock client for imitate import data from a Broxus provider
 */
@Slf4j
@RequiredArgsConstructor
@Component("broxusClientMock")
public class BroxusClientCloudMockImpl implements BroxusClientCloud {

    @Override
    public ExchangeRatioBroxusResponseDto getExchangeCurrencyRate(String token, Long currentTimestamp,
                                                                  String signature, ExchangeRatioBroxusResponseDto request) {
        request.setRate(2.314814814814815);
        return request;
    }
}
