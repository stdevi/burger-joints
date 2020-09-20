package com.stdevi.burgerjoints.utilities;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(value = ClientException.class)
    public final ResponseEntity<Map<String, String>> handleClientException(ClientException exception) {
        Map<String, String> response = new HashMap<>();

        response.put("timestamp", exception.getTimestamp());
        response.put("message", exception.getMessage());

        return new ResponseEntity<>(response, exception.getStatus());
    }
}
