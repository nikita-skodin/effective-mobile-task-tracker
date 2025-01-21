package com.skodin.DTOs;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.http.HttpStatus;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ErrorDTO(HttpStatus status, String message) {

    public static ErrorDTO badRequest(String message) {
        return new ErrorDTO(HttpStatus.BAD_REQUEST, message);
    }

    public static ErrorDTO notFound(String message) {
        return new ErrorDTO(HttpStatus.NOT_FOUND, message);
    }
}
