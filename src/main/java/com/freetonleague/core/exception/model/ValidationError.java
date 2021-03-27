package com.freetonleague.core.exception.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;


@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class ValidationError implements Serializable {
    private static final long serialVersionUID = 1721000215127027100L;

    private final String field;
    private final String message;

    @Override
    public String toString() {
        return "{" +
                "field:'" + field + '\'' +
                ", message:'" + message + '\'' +
                '}';
    }
}
