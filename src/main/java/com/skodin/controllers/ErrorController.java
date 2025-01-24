package com.skodin.controllers;

import com.skodin.DTOs.ErrorDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    private static final String PATH = "/error";

    private final ErrorAttributes errorAttributes;

    @RequestMapping(PATH)
    public ResponseEntity<ErrorDTO> error(WebRequest webRequest) {
        log.info("Handling error request at path: {}", PATH);

        Map<String, Object> attributes = errorAttributes.getErrorAttributes(
                webRequest,
                ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE)
        );

        Integer status = (Integer) attributes.get("status");
        log.debug("Extracted error attributes: status={}, message={}", status, attributes.get("message"));

        ResponseEntity<ErrorDTO> response = ResponseEntity
                .status(status)
                .body(new ErrorDTO(HttpStatus.resolve(status), (String) attributes.get("message")));

        log.info("Returning error response with status: {} and message: {}", status, attributes.get("message"));
        return response;
    }
}