package org.ingsw2526_036.bugboard26backend.exception;

import lombok.Getter;

@Getter
public class DuplicateResourceException extends RuntimeException {
    private final ErrorCode errorCode;

    public DuplicateResourceException(String message) {
        super(message);
        this.errorCode = ErrorCode.DUPLICATE_RESOURCE;
    }

    public DuplicateResourceException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}