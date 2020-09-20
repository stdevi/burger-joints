package com.stdevi.burgerjoints.utilities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@Getter
public class ClientException extends RuntimeException {

    private final String timestamp;
    private final HttpStatus status;
    private final String message;

    public ClientException(String message, HttpStatus status) {
        this.timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
        this.status = status;
        this.message = message;
    }
}
