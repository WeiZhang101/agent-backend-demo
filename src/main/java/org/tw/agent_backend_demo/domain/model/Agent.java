package org.tw.agent_backend_demo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Domain model representing an intelligent service agent.
 * Contains business logic and validation rules for agent management.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Agent {
    
    /**
     * Unique identifier for the agent
     */
    private UUID id;
    
    /**
     * Source type of the agent (fastgpt or hand)
     */
    private String source;
    
    /**
     * Name of the agent (must be unique, max 50 characters)
     */
    private String agentName;
    
    /**
     * List of tags associated with the agent
     */
    private List<String> tags;
    
    /**
     * URL to the agent's icon image
     */
    private String iconUrl;
    
    /**
     * Description of the agent (required, max 500 characters)
     */
    private String description;
    
    /**
     * Category of the agent
     */
    private String category;
    
    /**
     * URL of the target system this agent connects to
     */
    private String targetSystemUrl;
    
    /**
     * Visibility scope configuration for the agent
     */
    private VisibilityScope visibilityScope;
    
    /**
     * Creator of the agent (automatically populated by system)
     */
    private String creator;
    
    /**
     * Creation timestamp (automatically populated by system)
     */
    private LocalDateTime createdAt;
    
    /**
     * Converts this Agent domain model to an AgentPO persistence entity.
     * 
     * @return AgentPO object for database operations
     */
    public org.tw.agent_backend_demo.domain.model.AgentPO toAgentPO() {
        return org.tw.agent_backend_demo.domain.model.AgentPO.builder()
                .id(this.id)
                .source(this.source)
                .agentName(this.agentName)
                .tags(this.tags)
                .iconUrl(this.iconUrl)
                .description(this.description)
                .category(this.category)
                .targetSystemUrl(this.targetSystemUrl)
                .visibilityScope(this.visibilityScope)
                .creator(this.creator)
                .createdAt(this.createdAt)
                .build();
    }
    
    /**
     * Converts this Agent domain model to a CreateAgentResponse DTO.
     * 
     * @return CreateAgentResponse object for API response
     */
    public org.tw.agent_backend_demo.dto.CreateAgentResponse toCreateAgentResponse() {
        return org.tw.agent_backend_demo.dto.CreateAgentResponse.builder()
                .id(this.id)
                .source(this.source)
                .agentName(this.agentName)
                .tags(this.tags)
                .iconUrl(this.iconUrl)
                .description(this.description)
                .category(this.category)
                .targetSystemUrl(this.targetSystemUrl)
                .visibilityScope(this.visibilityScope)
                .creator(this.creator)
                .createdAt(this.createdAt)
                .build();
    }
    
    /**
     * Initializes system fields for the agent.
     * Encapsulates the logic for setting creator and creation timestamp.
     * 
     * @param creator the user or system creating the agent
     */
    public void initializeSystemFields(String creator) {
        this.creator = creator;
        this.createdAt = LocalDateTime.now();
    }
}
