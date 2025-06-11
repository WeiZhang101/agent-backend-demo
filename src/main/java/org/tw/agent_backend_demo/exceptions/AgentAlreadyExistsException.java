package org.tw.agent_backend_demo.exceptions;

public class AgentAlreadyExistsException extends RuntimeException {
    public AgentAlreadyExistsException(String message) {
        super(message);
    }
} 