package com.freetonleague.core.service.implementations;

import com.freetonleague.core.config.properties.AppRouletteProperties;
import com.freetonleague.core.domain.dto.RandomLongDto;
import com.freetonleague.core.service.RandomService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RandomOrgServiceImpl implements RandomService {

    private final AppRouletteProperties properties;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public RandomLongDto getRandomLong(Long max) {
        Map<String, Object> requestBody = getRequestBody(properties.getRandomOrgApiKey(), max.intValue(), UUID.randomUUID().toString());


        ResponseEntity<RandomOrgResponseWrapper> responseEntity = restTemplate.postForEntity(
                "https://api.random.org/json-rpc/4/invoke",
                requestBody,
                RandomOrgResponseWrapper.class
        );

        RandomOrgResponseWrapper responseWrapper = responseEntity.getBody();

        return RandomLongDto.builder()
                .randomizeId(responseWrapper.getResult().getSignature())
                .value(responseWrapper.getResult().getRandom().getData()[0].longValue())
                .build();
    }


    private Map<String, Object> getRequestBody(String apiKey, Integer maxValue, String id) {
        return Map.of(
                "jsonrpc", "4.0",
                "method", "generateSignedIntegers",
                "params", Map.of(
                        "apiKey", apiKey,
                        "n", 1,
                        "min", 1,
                        "max", maxValue,
                        "replacement", false
                ),
                "id", "4.0"
        );
    }

    @Data
    private static class RandomOrgResponseWrapper {

        private Result result;

        @Data
        private static class Result {

            private Random random;
            private String signature;

            @Data
            private static class Random {

                private Integer[] data;

            }

        }

    }

}
