package com.freetonleague.core.cloudclient;

import com.freetonleague.core.domain.dto.finance.ExchangeRatioBroxusResponseDto;
import com.freetonleague.core.domain.dto.finance.ExchangeRatioResponseDto;
import com.freetonleague.core.domain.enums.finance.CurrencyPairType;
import com.freetonleague.core.mapper.finance.ExchangeRatioResponseMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

import static java.util.Objects.isNull;

/**
 * Service-client for interact with Third-party Finance Provider Broxus
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BroxusClientService {

    private final BroxusClientCloud broxusClientCloud;
    private final ExchangeRatioResponseMapper responseMapper;
    private final Gson gsonSerializer;

    @Autowired
    @Qualifier("broxusClientMock")
    private BroxusClientCloud broxusClientMock;

    @Value("${config.broxus-native-client.token}")
    private String broxusToken;

    @Value("${config.broxus-native-client.mock:true}")
    private Boolean broxusMockEnabled;


    public ExchangeRatioResponseDto getExchangeCurrencyRate(CurrencyPairType currencyPair) {
        log.debug("^ try to get exchange currency rate from Broxus in BroxusClientService for currencyPair '{}'", currencyPair);
        ExchangeRatioBroxusResponseDto exchangeRatioBroxusRequest = ExchangeRatioBroxusResponseDto.builder()
                .id(UUID.randomUUID())
                .from(currencyPair.getCurrencyToSell())
                .to(currencyPair.getCurrencyToBuy())
                .build();
        String content = gsonSerializer.toJson(exchangeRatioBroxusRequest);
        Pair<Long, String> signPair = null;
        try {
            signPair = this.getSignature(BroxusClientCloud.EXCHANGE_RATE_PATH, content);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("!> error occurred for getExchangeCurrencyRate with request '{}' " +
                    "while calculate signature for broxus method with message '{}'", currencyPair, e.getMessage(), e);
        }
        if (isNull(signPair)) {
            log.error("!> error occurred for getExchangeCurrencyRate with request '{}' " +
                    "while get signature for broxus method. Signature was returned as NULL", currencyPair);
            return null;
        }
        log.debug("^ send request to Broxus to get exchange currency rate with data '{}', timestamp '{}' and signature '{}'",
                exchangeRatioBroxusRequest, signPair.getKey(), signPair.getValue());
        ExchangeRatioBroxusResponseDto exchangeRatioBroxusResponse = this.getCurrentBroxusClient().getExchangeCurrencyRate(broxusToken,
                signPair.getKey(), signPair.getValue(), exchangeRatioBroxusRequest);
        log.debug("^ received response for currencyPair '{}' from Broxus in getExchangeCurrencyRate of data '{}'",
                currencyPair, exchangeRatioBroxusResponse);
        exchangeRatioBroxusResponse.setRequestTimeRaw(signPair.getKey());
        return responseMapper.fromRaw(exchangeRatioBroxusResponse);
    }


    public BroxusClientCloud getCurrentBroxusClient() {
        if (broxusMockEnabled) {
            log.warn("~ broxus MOCK client enabled. Work with broxusClientMock service implementation in BroxusClientService");
            return broxusClientMock;
        }
        return broxusClientCloud;
    }

    /**
     * Signs the request for Broxus
     * e.g Header params (api-key, nonce, sign)
     * sign -> BASE64 format of HMAC SHA 256 of (nonce+url+body) -
     * for example, `1555687327100/v1/users/balance{"userAddress":"388854653","addressType":"telegram"}`.
     *
     * @param method  Path to a method called
     * @param content Request body to be sent
     * @return signature
     */
    private Pair<Long, String> getSignature(String method, String content) throws NoSuchAlgorithmException, InvalidKeyException {
        Long nonce = System.currentTimeMillis();
        String salt = nonce.toString() + method + content;
        SecretKeySpec secretKeySpec = new SecretKeySpec(broxusToken.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] signature = mac.doFinal(salt.getBytes(StandardCharsets.UTF_8));
        String base64 = Base64.getEncoder().encodeToString(signature);
        return new ImmutablePair<>(nonce, base64);
    }
}
