package org.tw.agent_backend_demo.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import org.tw.agent_backend_demo.domain.model.AgentPO;
import org.tw.agent_backend_demo.domain.model.ScopeType;
import org.tw.agent_backend_demo.domain.model.VisibilityScope;
import org.tw.agent_backend_demo.dto.CreateAgentRequest;
import org.tw.agent_backend_demo.dto.CreateAgentResponse;
import org.tw.agent_backend_demo.exception.AgentNameExistsException;
import org.tw.agent_backend_demo.exception.InvalidVisibilityScopeException;
import org.tw.agent_backend_demo.repository.AgentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for AgentServiceImpl.
 * Uses Mockito to test service layer business logic in isolation.
 */
@ExtendWith(MockitoExtension.class)
class AgentServiceImplTest {

    @Mock
    private AgentRepository agentRepository;

    @InjectMocks
    private AgentServiceImpl agentService;

    @Test
    void should_return_create_agent_response_when_create_agent_given_valid_request() {
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
        AgentPO savedAgentPO = AgentPO.builder()
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

        when(agentRepository.existsByAgentName("TestAgent")).thenReturn(false);
        when(agentRepository.save(any(AgentPO.class))).thenReturn(savedAgentPO);

        // When
        CreateAgentResponse response = agentService.createAgent(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(agentId);
        assertThat(response.getSource()).isEqualTo("fastgpt");
        assertThat(response.getAgentName()).isEqualTo("TestAgent");
        assertThat(response.getDescription()).isEqualTo("Test Description");
        assertThat(response.getCategory()).isEqualTo("AI");
        assertThat(response.getTargetSystemUrl()).isEqualTo("https://test.com");
        assertThat(response.getCreator()).isEqualTo("system");
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getVisibilityScope().getType()).isEqualTo(ScopeType.ORGANIZATION);
        assertThat(response.getVisibilityScope().getValues()).containsExactly("org1", "org2");

        verify(agentRepository, times(1)).existsByAgentName("TestAgent");
        verify(agentRepository, times(1)).save(any(AgentPO.class));
    }

    @Test
    void should_throw_agent_name_exists_exception_when_create_agent_given_duplicate_name() {
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

        when(agentRepository.existsByAgentName("ExistingAgent")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> agentService.createAgent(request))
                .isInstanceOf(AgentNameExistsException.class)
                .hasMessageContaining("ExistingAgent")
                .satisfies(ex -> {
                    AgentNameExistsException exception = (AgentNameExistsException) ex;
                    assertThat(exception.getErrorCode()).isEqualTo("AGENT_NAME_EXISTS");
                    assertThat(exception.getDuplicateName()).isEqualTo("ExistingAgent");
                });

        verify(agentRepository, times(1)).existsByAgentName("ExistingAgent");
        verify(agentRepository, never()).save(any(AgentPO.class));
    }

    @Test
    void should_throw_invalid_visibility_scope_exception_when_create_agent_given_empty_values() {
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

        when(agentRepository.existsByAgentName("TestAgent")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> agentService.createAgent(request))
                .isInstanceOf(InvalidVisibilityScopeException.class)
                .hasMessageContaining("empty")
                .satisfies(ex -> {
                    InvalidVisibilityScopeException exception = (InvalidVisibilityScopeException) ex;
                    assertThat(exception.getErrorCode()).isEqualTo("INVALID_VISIBILITY_SCOPE");
                });

        verify(agentRepository, times(1)).existsByAgentName("TestAgent");
        verify(agentRepository, never()).save(any(AgentPO.class));
    }

    @Test
    void should_throw_invalid_visibility_scope_exception_when_create_agent_given_null_values() {
        // Given
        CreateAgentRequest request = CreateAgentRequest.builder()
                .source("fastgpt")
                .agentName("TestAgent")
                .description("Test Description")
                .category("AI")
                .targetSystemUrl("https://test.com")
                .visibilityScope(VisibilityScope.builder()
                        .type(ScopeType.ORGANIZATION)
                        .values(null) // Null values list
                        .build())
                .build();

        when(agentRepository.existsByAgentName("TestAgent")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> agentService.createAgent(request))
                .isInstanceOf(InvalidVisibilityScopeException.class)
                .hasMessageContaining("empty")
                .satisfies(ex -> {
                    InvalidVisibilityScopeException exception = (InvalidVisibilityScopeException) ex;
                    assertThat(exception.getErrorCode()).isEqualTo("INVALID_VISIBILITY_SCOPE");
                });

        verify(agentRepository, times(1)).existsByAgentName("TestAgent");
        verify(agentRepository, never()).save(any(AgentPO.class));
    }

    @Test
    void should_handle_data_integrity_violation_when_create_agent_given_database_constraint_error() {
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

        when(agentRepository.existsByAgentName("TestAgent")).thenReturn(false);
        when(agentRepository.save(any(AgentPO.class)))
                .thenThrow(new DataIntegrityViolationException("Unique constraint violation"));

        // When & Then
        assertThatThrownBy(() -> agentService.createAgent(request))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("Unique constraint violation");

        verify(agentRepository, times(1)).existsByAgentName("TestAgent");
        verify(agentRepository, times(1)).save(any(AgentPO.class));
    }
}
