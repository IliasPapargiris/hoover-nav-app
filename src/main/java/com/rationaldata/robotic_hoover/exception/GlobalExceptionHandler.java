package com.rationaldata.robotic_hoover.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {


    private ErrorResponse createErrorResponse(String error, String message, HttpStatus status) {
        return new ErrorResponse(error, message, status.value(), LocalDateTime.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        String errorMessage = errors.toString();
        ErrorResponse errorResponse = createErrorResponse("Validation Failed", errorMessage, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });
        String errorMessage = errors.toString();
        ErrorResponse errorResponse = createErrorResponse("Constraint Violation", errorMessage, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRoomSizeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRoomSizeException(InvalidRoomSizeException ex, WebRequest request) {
        ErrorResponse errorResponse = createErrorResponse("Invalid Room Size", ex.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(OutOfRoomBoundsCoordinatesException.class)
    public ResponseEntity<ErrorResponse> handleOutOfRoomBoundsCoordinatesException(OutOfRoomBoundsCoordinatesException ex, WebRequest request) {
        ErrorResponse errorResponse = createErrorResponse("Out of Room Bounds", ex.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NegativeValuesException.class)
    public ResponseEntity<ErrorResponse> handleNegativeValuesException(NegativeValuesException ex, WebRequest request) {
        ErrorResponse errorResponse = createErrorResponse("Negative Values Error", ex.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
