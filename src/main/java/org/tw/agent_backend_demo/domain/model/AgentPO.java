package org.tw.agent_backend_demo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * JPA entity representing an agent in the database.
 * Provides persistence mapping and conversion to domain model.
 */
@Entity
@Table(name = "agents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentPO {
    
    /**
     * Primary key identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    /**
     * Source type of the agent (fastgpt or hand)
     */
    @Column(name = "source", nullable = false)
    private String source;
    
    /**
     * Name of the agent (must be unique)
     */
    @Column(name = "agent_name", nullable = false, unique = true, length = 50)
    private String agentName;
    
    /**
     * List of tags associated with the agent
     */
    @ElementCollection
    @CollectionTable(name = "agent_tags", joinColumns = @JoinColumn(name = "agent_id"))
    @Column(name = "tag")
    private List<String> tags;
    
    /**
     * URL to the agent's icon image
     */
    @Column(name = "icon_url")
    private String iconUrl;
    
    /**
     * Description of the agent
     */
    @Column(name = "description", nullable = false, length = 500)
    private String description;
    
    /**
     * Category of the agent
     */
    @Column(name = "category", nullable = false)
    private String category;
    
    /**
     * URL of the target system this agent connects to
     */
    @Column(name = "target_system_url", nullable = false)
    private String targetSystemUrl;
    
    /**
     * Visibility scope configuration for the agent
     */
    @Embedded
    private VisibilityScope visibilityScope;
    
    /**
     * Creator of the agent
     */
    @Column(name = "creator", nullable = false)
    private String creator;
    
    /**
     * Creation timestamp
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * Converts this AgentPO persistence entity to an Agent domain model.
     * 
     * @return Agent domain object for business operations
     */
    public Agent toAgent() {
        return Agent.builder()
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
}
