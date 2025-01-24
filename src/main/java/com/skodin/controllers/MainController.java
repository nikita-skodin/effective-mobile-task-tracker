package com.skodin.controllers;

import com.skodin.DTOs.ErrorDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Set;

@Log4j2
@RequiredArgsConstructor
public abstract class MainController {

    @ExceptionHandler
    protected ResponseEntity<ErrorDTO> handleException(ConstraintViolationException e) {
        log.warn("ConstraintViolationException occurred: {}", e.getMessage());

        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();

        StringBuilder response = new StringBuilder();

        for (var el : constraintViolations) {
            String[] pathElements = el.getPropertyPath().toString().split("\\.");
            String message = pathElements[pathElements.length - 1] + ": " + el.getMessage();
            response.append(message).append("; ");
            log.debug("Constraint violation: property={}, message={}", pathElements[pathElements.length - 1], el.getMessage());
        }

        String responseMessage = response.toString().trim();
        log.info("Returning error response: {}", responseMessage);

        return ResponseEntity
                .status(400)
                .body(new ErrorDTO(HttpStatus.BAD_REQUEST, responseMessage));
    }
}