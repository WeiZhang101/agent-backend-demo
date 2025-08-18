package org.tw.agent_backend_demo.exception;

import lombok.Getter;

/**
 * Exception thrown when an invalid visibility scope is provided.
 * Extends AgentBusinessException to provide specific handling for visibility scope validation errors.
 */
@Getter
public class InvalidVisibilityScopeException extends AgentBusinessException {
    
    /**
     * Error code for invalid visibility scope
     */
    private static final String ERROR_CODE = "INVALID_VISIBILITY_SCOPE";
    
    /**
     * The invalid scope value that caused the error
     */
    private final String invalidScope;
    
    /**
     * Constructor with invalid scope.
     * 
     * @param invalidScope the invalid visibility scope value
     */
    public InvalidVisibilityScopeException(String invalidScope) {
        super(ERROR_CODE, "Invalid visibility scope: " + invalidScope);
        this.invalidScope = invalidScope;
    }
    
    /**
     * Constructor with invalid scope and cause.
     * 
     * @param invalidScope the invalid visibility scope value
     * @param cause the underlying cause of the exception
     */
    public InvalidVisibilityScopeException(String invalidScope, Throwable cause) {
        super(ERROR_CODE, "Invalid visibility scope: " + invalidScope, cause);
        this.invalidScope = invalidScope;
    }
}
