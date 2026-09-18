package org.ingsw2526_036.bugboard26backend.exception;

import lombok.Getter;

@Getter
public class BusinessRuleException extends IllegalArgumentException {
    private final ErrorCode errorCode;

    public BusinessRuleException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
