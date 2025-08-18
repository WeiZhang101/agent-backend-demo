package org.tw.agent_backend_demo.dto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.tw.agent_backend_demo.domain.model.Agent;
import org.tw.agent_backend_demo.domain.model.ScopeType;
import org.tw.agent_backend_demo.domain.model.VisibilityScope;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for CreateAgentRequest DTO.
 * Tests validation annotations and conversion to domain model.
 */
@SpringBootTest
@ActiveProfiles("test")
class CreateAgentRequestTest {

    @Test
    void should_convert_to_agent_when_to_agent_given_valid_request() {
        // Given
        CreateAgentRequest request = CreateAgentRequest.builder()
                .source("fastgpt")
                .agentName("TestAgent")
                .tags(List.of("AI", "ML"))
                .iconUrl("https://example.com/icon.png")
                .description("Test Description")
                .category("AI")
                .targetSystemUrl("https://test.com")
                .visibilityScope(VisibilityScope.builder()
                        .type(ScopeType.ORGANIZATION)
                        .values(List.of("org1", "org2"))
                        .build())
                .build();

        // When
        Agent agent = request.toAgent();

        // Then
        assertThat(agent).isNotNull();
        assertThat(agent.getSource()).isEqualTo("fastgpt");
        assertThat(agent.getAgentName()).isEqualTo("TestAgent");
        assertThat(agent.getTags()).containsExactly("AI", "ML");
        assertThat(agent.getIconUrl()).isEqualTo("https://example.com/icon.png");
        assertThat(agent.getDescription()).isEqualTo("Test Description");
        assertThat(agent.getCategory()).isEqualTo("AI");
        assertThat(agent.getTargetSystemUrl()).isEqualTo("https://test.com");
        assertThat(agent.getVisibilityScope().getType()).isEqualTo(ScopeType.ORGANIZATION);
        assertThat(agent.getVisibilityScope().getValues()).containsExactly("org1", "org2");
        
        // System fields should not be set
        assertThat(agent.getId()).isNull();
        assertThat(agent.getCreator()).isNull();
        assertThat(agent.getCreatedAt()).isNull();
    }

    @Test
    void should_convert_to_agent_when_to_agent_given_minimal_valid_request() {
        // Given
        CreateAgentRequest request = CreateAgentRequest.builder()
                .source("hand")
                .agentName("MinimalAgent")
                .description("Minimal Description")
                .category("Basic")
                .targetSystemUrl("https://minimal.com")
                .visibilityScope(VisibilityScope.builder()
                        .type(ScopeType.PERSONNEL)
                        .values(List.of("user1"))
                        .build())
                .build();

        // When
        Agent agent = request.toAgent();

        // Then
        assertThat(agent).isNotNull();
        assertThat(agent.getSource()).isEqualTo("hand");
        assertThat(agent.getAgentName()).isEqualTo("MinimalAgent");
        assertThat(agent.getTags()).isNull();
        assertThat(agent.getIconUrl()).isNull();
        assertThat(agent.getDescription()).isEqualTo("Minimal Description");
        assertThat(agent.getCategory()).isEqualTo("Basic");
        assertThat(agent.getTargetSystemUrl()).isEqualTo("https://minimal.com");
        assertThat(agent.getVisibilityScope().getType()).isEqualTo(ScopeType.PERSONNEL);
        assertThat(agent.getVisibilityScope().getValues()).containsExactly("user1");
    }

    @Test
    void should_handle_empty_collections_when_to_agent_given_request_with_empty_tags() {
        // Given
        CreateAgentRequest request = CreateAgentRequest.builder()
                .source("hand")
                .agentName("EmptyTagsAgent")
                .tags(List.of()) // Empty tags list
                .description("Agent with empty tags")
                .category("Test")
                .targetSystemUrl("https://test.com")
                .visibilityScope(VisibilityScope.builder()
                        .type(ScopeType.PERSONNEL)
                        .values(List.of("user1"))
                        .build())
                .build();

        // When
        Agent agent = request.toAgent();

        // Then
        assertThat(agent).isNotNull();
        assertThat(agent.getTags()).isNotNull();
        assertThat(agent.getTags()).isEmpty();
        assertThat(agent.getAgentName()).isEqualTo("EmptyTagsAgent");
    }

    @Test
    void should_preserve_all_field_types_when_to_agent_given_complete_request() {
        // Given
        List<String> tags = List.of("AI", "ML", "NLP", "ChatBot");
        CreateAgentRequest request = CreateAgentRequest.builder()
                .source("fastgpt")
                .agentName("CompleteAgent")
                .tags(tags)
                .iconUrl("https://complete-example.com/icon.svg")
                .description("This is a complete agent request with all fields populated for testing")
                .category("Advanced AI")
                .targetSystemUrl("https://complete-system.com/api/v1")
                .visibilityScope(VisibilityScope.builder()
                        .type(ScopeType.ORGANIZATION)
                        .values(List.of("engineering", "research", "product", "qa"))
                        .build())
                .build();

        // When
        Agent agent = request.toAgent();

        // Then
        assertThat(agent).isNotNull();
        assertThat(agent.getSource()).isEqualTo("fastgpt");
        assertThat(agent.getAgentName()).isEqualTo("CompleteAgent");
        assertThat(agent.getTags()).containsExactlyElementsOf(tags);
        assertThat(agent.getIconUrl()).isEqualTo("https://complete-example.com/icon.svg");
        assertThat(agent.getDescription()).isEqualTo("This is a complete agent request with all fields populated for testing");
        assertThat(agent.getCategory()).isEqualTo("Advanced AI");
        assertThat(agent.getTargetSystemUrl()).isEqualTo("https://complete-system.com/api/v1");
        assertThat(agent.getVisibilityScope().getType()).isEqualTo(ScopeType.ORGANIZATION);
        assertThat(agent.getVisibilityScope().getValues()).containsExactly("engineering", "research", "product", "qa");
        
        // Ensure system fields are not set during conversion
        assertThat(agent.getId()).isNull();
        assertThat(agent.getCreator()).isNull();
        assertThat(agent.getCreatedAt()).isNull();
    }
}
