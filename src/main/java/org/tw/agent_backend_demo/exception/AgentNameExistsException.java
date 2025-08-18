package org.tw.agent_backend_demo.exception;

import lombok.Getter;

/**
 * Exception thrown when attempting to create an agent with a name that already exists.
 * Extends AgentBusinessException to provide specific handling for name duplication scenarios.
 */
@Getter
public class AgentNameExistsException extends AgentBusinessException {
    
    /**
     * Error code for agent name existence conflicts
     */
    private static final String ERROR_CODE = "AGENT_NAME_EXISTS";
    
    /**
     * The duplicate agent name that caused the conflict
     */
    private final String duplicateName;
    
    /**
     * Constructor with duplicate name.
     * 
     * @param duplicateName the agent name that already exists
     */
    public AgentNameExistsException(String duplicateName) {
        super(ERROR_CODE, "Agent name '" + duplicateName + "' already exists");
        this.duplicateName = duplicateName;
    }
    
    /**
     * Constructor with duplicate name and cause.
     * 
     * @param duplicateName the agent name that already exists
     * @param cause the underlying cause of the exception
     */
    public AgentNameExistsException(String duplicateName, Throwable cause) {
        super(ERROR_CODE, "Agent name '" + duplicateName + "' already exists", cause);
        this.duplicateName = duplicateName;
    }
}
