package org.ingsw2526_036.bugboard26backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.ingsw2526_036.bugboard26backend.exception.ErrorCode;

import java.util.List;

public record ErrorResponseDto(
    ErrorCode errorCode,
    String message,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<String> details
) {
    public ErrorResponseDto(ErrorCode errorCode, String message) {
        this(errorCode, message, null);
    }
}
