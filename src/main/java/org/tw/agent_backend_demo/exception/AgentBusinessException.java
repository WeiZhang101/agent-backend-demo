package org.tw.agent_backend_demo.exception;

import lombok.Getter;

/**
 * Base business exception class for agent-related operations.
 * Extends RuntimeException to provide unchecked exception handling.
 */
@Getter
public class AgentBusinessException extends RuntimeException {
    
    /**
     * Business error code for categorizing different types of errors
     */
    private final String errorCode;
    
    /**
     * User-friendly error message
     */
    private final String errorMessage;
    
    /**
     * Constructor with error code and message.
     * 
     * @param errorCode business error code
     * @param errorMessage user-friendly error message
     */
    public AgentBusinessException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    /**
     * Constructor with error code, message, and cause.
     * 
     * @param errorCode business error code
     * @param errorMessage user-friendly error message
     * @param cause the underlying cause of the exception
     */
    public AgentBusinessException(String errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
