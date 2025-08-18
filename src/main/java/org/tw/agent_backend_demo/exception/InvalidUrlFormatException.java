package org.tw.agent_backend_demo.exception;

import lombok.Getter;

/**
 * Exception thrown when an invalid URL format is provided.
 * Extends AgentBusinessException to provide specific handling for URL format validation errors.
 */
@Getter
public class InvalidUrlFormatException extends AgentBusinessException {
    
    /**
     * Error code for invalid URL format
     */
    private static final String ERROR_CODE = "INVALID_URL_FORMAT";
    
    /**
     * The invalid URL that caused the error
     */
    private final String invalidUrl;
    
    /**
     * Constructor with invalid URL.
     * 
     * @param invalidUrl the incorrectly formatted URL
     */
    public InvalidUrlFormatException(String invalidUrl) {
        super(ERROR_CODE, "Invalid URL format: " + invalidUrl);
        this.invalidUrl = invalidUrl;
    }
    
    /**
     * Constructor with invalid URL and cause.
     * 
     * @param invalidUrl the incorrectly formatted URL
     * @param cause the underlying cause of the exception
     */
    public InvalidUrlFormatException(String invalidUrl, Throwable cause) {
        super(ERROR_CODE, "Invalid URL format: " + invalidUrl, cause);
        this.invalidUrl = invalidUrl;
    }
}
