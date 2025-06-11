package org.tw.agent_backend_demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tw.agent_backend_demo.models.Agent;
import org.tw.agent_backend_demo.models.VisibilityScope;
import org.tw.agent_backend_demo.models.enums.AgentSource;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentResponse {
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

    public static AgentResponse of(Agent entity) {
        return new AgentResponse(
            entity.getId(),
            entity.getSource(),
            entity.getName(),
            entity.getTags(),
            entity.getIconUrl(),
            entity.getDescription(),
            entity.getCategory(),
            entity.getTargetSystemUrl(),
            entity.getVisibilityScope(),
            entity.getCreator(),
            entity.getCreatedAt()
        );
    }
} 