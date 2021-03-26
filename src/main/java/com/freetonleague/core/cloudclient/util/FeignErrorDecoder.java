package com.freetonleague.core.cloudclient.util;

import com.freetonleague.core.exception.BadRequestException;
import com.freetonleague.core.exception.NotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;

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
        switch (response.status()){
            case 400:
                return new BadRequestException();
            case 404:
                return new NotFoundException();
            default:
                return new Exception("Generic error");
        }
    }
}
