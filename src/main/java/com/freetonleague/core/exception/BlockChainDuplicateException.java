package com.freetonleague.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
public class BlockChainDuplicateException extends RuntimeException {

    public BlockChainDuplicateException(String message) {
        super(message);
    }
}
