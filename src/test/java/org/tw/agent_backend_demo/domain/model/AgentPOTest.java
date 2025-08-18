package org.tw.agent_backend_demo.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for AgentPO persistence entity.
 * Tests entity mapping and conversion to domain model.
 */
class AgentPOTest {

    @Test
    void should_convert_to_agent_when_to_agent_given_valid_agent_po() {
        // Given
        UUID agentId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        VisibilityScope visibilityScope = VisibilityScope.builder()
                .type(ScopeType.ORGANIZATION)
                .values(List.of("org1", "org2"))
                .build();

        AgentPO agentPO = AgentPO.builder()
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
        Agent agent = agentPO.toAgent();

        // Then
        assertThat(agent).isNotNull();
        assertThat(agent.getId()).isEqualTo(agentId);
        assertThat(agent.getSource()).isEqualTo("fastgpt");
        assertThat(agent.getAgentName()).isEqualTo("TestAgent");
        assertThat(agent.getTags()).containsExactly("AI", "Test");
        assertThat(agent.getIconUrl()).isEqualTo("https://example.com/icon.png");
        assertThat(agent.getDescription()).isEqualTo("Test Description");
        assertThat(agent.getCategory()).isEqualTo("AI");
        assertThat(agent.getTargetSystemUrl()).isEqualTo("https://test.com");
        assertThat(agent.getVisibilityScope()).isEqualTo(visibilityScope);
        assertThat(agent.getCreator()).isEqualTo("testuser");
        assertThat(agent.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void should_handle_null_optional_fields_when_to_agent_given_agent_po_with_null_fields() {
        // Given
        UUID agentId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        VisibilityScope visibilityScope = VisibilityScope.builder()
                .type(ScopeType.PERSONNEL)
                .values(List.of("user1"))
                .build();

        AgentPO agentPO = AgentPO.builder()
                .id(agentId)
                .source("hand")
                .agentName("MinimalAgent")
                .tags(null) // Optional field
                .iconUrl(null) // Optional field
                .description("Minimal Description")
                .category("Basic")
                .targetSystemUrl("https://minimal.com")
                .visibilityScope(visibilityScope)
                .creator("admin")
                .createdAt(createdAt)
                .build();

        // When
        Agent agent = agentPO.toAgent();

        // Then
        assertThat(agent).isNotNull();
        assertThat(agent.getId()).isEqualTo(agentId);
        assertThat(agent.getSource()).isEqualTo("hand");
        assertThat(agent.getAgentName()).isEqualTo("MinimalAgent");
        assertThat(agent.getTags()).isNull();
        assertThat(agent.getIconUrl()).isNull();
        assertThat(agent.getDescription()).isEqualTo("Minimal Description");
        assertThat(agent.getCategory()).isEqualTo("Basic");
        assertThat(agent.getTargetSystemUrl()).isEqualTo("https://minimal.com");
        assertThat(agent.getVisibilityScope()).isEqualTo(visibilityScope);
        assertThat(agent.getCreator()).isEqualTo("admin");
        assertThat(agent.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void should_preserve_all_field_types_when_to_agent_given_complete_agent_po() {
        // Given
        UUID agentId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        List<String> tags = List.of("AI", "ML", "NLP");
        VisibilityScope visibilityScope = VisibilityScope.builder()
                .type(ScopeType.ORGANIZATION)
                .values(List.of("engineering", "research", "product"))
                .build();

        AgentPO agentPO = AgentPO.builder()
                .id(agentId)
                .source("fastgpt")
                .agentName("ComplexAgent")
                .tags(tags)
                .iconUrl("https://complex-example.com/icon.svg")
                .description("This is a complex agent with multiple tags and detailed configuration")
                .category("Advanced AI")
                .targetSystemUrl("https://complex-system.com/api")
                .visibilityScope(visibilityScope)
                .creator("system-admin")
                .createdAt(createdAt)
                .build();

        // When
        Agent agent = agentPO.toAgent();

        // Then
        assertThat(agent).isNotNull();
        assertThat(agent.getId()).isEqualTo(agentId);
        assertThat(agent.getSource()).isEqualTo("fastgpt");
        assertThat(agent.getAgentName()).isEqualTo("ComplexAgent");
        assertThat(agent.getTags()).containsExactlyElementsOf(tags);
        assertThat(agent.getIconUrl()).isEqualTo("https://complex-example.com/icon.svg");
        assertThat(agent.getDescription()).isEqualTo("This is a complex agent with multiple tags and detailed configuration");
        assertThat(agent.getCategory()).isEqualTo("Advanced AI");
        assertThat(agent.getTargetSystemUrl()).isEqualTo("https://complex-system.com/api");
        assertThat(agent.getVisibilityScope().getType()).isEqualTo(ScopeType.ORGANIZATION);
        assertThat(agent.getVisibilityScope().getValues()).containsExactly("engineering", "research", "product");
        assertThat(agent.getCreator()).isEqualTo("system-admin");
        assertThat(agent.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void should_handle_empty_collections_when_to_agent_given_agent_po_with_empty_lists() {
        // Given
        UUID agentId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        VisibilityScope visibilityScope = VisibilityScope.builder()
                .type(ScopeType.ORGANIZATION)
                .values(List.of("default-org"))
                .build();

        AgentPO agentPO = AgentPO.builder()
                .id(agentId)
                .source("hand")
                .agentName("EmptyTagsAgent")
                .tags(List.of()) // Empty list instead of null
                .iconUrl("https://example.com/icon.png")
                .description("Agent with empty tags list")
                .category("Test")
                .targetSystemUrl("https://test.com")
                .visibilityScope(visibilityScope)
                .creator("tester")
                .createdAt(createdAt)
                .build();

        // When
        Agent agent = agentPO.toAgent();

        // Then
        assertThat(agent).isNotNull();
        assertThat(agent.getTags()).isNotNull();
        assertThat(agent.getTags()).isEmpty();
        assertThat(agent.getVisibilityScope().getValues()).containsExactly("default-org");
    }
}
