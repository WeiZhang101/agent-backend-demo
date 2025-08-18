package org.tw.agent_backend_demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tw.agent_backend_demo.domain.model.Agent;
import org.tw.agent_backend_demo.domain.model.VisibilityScope;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for agent creation responses.
 * Encapsulates complete agent information after successful creation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAgentResponse {
    
    /**
     * Unique identifier for the created agent
     */
    @JsonProperty("id")
    private UUID id;
    
    /**
     * Source type of the agent
     */
    @JsonProperty("source")
    private String source;
    
    /**
     * Name of the agent
     */
    @JsonProperty("agentName")
    private String agentName;
    
    /**
     * List of tags associated with the agent
     */
    @JsonProperty("tags")
    private List<String> tags;
    
    /**
     * URL to the agent's icon image
     */
    @JsonProperty("iconUrl")
    private String iconUrl;
    
    /**
     * Description of the agent
     */
    @JsonProperty("description")
    private String description;
    
    /**
     * Category of the agent
     */
    @JsonProperty("category")
    private String category;
    
    /**
     * URL of the target system this agent connects to
     */
    @JsonProperty("targetSystemUrl")
    private String targetSystemUrl;
    
    /**
     * Visibility scope configuration for the agent
     */
    @JsonProperty("visibilityScope")
    private VisibilityScope visibilityScope;
    
    /**
     * Creator of the agent
     */
    @JsonProperty("creator")
    private String creator;
    
    /**
     * Creation timestamp
     */
    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;
    
    /**
     * Creates a CreateAgentResponse from an Agent domain model.
     * 
     * @param agent the Agent domain object to convert
     * @return CreateAgentResponse object for API response
     */
    public static CreateAgentResponse fromAgent(Agent agent) {
        return CreateAgentResponse.builder()
                .id(agent.getId())
                .source(agent.getSource())
                .agentName(agent.getAgentName())
                .tags(agent.getTags())
                .iconUrl(agent.getIconUrl())
                .description(agent.getDescription())
                .category(agent.getCategory())
                .targetSystemUrl(agent.getTargetSystemUrl())
                .visibilityScope(agent.getVisibilityScope())
                .creator(agent.getCreator())
                .createdAt(agent.getCreatedAt())
                .build();
    }
}
