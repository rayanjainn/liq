package com.collabnex.common.exception;

import com.collabnex.common.dto.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler that catches all thrown exceptions and returns
 * consistent {@link ApiResponse} shapes. Ensures frontend clients always receive
 * a predictable JSON structure regardless of error type.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles NotFoundException — returns 404 Not Found.
     *
     * @param ex the NotFoundException thrown by service or controller code
     * @return ApiResponse with success=false and the error message, HTTP 404
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handles BusinessException — returns 400 Bad Request by default,
     * or 409 Conflict if the message indicates a duplicate/conflict scenario.
     *
     * @param ex the BusinessException thrown by service code
     * @return ApiResponse with success=false and the error message
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        String msg = ex.getMessage();
        // Return 409 for duplicate/conflict scenarios
        if (msg != null && (msg.contains("already") || msg.contains("duplicate"))) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(msg));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(msg));
    }

    /**
     * Handles Bean Validation failures (from @Valid annotated parameters).
     * Returns a map of field names to error messages.
     *
     * @param ex the MethodArgumentNotValidException from validation failures
     * @return ApiResponse with field-level error details, HTTP 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (var err : ex.getBindingResult().getAllErrors()) {
            String field = ((FieldError) err).getField();
            errors.put(field, err.getDefaultMessage());
        }
        return ResponseEntity.badRequest()
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("Validation failed")
                        .data(errors)
                        .build());
    }

    /**
     * Handles DataIntegrityViolationException — typically from unique constraint violations
     * (e.g., duplicate job application). Returns 409 Conflict.
     *
     * @param ex the DataIntegrityViolationException from the database layer
     * @return ApiResponse with conflict message, HTTP 409
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("Duplicate entry or data integrity violation"));
    }

    /**
     * Catch-all handler for any unhandled exceptions. Logs the stack trace
     * and returns a generic 500 Internal Server Error.
     *
     * @param ex the unhandled exception
     * @return ApiResponse with a generic error message, HTTP 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Unexpected error: " + ex.getMessage()));
    }
}
