package org.ingsw2526_036.bugboard26backend.exception;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.List;
import java.util.Map;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<@NonNull String> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<@NonNull String> handleResourceNotFoundException(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<@NonNull String> handleDuplicateResourceException(DuplicateResourceException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<@NonNull List<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {

        List<String> listaErrori = new ArrayList<>();

        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            String messaggio = error.getField() + ": " + error.getDefaultMessage();
            listaErrori.add(messaggio);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(listaErrori);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<@NonNull String> handleUsernameNotFoundException(UsernameNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        
        errorResponse.put("error", "Validation Error");
        errorResponse.put("message", ex.getMessage()); 

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // Ritorna 400
    }
}
