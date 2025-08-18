package org.tw.agent_backend_demo.service;

import org.tw.agent_backend_demo.dto.CreateAgentRequest;
import org.tw.agent_backend_demo.dto.CreateAgentResponse;

/**
 * Service interface for agent management operations.
 * Defines core business methods for agent creation and management.
 */
public interface AgentService {
    
    /**
     * Creates a new agent based on the provided request.
     * 
     * @param request the agent creation request containing all necessary information
     * @return CreateAgentResponse containing the created agent information
     * @throws org.tw.agent_backend_demo.exception.AgentNameExistsException if agent name already exists
     * @throws org.tw.agent_backend_demo.exception.InvalidVisibilityScopeException if visibility scope is invalid
     * @throws org.tw.agent_backend_demo.exception.InvalidUrlFormatException if URL format is invalid
     */
    CreateAgentResponse createAgent(CreateAgentRequest request);
}
