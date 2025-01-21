package com.skodin.DTOs;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ErrorDTO(HttpStatus status, String message, LocalDateTime timestamp) {

    public static ErrorDTO badRequest(String message) {
        return new ErrorDTO(HttpStatus.BAD_REQUEST, message);
    }

    public static ErrorDTO notFound(String message) {
        return new ErrorDTO(HttpStatus.NOT_FOUND, message);
    }

    public ErrorDTO(HttpStatus status, String message) {
        this(status, message, LocalDateTime.now());
    }
}
