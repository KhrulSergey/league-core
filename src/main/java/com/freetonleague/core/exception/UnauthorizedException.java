package com.freetonleague.core.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends BaseDetailedException {

    public UnauthorizedException(String message, String detailedMessage) {
        super(message, detailedMessage);
    }
}
