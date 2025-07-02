package com.gadbacorp.api.excepciones;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice  // o ponlo justo en tu TipoProductoController
public class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String,String>> handleStatusException(ResponseStatusException ex) {
        // Armamos un JSON { "message": "la razón" }
        Map<String,String> body = Collections.singletonMap("message", ex.getReason());
        return ResponseEntity
            .status(ex.getStatusCode())      // en Spring 5 usa ex.getRawStatusCode()
            .body(body);
    }
}