package org.tw.agent_backend_demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tw.agent_backend_demo.models.VisibilityScope;
import org.tw.agent_backend_demo.models.enums.AgentSource;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAgentRequest {
    private AgentSource source;
    private String name;
    private List<String> tags;
    private String iconUrl;
    private String description;
    private String category;
    private String targetSystemUrl;
    private VisibilityScope visibilityScope;
} 