package org.tw.agent_backend_demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.tw.agent_backend_demo.dto.ErrorResponse;
import org.tw.agent_backend_demo.exception.AgentBusinessException;
import org.tw.agent_backend_demo.exception.AgentNameExistsException;
import org.tw.agent_backend_demo.exception.InvalidUrlFormatException;
import org.tw.agent_backend_demo.exception.InvalidVisibilityScopeException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Provides unified exception handling and standardized error responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handles general business exceptions from the agent domain.
     * 
     * @param ex the business exception
     * @param request the web request context
     * @return ResponseEntity with appropriate HTTP status and error response
     */
    @ExceptionHandler(AgentBusinessException.class)
    public ResponseEntity<ErrorResponse> handleAgentBusinessException(
            AgentBusinessException ex, WebRequest request) {
        log.warn("Business exception occurred: {}", ex.getErrorMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.of(ex.getErrorCode(), ex.getErrorMessage())
                .withPath(getRequestPath(request));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handles agent name existence exceptions.
     * 
     * @param ex the agent name exists exception
     * @param request the web request context
     * @return ResponseEntity with HTTP 409 status and error response
     */
    @ExceptionHandler(AgentNameExistsException.class)
    public ResponseEntity<ErrorResponse> handleAgentNameExistsException(
            AgentNameExistsException ex, WebRequest request) {
        log.warn("Agent name already exists: {}", ex.getDuplicateName());
        
        Map<String, Object> details = new HashMap<>();
        details.put("duplicateName", ex.getDuplicateName());
        
        ErrorResponse errorResponse = ErrorResponse.of(ex.getErrorCode(), ex.getErrorMessage())
                .withPath(getRequestPath(request))
                .withDetails(details);
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    /**
     * Handles invalid visibility scope exceptions.
     * 
     * @param ex the invalid visibility scope exception
     * @param request the web request context
     * @return ResponseEntity with HTTP 400 status and error response
     */
    @ExceptionHandler(InvalidVisibilityScopeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidVisibilityScopeException(
            InvalidVisibilityScopeException ex, WebRequest request) {
        log.warn("Invalid visibility scope: {}", ex.getInvalidScope());
        
        Map<String, Object> details = new HashMap<>();
        details.put("invalidScope", ex.getInvalidScope());
        
        ErrorResponse errorResponse = ErrorResponse.of(ex.getErrorCode(), ex.getErrorMessage())
                .withPath(getRequestPath(request))
                .withDetails(details);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handles invalid URL format exceptions.
     * 
     * @param ex the invalid URL format exception
     * @param request the web request context
     * @return ResponseEntity with HTTP 400 status and error response
     */
    @ExceptionHandler(InvalidUrlFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrlFormatException(
            InvalidUrlFormatException ex, WebRequest request) {
        log.warn("Invalid URL format: {}", ex.getInvalidUrl());
        
        Map<String, Object> details = new HashMap<>();
        details.put("invalidUrl", ex.getInvalidUrl());
        
        ErrorResponse errorResponse = ErrorResponse.of(ex.getErrorCode(), ex.getErrorMessage())
                .withPath(getRequestPath(request))
                .withDetails(details);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handles Spring validation exceptions from @Valid annotations.
     * 
     * @param ex the method argument not valid exception
     * @param request the web request context
     * @return ResponseEntity with HTTP 400 status and detailed validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request) {
        log.warn("Validation failed for request: {}", ex.getMessage());
        
        Map<String, Object> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = ErrorResponse.of("VALIDATION_FAILED", "Request validation failed")
                .withPath(getRequestPath(request))
                .withDetails(Map.of("validationErrors", validationErrors));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handles data integrity violation exceptions from database constraints.
     * 
     * @param ex the data integrity violation exception
     * @param request the web request context
     * @return ResponseEntity with appropriate HTTP status and error response
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request) {
        log.error("Data integrity violation: {}", ex.getMessage(), ex);
        
        String errorMessage = "Data integrity constraint violation";
        String errorCode = "DATA_INTEGRITY_VIOLATION";
        
        // Check if it's a unique constraint violation
        if (ex.getMessage() != null && ex.getMessage().contains("unique")) {
            errorMessage = "Duplicate data detected";
            errorCode = "DUPLICATE_DATA";
        }
        
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, errorMessage)
                .withPath(getRequestPath(request));
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    /**
     * Handles unexpected system exceptions.
     * 
     * @param ex the generic exception
     * @param request the web request context
     * @return ResponseEntity with HTTP 500 status and generic error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.of("INTERNAL_SERVER_ERROR", "An unexpected error occurred")
                .withPath(getRequestPath(request));
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Extracts the request path from the web request.
     * 
     * @param request the web request
     * @return the request path
     */
    private String getRequestPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
