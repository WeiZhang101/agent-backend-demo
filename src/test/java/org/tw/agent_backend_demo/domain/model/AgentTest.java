package org.tw.agent_backend_demo.domain.model;

import org.junit.jupiter.api.Test;
import org.tw.agent_backend_demo.dto.CreateAgentResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for Agent domain model.
 * Tests domain logic, conversions, and business operations.
 */
class AgentTest {

    @Test
    void should_convert_to_agent_po_when_to_agent_po_given_valid_agent() {
        // Given
        UUID agentId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        VisibilityScope visibilityScope = VisibilityScope.builder()
                .type(ScopeType.ORGANIZATION)
                .values(List.of("org1", "org2"))
                .build();

        Agent agent = Agent.builder()
                .id(agentId)
                .source("fastgpt")
                .agentName("TestAgent")
                .tags(List.of("AI", "Test"))
                .iconUrl("https://example.com/icon.png")
                .description("Test Description")
                .category("AI")
                .targetSystemUrl("https://test.com")
                .visibilityScope(visibilityScope)
                .creator("testuser")
                .createdAt(createdAt)
                .build();

        // When
        AgentPO agentPO = agent.toAgentPO();

        // Then
        assertThat(agentPO).isNotNull();
        assertThat(agentPO.getId()).isEqualTo(agentId);
        assertThat(agentPO.getSource()).isEqualTo("fastgpt");
        assertThat(agentPO.getAgentName()).isEqualTo("TestAgent");
        assertThat(agentPO.getTags()).containsExactly("AI", "Test");
        assertThat(agentPO.getIconUrl()).isEqualTo("https://example.com/icon.png");
        assertThat(agentPO.getDescription()).isEqualTo("Test Description");
        assertThat(agentPO.getCategory()).isEqualTo("AI");
        assertThat(agentPO.getTargetSystemUrl()).isEqualTo("https://test.com");
        assertThat(agentPO.getVisibilityScope()).isEqualTo(visibilityScope);
        assertThat(agentPO.getCreator()).isEqualTo("testuser");
        assertThat(agentPO.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void should_convert_to_create_agent_response_when_to_create_agent_response_given_valid_agent() {
        // Given
        UUID agentId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        VisibilityScope visibilityScope = VisibilityScope.builder()
                .type(ScopeType.PERSONNEL)
                .values(List.of("user1", "user2"))
                .build();

        Agent agent = Agent.builder()
                .id(agentId)
                .source("hand")
                .agentName("ManualAgent")
                .tags(List.of("Manual", "Custom"))
                .iconUrl("https://example.com/manual-icon.png")
                .description("Manually created agent")
                .category("Custom")
                .targetSystemUrl("https://manual.com")
                .visibilityScope(visibilityScope)
                .creator("admin")
                .createdAt(createdAt)
                .build();

        // When
        CreateAgentResponse response = agent.toCreateAgentResponse();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(agentId);
        assertThat(response.getSource()).isEqualTo("hand");
        assertThat(response.getAgentName()).isEqualTo("ManualAgent");
        assertThat(response.getTags()).containsExactly("Manual", "Custom");
        assertThat(response.getIconUrl()).isEqualTo("https://example.com/manual-icon.png");
        assertThat(response.getDescription()).isEqualTo("Manually created agent");
        assertThat(response.getCategory()).isEqualTo("Custom");
        assertThat(response.getTargetSystemUrl()).isEqualTo("https://manual.com");
        assertThat(response.getVisibilityScope()).isEqualTo(visibilityScope);
        assertThat(response.getCreator()).isEqualTo("admin");
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void should_initialize_system_fields_when_initialize_system_fields_given_creator() {
        // Given
        Agent agent = Agent.builder()
                .source("fastgpt")
                .agentName("NewAgent")
                .description("New Agent Description")
                .category("AI")
                .targetSystemUrl("https://new.com")
                .visibilityScope(VisibilityScope.builder()
                        .type(ScopeType.ORGANIZATION)
                        .values(List.of("org1"))
                        .build())
                .build();

        String creator = "testuser";

        // When
        agent.initializeSystemFields(creator);

        // Then
        assertThat(agent.getCreator()).isEqualTo("testuser");
        assertThat(agent.getCreatedAt()).isNotNull();
        assertThat(agent.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        
        // Verify that other fields remain unchanged
        assertThat(agent.getSource()).isEqualTo("fastgpt");
        assertThat(agent.getAgentName()).isEqualTo("NewAgent");
        assertThat(agent.getDescription()).isEqualTo("New Agent Description");
        assertThat(agent.getCategory()).isEqualTo("AI");
        assertThat(agent.getTargetSystemUrl()).isEqualTo("https://new.com");
    }

    @Test
    void should_update_creator_and_timestamp_when_initialize_system_fields_called_multiple_times() {
        // Given
        Agent agent = Agent.builder()
                .source("fastgpt")
                .agentName("Agent")
                .description("Description")
                .category("AI")
                .targetSystemUrl("https://test.com")
                .visibilityScope(VisibilityScope.builder()
                        .type(ScopeType.ORGANIZATION)
                        .values(List.of("org1"))
                        .build())
                .build();

        // When
        agent.initializeSystemFields("user1");
        LocalDateTime firstTimestamp = agent.getCreatedAt();
        
        // Small delay to ensure different timestamp
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        agent.initializeSystemFields("user2");

        // Then
        assertThat(agent.getCreator()).isEqualTo("user2");
        assertThat(agent.getCreatedAt()).isAfter(firstTimestamp);
    }
}
