package com.tech.padawan.financialmanager.global.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.tech.padawan.financialmanager.global.exception.AlreadyExistsException;
import com.tech.padawan.financialmanager.global.exception.BusinessRuleException;
import com.tech.padawan.financialmanager.global.exception.NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Data integrity violation."; // Default message

        Throwable rootCause = ex.getRootCause();
        if (rootCause != null) {
            String rootCauseMessage = rootCause.getMessage();

            Pattern pattern = Pattern.compile("Key \\((.*?)\\)=\\((.*?)\\) already exists\\.");
            Matcher matcher = pattern.matcher(rootCauseMessage);

            if (matcher.find()) {
                String fieldName = matcher.group(1);
                String value = matcher.group(2);
                message = String.format("A record with this %s already exists.", fieldName);
            }
        }

        Map<String, String> body = Map.of("message", message);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(
            NotFoundException ex
    ) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidFieldException(
            AlreadyExistsException ex
    ) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Already exists");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<Map<String, String>> handleBusinessRuleException(
            BusinessRuleException ex
    ) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Business rules violation");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<Map<String, String>> handleJwtVerificationException(
            NotFoundException ex
    ) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex){
        Map<String, String> error = new HashMap<>();
        error.put("error", "Runtime Exception");
        error.put("message", ex.getMessage());
        System.out.println(ex);
        ex.printStackTrace();
        System.out.println("Error caused by: " + ex.getCause());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
