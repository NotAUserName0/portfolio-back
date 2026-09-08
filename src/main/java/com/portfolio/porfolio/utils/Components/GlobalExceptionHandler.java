package com.portfolio.porfolio.utils.Components;
import org.springframework.http.HttpStatus;
import com.portfolio.porfolio.utils.HttpStatuses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.portfolio.porfolio.utils.ApiResponse;
import com.portfolio.porfolio.utils.Messages;

import java.util.HashMap;
import java.util.Map;

/**
 * @description Global exception handler for handling runtime and validation exceptions.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @description Handles validation/argument errors (such as invalid file MIME types or extensions).
     * @param ex The IllegalArgumentException thrown.
     * @return Standardized error response with BAD_REQUEST status.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(HttpStatuses.BAD_REQUEST, ex.getMessage()));
    }

    /**
     * @description Handles security violations (such as Path Traversal attempts).
     * @param ex The SecurityException thrown.
     * @return Standardized error response with FORBIDDEN status.
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse<Object>> handleSecurityException(SecurityException ex) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(HttpStatuses.FORBIDDEN, ex.getMessage()));
    }

    /**
     * @description Handles runtime exceptions and returns a standardized error response.
     * @param ex The runtime exception that occurred.
     * @return A ResponseEntity containing the standardized error response.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * @description Handles validation exceptions and returns a standardized error response.
     * @param ex The validation exception that occurred.
     * @return A ResponseEntity containing the standardized error response.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity
            .status(HttpStatus.NOT_ACCEPTABLE)
            .body(new ApiResponse<>(false, HttpStatuses.NOT_ACCEPTABLE, Messages.VALIDATION_ERROR, errors));
    }
}
