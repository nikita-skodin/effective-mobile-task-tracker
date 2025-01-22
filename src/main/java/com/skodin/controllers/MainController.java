package com.skodin.controllers;

import com.skodin.DTOs.ErrorDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Set;

@RequiredArgsConstructor
public abstract class MainController {

    @ExceptionHandler
    protected ResponseEntity<ErrorDTO> handleException(ConstraintViolationException e) {

        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();

        StringBuilder response = new StringBuilder();

        for (var el : constraintViolations) {
            String[] pathElements = el.getPropertyPath().toString().split("\\.");
            String message = pathElements[pathElements.length - 1] + ": " + el.getMessage();
            response.append(message).append("; ");
        }

        return ResponseEntity
                .status(400)
                .body(new ErrorDTO(HttpStatus.BAD_REQUEST, response.toString().trim()));
    }

}
