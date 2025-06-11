package org.tw.agent_backend_demo.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tw.agent_backend_demo.models.enums.AgentSource;
import org.tw.agent_backend_demo.models.enums.VisibilityType;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "agents", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
@AllArgsConstructor
@NoArgsConstructor
public class AgentPO {
    @Id
    private String id;
    
    @Enumerated(EnumType.STRING)
    private AgentSource source;
    
    @Column(unique = true, length = 50)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String tags;
    
    private String iconUrl;
    
    @Column(length = 500)
    private String description;
    
    private String category;
    
    private String targetSystemUrl;
    
    @Enumerated(EnumType.STRING)
    private VisibilityType visibilityType;
    
    @Column(columnDefinition = "TEXT")
    private String visibilityValues;
    
    private String creator;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    public static AgentPO of(Agent entity) {
        ObjectMapper objectMapper = new ObjectMapper();
        String tagsJson = "";
        String visibilityValuesJson = "";
        
        try {
            if (entity.getTags() != null) {
                tagsJson = objectMapper.writeValueAsString(entity.getTags());
            }
            if (entity.getVisibilityScope() != null && entity.getVisibilityScope().getValues() != null) {
                visibilityValuesJson = objectMapper.writeValueAsString(entity.getVisibilityScope().getValues());
            }
        } catch (JsonProcessingException e) {
            // Handle JSON serialization error, use empty strings as fallback
        }
        
        VisibilityType visibilityType = entity.getVisibilityScope() != null ? 
            entity.getVisibilityScope().getType() : VisibilityType.ALL;
        
        return new AgentPO(
            entity.getId(),
            entity.getSource(),
            entity.getName(),
            tagsJson,
            entity.getIconUrl(),
            entity.getDescription(),
            entity.getCategory(),
            entity.getTargetSystemUrl(),
            visibilityType,
            visibilityValuesJson,
            entity.getCreator(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
} 