package com.freetonleague.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CustomUnexpectedException extends RuntimeException {

    public CustomUnexpectedException(String message) {
        super(message);
    }
}
