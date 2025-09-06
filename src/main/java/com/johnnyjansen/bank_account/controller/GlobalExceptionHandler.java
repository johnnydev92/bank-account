package com.johnnyjansen.bank_account.controller;


import com.johnnyjansen.bank_account.infrastructure.exceptions.ConflictException;
import com.johnnyjansen.bank_account.infrastructure.exceptions.ResourceNotFoundException;
import com.johnnyjansen.bank_account.infrastructure.exceptions.UnathorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<String> handleConflictException(ConflictException ex) {
        return new ResponseEntity<>("A conflict occurred: " + ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>("The requested resource was not found: " + ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnathorizedException.class)
    public ResponseEntity<String> unathorizedException(UnathorizedException ex) {
        return new ResponseEntity<>("Unauthorized access: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected server error occurred. Please try again later.");
    }

}
