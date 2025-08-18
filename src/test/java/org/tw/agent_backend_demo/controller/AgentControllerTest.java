package org.tw.agent_backend_demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.tw.agent_backend_demo.domain.model.ScopeType;
import org.tw.agent_backend_demo.domain.model.VisibilityScope;
import org.tw.agent_backend_demo.dto.CreateAgentRequest;
import org.tw.agent_backend_demo.dto.CreateAgentResponse;
import org.tw.agent_backend_demo.exception.AgentNameExistsException;
import org.tw.agent_backend_demo.exception.InvalidVisibilityScopeException;
import org.tw.agent_backend_demo.service.AgentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for AgentController.
 * Uses @WebMvcTest to test the web layer in isolation.
 */
@WebMvcTest(AgentController.class)
class AgentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgentService agentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_return_201_and_agent_response_when_create_agent_given_valid_request() throws Exception {
        // Given
        CreateAgentRequest request = CreateAgentRequest.builder()
                .source("fastgpt")
                .agentName("TestAgent")
                .description("Test Description")
                .category("AI")
                .targetSystemUrl("https://test.com")
                .visibilityScope(VisibilityScope.builder()
                        .type(ScopeType.ORGANIZATION)
                        .values(List.of("org1", "org2"))
                        .build())
                .build();

        UUID agentId = UUID.randomUUID();
        CreateAgentResponse response = CreateAgentResponse.builder()
                .id(agentId)
                .source("fastgpt")
                .agentName("TestAgent")
                .description("Test Description")
                .category("AI")
                .targetSystemUrl("https://test.com")
                .visibilityScope(VisibilityScope.builder()
                        .type(ScopeType.ORGANIZATION)
                        .values(List.of("org1", "org2"))
                        .build())
                .creator("system")
                .createdAt(LocalDateTime.now())
                .build();

        when(agentService.createAgent(any(CreateAgentRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(agentId.toString()))
                .andExpect(jsonPath("$.source").value("fastgpt"))
                .andExpect(jsonPath("$.agentName").value("TestAgent"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.category").value("AI"))
                .andExpect(jsonPath("$.targetSystemUrl").value("https://test.com"))
                .andExpect(jsonPath("$.creator").value("system"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.visibilityScope.type").value("ORGANIZATION"))
                .andExpect(jsonPath("$.visibilityScope.values[0]").value("org1"));

        verify(agentService, times(1)).createAgent(any(CreateAgentRequest.class));
    }

    @Test
    void should_return_400_when_create_agent_given_missing_required_fields() throws Exception {
        // Given
        CreateAgentRequest request = CreateAgentRequest.builder()
                .source("fastgpt")
                // Missing agentName, description, category, targetSystemUrl
                .build();

        // When & Then
        mockMvc.perform(post("/api/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").exists());

        verify(agentService, never()).createAgent(any(CreateAgentRequest.class));
    }

    @Test
    void should_return_400_when_create_agent_given_invalid_agent_name_length() throws Exception {
        // Given
        String longAgentName = "a".repeat(51); // Exceeds 50 character limit
        CreateAgentRequest request = CreateAgentRequest.builder()
                .source("fastgpt")
                .agentName(longAgentName)
                .description("Test Description")
                .category("AI")
                .targetSystemUrl("https://test.com")
                .visibilityScope(VisibilityScope.builder()
                        .type(ScopeType.ORGANIZATION)
                        .values(List.of("org1"))
                        .build())
                .build();

        // When & Then
        mockMvc.perform(post("/api/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.details.validationErrors.agentName").value("Agent name must not exceed 50 characters"));

        verify(agentService, never()).createAgent(any(CreateAgentRequest.class));
    }

    @Test
    void should_return_400_when_create_agent_given_invalid_description_length() throws Exception {
        // Given
        String longDescription = "a".repeat(501); // Exceeds 500 character limit
        CreateAgentRequest request = CreateAgentRequest.builder()
                .source("fastgpt")
                .agentName("TestAgent")
                .description(longDescription)
                .category("AI")
                .targetSystemUrl("https://test.com")
                .visibilityScope(VisibilityScope.builder()
                        .type(ScopeType.ORGANIZATION)
                        .values(List.of("org1"))
                        .build())
                .build();

        // When & Then
        mockMvc.perform(post("/api/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.details.validationErrors.description").value("Description must not exceed 500 characters"));

        verify(agentService, never()).createAgent(any(CreateAgentRequest.class));
    }

    @Test
    void should_return_400_when_create_agent_given_invalid_url_format() throws Exception {
        // Given
        CreateAgentRequest request = CreateAgentRequest.builder()
                .source("fastgpt")
                .agentName("TestAgent")
                .description("Test Description")
                .category("AI")
                .iconUrl("invalid-url")
                .targetSystemUrl("ftp://invalid.com")
                .visibilityScope(VisibilityScope.builder()
                        .type(ScopeType.ORGANIZATION)
                        .values(List.of("org1"))
                        .build())
                .build();

        // When & Then
        mockMvc.perform(post("/api/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.details.validationErrors.iconUrl").value("Icon URL must be a valid HTTP or HTTPS URL"))
                .andExpect(jsonPath("$.details.validationErrors.targetSystemUrl").value("Target system URL must be a valid HTTP or HTTPS URL"));

        verify(agentService, never()).createAgent(any(CreateAgentRequest.class));
    }

    @Test
    void should_return_409_when_create_agent_given_duplicate_agent_name() throws Exception {
        // Given
        CreateAgentRequest request = CreateAgentRequest.builder()
                .source("fastgpt")
                .agentName("ExistingAgent")
                .description("Test Description")
                .category("AI")
                .targetSystemUrl("https://test.com")
                .visibilityScope(VisibilityScope.builder()
                        .type(ScopeType.ORGANIZATION)
                        .values(List.of("org1"))
                        .build())
                .build();

        when(agentService.createAgent(any(CreateAgentRequest.class)))
                .thenThrow(new AgentNameExistsException("ExistingAgent"));

        // When & Then
        mockMvc.perform(post("/api/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("AGENT_NAME_EXISTS"))
                .andExpect(jsonPath("$.errorMessage").value(org.hamcrest.Matchers.containsString("ExistingAgent")))
                .andExpect(jsonPath("$.details.duplicateName").value("ExistingAgent"));

        verify(agentService, times(1)).createAgent(any(CreateAgentRequest.class));
    }

    @Test
    void should_return_400_when_create_agent_given_invalid_visibility_scope() throws Exception {
        // Given
        CreateAgentRequest request = CreateAgentRequest.builder()
                .source("fastgpt")
                .agentName("TestAgent")
                .description("Test Description")
                .category("AI")
                .targetSystemUrl("https://test.com")
                .visibilityScope(VisibilityScope.builder()
                        .type(ScopeType.ORGANIZATION)
                        .values(List.of()) // Empty values list
                        .build())
                .build();

        when(agentService.createAgent(any(CreateAgentRequest.class)))
                .thenThrow(new InvalidVisibilityScopeException("Visibility scope values cannot be empty"));

        // When & Then
        mockMvc.perform(post("/api/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("INVALID_VISIBILITY_SCOPE"))
                .andExpect(jsonPath("$.errorMessage").value(org.hamcrest.Matchers.containsString("empty")));

        verify(agentService, times(1)).createAgent(any(CreateAgentRequest.class));
    }

    @Test
    void should_return_500_when_create_agent_given_unexpected_error() throws Exception {
        // Given
        CreateAgentRequest request = CreateAgentRequest.builder()
                .source("fastgpt")
                .agentName("TestAgent")
                .description("Test Description")
                .category("AI")
                .targetSystemUrl("https://test.com")
                .visibilityScope(VisibilityScope.builder()
                        .type(ScopeType.ORGANIZATION)
                        .values(List.of("org1"))
                        .build())
                .build();

        when(agentService.createAgent(any(CreateAgentRequest.class)))
                .thenThrow(new RuntimeException("Unexpected database error"));

        // When & Then
        mockMvc.perform(post("/api/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("database"))));

        verify(agentService, times(1)).createAgent(any(CreateAgentRequest.class));
    }
}
