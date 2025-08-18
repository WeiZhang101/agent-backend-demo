package org.tw.agent_backend_demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tw.agent_backend_demo.domain.model.Agent;
import org.tw.agent_backend_demo.domain.model.VisibilityScope;

import java.util.List;

/**
 * DTO for agent creation requests.
 * Encapsulates and validates parameters for creating a new agent.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAgentRequest {
    
    /**
     * Source type of the agent (fastgpt or hand)
     */
    @NotBlank(message = "Source is required")
    @Pattern(regexp = "^(fastgpt|hand)$", message = "Source must be either 'fastgpt' or 'hand'")
    private String source;
    
    /**
     * Name of the agent (required, max 50 characters)
     */
    @NotBlank(message = "Agent name is required")
    @Size(max = 50, message = "Agent name must not exceed 50 characters")
    private String agentName;
    
    /**
     * List of tags associated with the agent (optional)
     */
    private List<String> tags;
    
    /**
     * URL to the agent's icon image (optional, must be valid URL if provided)
     */
    @Pattern(regexp = "^https?://.*", message = "Icon URL must be a valid HTTP or HTTPS URL")
    private String iconUrl;
    
    /**
     * Description of the agent (required, max 500 characters)
     */
    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    /**
     * Category of the agent (required)
     */
    @NotBlank(message = "Category is required")
    private String category;
    
    /**
     * URL of the target system this agent connects to (required, must be valid URL)
     */
    @NotBlank(message = "Target system URL is required")
    @Pattern(regexp = "^https?://.*", message = "Target system URL must be a valid HTTP or HTTPS URL")
    private String targetSystemUrl;
    
    /**
     * Visibility scope configuration for the agent (required)
     */
    @NotNull(message = "Visibility scope is required")
    @Valid
    private VisibilityScope visibilityScope;
    
    /**
     * Converts this CreateAgentRequest to an Agent domain model.
     * System fields (id, creator, createdAt) will be populated by the service layer.
     * 
     * @return Agent domain object with mapped properties
     */
    public Agent toAgent() {
        return Agent.builder()
                .source(this.source)
                .agentName(this.agentName)
                .tags(this.tags)
                .iconUrl(this.iconUrl)
                .description(this.description)
                .category(this.category)
                .targetSystemUrl(this.targetSystemUrl)
                .visibilityScope(this.visibilityScope)
                .build();
    }
}
