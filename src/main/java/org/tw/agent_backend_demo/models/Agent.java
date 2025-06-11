package org.tw.agent_backend_demo.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tw.agent_backend_demo.dto.CreateAgentRequest;
import org.tw.agent_backend_demo.models.enums.AgentSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Agent {
    private String id;
    private AgentSource source;
    private String name;
    private List<String> tags;
    private String iconUrl;
    private String description;
    private String category;
    private String targetSystemUrl;
    private VisibilityScope visibilityScope;
    private String creator;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Agent fromRequest(CreateAgentRequest request, String creator) {
        LocalDateTime now = LocalDateTime.now();
        List<String> tags = request.getTags() != null ? request.getTags() : new ArrayList<>();
        
        return new Agent(
            UUID.randomUUID().toString(),
            request.getSource(),
            request.getName(),
            tags,
            request.getIconUrl(),
            request.getDescription(),
            request.getCategory(),
            request.getTargetSystemUrl(),
            request.getVisibilityScope(),
            creator,
            now,
            now
        );
    }

    public static Agent fromPO(AgentPO po) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> tags = new ArrayList<>();
        List<String> visibilityValues = new ArrayList<>();
        
        try {
            if (po.getTags() != null && !po.getTags().isEmpty()) {
                tags = objectMapper.readValue(po.getTags(), new TypeReference<List<String>>() {});
            }
            if (po.getVisibilityValues() != null && !po.getVisibilityValues().isEmpty()) {
                visibilityValues = objectMapper.readValue(po.getVisibilityValues(), new TypeReference<List<String>>() {});
            }
        } catch (JsonProcessingException e) {
            // Handle JSON parsing error, use empty lists as fallback
        }
        
        VisibilityScope visibilityScope = new VisibilityScope(po.getVisibilityType(), visibilityValues);
        
        return new Agent(
            po.getId(),
            po.getSource(),
            po.getName(),
            tags,
            po.getIconUrl(),
            po.getDescription(),
            po.getCategory(),
            po.getTargetSystemUrl(),
            visibilityScope,
            po.getCreator(),
            po.getCreatedAt(),
            po.getUpdatedAt()
        );
    }
} 