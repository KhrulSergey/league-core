package com.freetonleague.core.exception;


import com.freetonleague.core.exception.model.ValidationError;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@Data
@EqualsAndHashCode(callSuper = false)
public class ValidationException extends RuntimeException {

    private String message;
    private List<ValidationError> validationErrors;

    public ValidationException(String message, List<ValidationError> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }

    public ValidationException(String message, ValidationError error) {
        this(message, Collections.singletonList(error));
    }

    public ValidationException(String message, String field, String validationMessage) {
        this(message, Collections.singletonList(new ValidationError(field, validationMessage)));
    }

    public String getDetailedMessage() {
        return this.validationErrors.stream()
                .map(ve -> String.format("[field=%s, message=%s]", ve.getField(), ve.getMessage()))
                .collect(Collectors.joining());
    }
}
