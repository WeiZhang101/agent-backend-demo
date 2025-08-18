package org.tw.agent_backend_demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.tw.agent_backend_demo.domain.model.ScopeType;
import org.tw.agent_backend_demo.domain.model.VisibilityScope;
import org.tw.agent_backend_demo.dto.CreateAgentRequest;
import org.tw.agent_backend_demo.repository.AgentRepository;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for AgentController.
 * Tests the complete request-response cycle from HTTP to database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AgentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AgentRepository agentRepository;

    @Test
    void should_create_agent_successfully_when_post_agent_given_valid_request_end_to_end() throws Exception {
        // Given
        CreateAgentRequest request = CreateAgentRequest.builder()
                .source("fastgpt")
                .agentName("IntegrationTestAgent")
                .tags(List.of("AI", "Integration"))
                .iconUrl("https://example.com/integration-icon.png")
                .description("Integration test agent description")
                .category("Test")
                .targetSystemUrl("https://integration-test.com")
                .visibilityScope(VisibilityScope.builder()
                        .type(ScopeType.ORGANIZATION)
                        .values(List.of("engineering", "qa"))
                        .build())
                .build();

        // When & Then
        mockMvc.perform(post("/api/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(matchesPattern("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")))
                .andExpect(jsonPath("$.source").value("fastgpt"))
                .andExpect(jsonPath("$.agentName").value("IntegrationTestAgent"))
                .andExpect(jsonPath("$.tags", hasSize(2)))
                .andExpect(jsonPath("$.tags[0]").value("AI"))
                .andExpect(jsonPath("$.tags[1]").value("Integration"))
                .andExpect(jsonPath("$.iconUrl").value("https://example.com/integration-icon.png"))
                .andExpect(jsonPath("$.description").value("Integration test agent description"))
                .andExpect(jsonPath("$.category").value("Test"))
                .andExpect(jsonPath("$.targetSystemUrl").value("https://integration-test.com"))
                .andExpect(jsonPath("$.visibilityScope.type").value("ORGANIZATION"))
                .andExpect(jsonPath("$.visibilityScope.values", hasSize(2)))
                .andExpect(jsonPath("$.visibilityScope.values", containsInAnyOrder("engineering", "qa")))
                .andExpect(jsonPath("$.creator").value("system"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.createdAt").value(matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$")));

        // Verify database state
        assert(agentRepository.existsByAgentName("IntegrationTestAgent"));
        assert(agentRepository.findAll().size() >= 1);
    }

    @Test
    void should_handle_duplicate_name_error_when_post_agent_given_existing_name_end_to_end() throws Exception {
        // Given
        CreateAgentRequest firstRequest = CreateAgentRequest.builder()
                .source("fastgpt")
                .agentName("DuplicateTestAgent")
                .description("First agent description")
                .category("Test")
                .targetSystemUrl("https://first-test.com")
                .visibilityScope(VisibilityScope.builder()
                        .type(ScopeType.ORGANIZATION)
                        .values(List.of("org1"))
                        .build())
                .build();

        CreateAgentRequest secondRequest = CreateAgentRequest.builder()
                .source("hand")
                .agentName("DuplicateTestAgent") // Same name
                .description("Second agent description")
                .category("Different")
                .targetSystemUrl("https://second-test.com")
                .visibilityScope(VisibilityScope.builder()
                        .type(ScopeType.PERSONNEL)
                        .values(List.of("user1"))
                        .build())
                .build();

        // When
        // First request should succeed
        mockMvc.perform(post("/api/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isCreated());

        // Second request should fail with duplicate name error
        mockMvc.perform(post("/api/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondRequest)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("AGENT_NAME_EXISTS"))
                .andExpect(jsonPath("$.errorMessage").value(containsString("DuplicateTestAgent")))
                .andExpect(jsonPath("$.errorMessage").value(containsString("already exists")))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/agents"))
                .andExpect(jsonPath("$.details.duplicateName").value("DuplicateTestAgent"));

        // Verify database state - only one agent should exist
        assert(agentRepository.existsByAgentName("DuplicateTestAgent"));
        long count = agentRepository.findAll().stream()
                .filter(agent -> "DuplicateTestAgent".equals(agent.getAgentName()))
                .count();
        assert(count == 1);
    }

    @Test
    void should_handle_validation_errors_when_post_agent_given_invalid_request_end_to_end() throws Exception {
        // Given
        CreateAgentRequest invalidRequest = CreateAgentRequest.builder()
                .source("invalid-source") // Invalid source
                .agentName("") // Empty agent name
                .description("a".repeat(501)) // Description too long
                .category("") // Empty category
                .iconUrl("invalid-url") // Invalid URL format
                .targetSystemUrl("ftp://invalid.com") // Invalid URL format
                .visibilityScope(null) // Null visibility scope
                .build();

        // When & Then
        mockMvc.perform(post("/api/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/agents"))
                .andExpect(jsonPath("$.errorMessage").exists());

        // Verify no data was saved to database
        assert(!agentRepository.existsByAgentName(""));
        // The empty name should not create any agent
    }

    @Test
    void should_handle_visibility_scope_validation_when_post_agent_given_empty_scope_values_end_to_end() throws Exception {
        // Given
        CreateAgentRequest request = CreateAgentRequest.builder()
                .source("fastgpt")
                .agentName("EmptyScopeAgent")
                .description("Agent with empty visibility scope values")
                .category("Test")
                .targetSystemUrl("https://test.com")
                .visibilityScope(VisibilityScope.builder()
                        .type(ScopeType.ORGANIZATION)
                        .values(List.of()) // Empty values list
                        .build())
                .build();

        // When & Then
        mockMvc.perform(post("/api/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("INVALID_VISIBILITY_SCOPE"))
                .andExpect(jsonPath("$.errorMessage").value(containsString("empty")))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/agents"));

        // Verify no data was saved to database
        assert(!agentRepository.existsByAgentName("EmptyScopeAgent"));
    }

    @Test
    void should_support_different_source_types_when_post_agent_given_hand_source_end_to_end() throws Exception {
        // Given
        CreateAgentRequest request = CreateAgentRequest.builder()
                .source("hand") // Different source type
                .agentName("HandCreatedAgent")
                .description("Manually created agent")
                .category("Manual")
                .targetSystemUrl("https://manual.com")
                .visibilityScope(VisibilityScope.builder()
                        .type(ScopeType.PERSONNEL)
                        .values(List.of("admin", "operator"))
                        .build())
                .build();

        // When & Then
        mockMvc.perform(post("/api/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.source").value("hand"))
                .andExpect(jsonPath("$.agentName").value("HandCreatedAgent"))
                .andExpect(jsonPath("$.visibilityScope.type").value("PERSONNEL"))
                .andExpect(jsonPath("$.visibilityScope.values", containsInAnyOrder("admin", "operator")));

        // Verify database state
        assert(agentRepository.existsByAgentName("HandCreatedAgent"));
    }

    @Test
    void should_handle_minimal_valid_request_when_post_agent_given_only_required_fields_end_to_end() throws Exception {
        // Given
        CreateAgentRequest request = CreateAgentRequest.builder()
                .source("fastgpt")
                .agentName("MinimalAgent")
                .description("Minimal agent with only required fields")
                .category("Basic")
                .targetSystemUrl("https://minimal.com")
                .visibilityScope(VisibilityScope.builder()
                        .type(ScopeType.ORGANIZATION)
                        .values(List.of("default"))
                        .build())
                // No optional fields (tags, iconUrl)
                .build();

        // When & Then
        mockMvc.perform(post("/api/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.source").value("fastgpt"))
                .andExpect(jsonPath("$.agentName").value("MinimalAgent"))
                .andExpect(jsonPath("$.tags").doesNotExist())
                .andExpect(jsonPath("$.iconUrl").doesNotExist())
                .andExpect(jsonPath("$.description").value("Minimal agent with only required fields"))
                .andExpect(jsonPath("$.category").value("Basic"))
                .andExpect(jsonPath("$.targetSystemUrl").value("https://minimal.com"))
                .andExpect(jsonPath("$.creator").value("system"))
                .andExpect(jsonPath("$.createdAt").exists());

        // Verify database state
        assert(agentRepository.existsByAgentName("MinimalAgent"));
    }
}
