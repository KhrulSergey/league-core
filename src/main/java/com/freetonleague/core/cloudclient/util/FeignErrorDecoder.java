package com.freetonleague.core.cloudclient.util;

import com.freetonleague.core.exception.BadRequestException;
import com.freetonleague.core.exception.NotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FeignErrorDecoder implements ErrorDecoder {

    //TODO отладить работу декодера
//    @Override
//    public Exception decode(String methodKey, Response response) {
//        if (response.status() >= 400 && response.status() <= 499) {
//            return new StashClientException(
//                    response.status(),
//                    response.reason()
//            );
//        }
//        if (response.status() >= 500 && response.status() <= 599) {
//            return new StashServerException(
//                    response.status(),
//                    response.reason()
//            );
//        }
//        return errorStatus(methodKey, response);
//    }

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("!> Got error while feign http request. Method key '{}'. Response body from resource: {}, reason {}. " +
                        "Full response from resource: {}",
                methodKey, response.body(), response.reason(), response);
        switch (response.status()) {
            case 400:
                return new BadRequestException();
            case 404:
                return new NotFoundException();
            default:
                return new Exception("Generic error");
        }
    }
}
