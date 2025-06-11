package org.tw.agent_backend_demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AgentAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleAgentAlreadyExistsException(AgentAlreadyExistsException e) {
        return buildErrorResponse(HttpStatus.CONFLICT, "AGENT_ALREADY_EXISTS", e.getMessage());
    }

    @ExceptionHandler(InvalidSourceException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidSourceException(InvalidSourceException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_SOURCE", e.getMessage());
    }

    @ExceptionHandler(InvalidTagException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTagException(InvalidTagException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_TAG", e.getMessage());
    }

    @ExceptionHandler(InvalidIconUrlException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidIconUrlException(InvalidIconUrlException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_ICON_URL", e.getMessage());
    }

    @ExceptionHandler(InvalidCategoryException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCategoryException(InvalidCategoryException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_CATEGORY", e.getMessage());
    }

    @ExceptionHandler(InvalidTargetSystemUrlException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTargetSystemUrlException(InvalidTargetSystemUrlException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_TARGET_SYSTEM_URL", e.getMessage());
    }

    @ExceptionHandler(InvalidVisibilityScopeException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidVisibilityScopeException(InvalidVisibilityScopeException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_VISIBILITY_SCOPE", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "服务器内部错误");
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String code, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("code", code);
        errorResponse.put("message", message);
        
        return new ResponseEntity<>(errorResponse, status);
    }
} 