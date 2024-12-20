package com.freetonleague.core.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class GameDisciplineManageException extends BaseDetailedException {

    public GameDisciplineManageException(String message, String detailedMessage) {
        super(message, detailedMessage);
    }
}
