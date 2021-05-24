package com.freetonleague.core.cloudclient.util;

import com.freetonleague.core.exception.*;
import com.freetonleague.core.exception.model.ApiError;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class FeignErrorDecoder implements ErrorDecoder {

    private final Gson gson;

    @Override
    public Exception decode(String methodKey, Response response) {
        String body = response.body().toString();
        log.error("!> Got error while feign http request. Method key '{}'. Response body from resource: {}, reason {}. " +
                "Full response from resource: {}", methodKey, body, response.reason(), response);
        ApiError apiError;
        try {
            apiError = gson.fromJson(body, ApiError.class);
        } catch (JsonParseException e) {
            return new CustomUnexpectedException(ExceptionMessages.FEIGN_UNEXPECTED_ERROR);
        }
        if (nonNull(apiError)) {
            return new InnerServiceFeignException(apiError);
        }
        switch (response.status()) {
            case 400:
                return new BadRequestException();
            case 404:
                return new NotFoundException();
            default:
                return new CustomUnexpectedException(ExceptionMessages.FEIGN_UNEXPECTED_ERROR);
        }
    }
}
