package org.tw.agent_backend_demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Unified error response DTO for API error information.
 * Provides standardized format for all error responses across the application.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    /**
     * Business error code for categorizing different types of errors
     */
    private String errorCode;
    
    /**
     * User-friendly error message
     */
    private String errorMessage;
    
    /**
     * Timestamp when the error occurred
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;
    
    /**
     * Request path where the error occurred
     */
    private String path;
    
    /**
     * Additional error detail information (optional)
     */
    private Map<String, Object> details;
    
    /**
     * Static factory method to create an ErrorResponse with error code and message.
     * 
     * @param errorCode business error code
     * @param errorMessage user-friendly error message
     * @return ErrorResponse instance with timestamp automatically set
     */
    public static ErrorResponse of(String errorCode, String errorMessage) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Sets the request path for this error response.
     * 
     * @param path the request path where the error occurred
     * @return this ErrorResponse instance for method chaining
     */
    public ErrorResponse withPath(String path) {
        this.path = path;
        return this;
    }
    
    /**
     * Sets additional detail information for this error response.
     * 
     * @param details additional error detail information
     * @return this ErrorResponse instance for method chaining
     */
    public ErrorResponse withDetails(Map<String, Object> details) {
        this.details = details;
        return this;
    }
}
