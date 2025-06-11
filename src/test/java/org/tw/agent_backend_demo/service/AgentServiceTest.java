package org.tw.agent_backend_demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tw.agent_backend_demo.dto.CreateAgentRequest;
import org.tw.agent_backend_demo.exceptions.*;
import org.tw.agent_backend_demo.models.Agent;
import org.tw.agent_backend_demo.models.VisibilityScope;
import org.tw.agent_backend_demo.models.enums.AgentSource;
import org.tw.agent_backend_demo.models.enums.VisibilityType;
import org.tw.agent_backend_demo.repository.AgentRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgentServiceTest {

    @Mock
    private AgentRepository agentRepository;

    @InjectMocks
    private AgentServiceImpl agentService;

    private CreateAgentRequest validRequest;
    private String creator;

    @BeforeEach
    void setUp() {
        creator = "test-user";
        List<String> tags = Arrays.asList("大语言模型");
        VisibilityScope visibilityScope = new VisibilityScope(VisibilityType.ALL, Arrays.asList());
        
        validRequest = new CreateAgentRequest(
            AgentSource.FASTGPT,
            "测试智能体",
            tags,
            "https://example.com/icon.png",
            "这是一个测试智能体",
            "智能助手",
            "https://example.com/target",
            visibilityScope
        );
    }

    @Test
    void should_create_agent_successfully_when_create_agent_given_valid_request() {
        // Given
        Agent mockAgent = Agent.fromRequest(validRequest, creator);
        when(agentRepository.findByName(validRequest.getName())).thenReturn(Optional.empty());
        when(agentRepository.save(any(Agent.class))).thenReturn(mockAgent);

        // When
        Agent result = agentService.createAgent(validRequest, creator);

        // Then
        assertNotNull(result);
        assertEquals(validRequest.getName(), result.getName());
        assertEquals(validRequest.getSource(), result.getSource());
        assertEquals(validRequest.getDescription(), result.getDescription());
        assertEquals(validRequest.getCategory(), result.getCategory());
        assertEquals(creator, result.getCreator());
        assertNotNull(result.getId());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(agentRepository, times(1)).findByName(validRequest.getName());
        verify(agentRepository, times(1)).save(any(Agent.class));
    }

    @Test
    void should_throw_agent_already_exists_exception_when_create_agent_given_duplicate_name() {
        // Given
        Agent existingAgent = Agent.fromRequest(validRequest, creator);
        when(agentRepository.findByName(validRequest.getName())).thenReturn(Optional.of(existingAgent));

        // When & Then
        AgentAlreadyExistsException exception = assertThrows(
            AgentAlreadyExistsException.class,
            () -> agentService.createAgent(validRequest, creator)
        );

        assertTrue(exception.getMessage().contains("测试智能体"));
        verify(agentRepository, times(1)).findByName(validRequest.getName());
        verify(agentRepository, never()).save(any(Agent.class));
    }

    @Test
    void should_throw_invalid_source_exception_when_create_agent_given_invalid_source() {
        // Given
        validRequest.setSource(null);

        // When & Then
        InvalidSourceException exception = assertThrows(
            InvalidSourceException.class,
            () -> agentService.createAgent(validRequest, creator)
        );

        assertEquals("Agent source cannot be null", exception.getMessage());
        verify(agentRepository, never()).findByName(any(String.class));
        verify(agentRepository, never()).save(any(Agent.class));
    }

    @Test
    void should_throw_invalid_tag_exception_when_create_agent_given_invalid_tags() {
        // Given
        List<String> invalidTags = Arrays.asList("无效标签");
        validRequest.setTags(invalidTags);

        // When & Then
        InvalidTagException exception = assertThrows(
            InvalidTagException.class,
            () -> agentService.createAgent(validRequest, creator)
        );

        assertTrue(exception.getMessage().contains("无效标签"));
        verify(agentRepository, never()).findByName(any(String.class));
        verify(agentRepository, never()).save(any(Agent.class));
    }

    @Test
    void should_throw_invalid_icon_url_exception_when_create_agent_given_invalid_icon_url() {
        // Given
        validRequest.setIconUrl("invalid-url");

        // When & Then
        InvalidIconUrlException exception = assertThrows(
            InvalidIconUrlException.class,
            () -> agentService.createAgent(validRequest, creator)
        );

        assertEquals("Icon URL must be a valid HTTP/HTTPS URL", exception.getMessage());
        verify(agentRepository, never()).findByName(any(String.class));
        verify(agentRepository, never()).save(any(Agent.class));
    }

    @Test
    void should_throw_invalid_category_exception_when_create_agent_given_invalid_category() {
        // Given
        validRequest.setCategory("无效分类");

        // When & Then
        InvalidCategoryException exception = assertThrows(
            InvalidCategoryException.class,
            () -> agentService.createAgent(validRequest, creator)
        );

        assertTrue(exception.getMessage().contains("无效分类"));
        verify(agentRepository, never()).findByName(any(String.class));
        verify(agentRepository, never()).save(any(Agent.class));
    }

    @Test
    void should_throw_invalid_target_system_url_exception_when_create_agent_given_invalid_target_url() {
        // Given
        validRequest.setTargetSystemUrl("invalid-url");

        // When & Then
        InvalidTargetSystemUrlException exception = assertThrows(
            InvalidTargetSystemUrlException.class,
            () -> agentService.createAgent(validRequest, creator)
        );

        assertEquals("Target system URL must be a valid HTTP/HTTPS URL", exception.getMessage());
        verify(agentRepository, never()).findByName(any(String.class));
        verify(agentRepository, never()).save(any(Agent.class));
    }

    @Test
    void should_throw_invalid_target_system_url_exception_when_create_agent_given_null_target_url() {
        // Given
        validRequest.setTargetSystemUrl(null);

        // When & Then
        InvalidTargetSystemUrlException exception = assertThrows(
            InvalidTargetSystemUrlException.class,
            () -> agentService.createAgent(validRequest, creator)
        );

        assertEquals("Target system URL cannot be null or empty", exception.getMessage());
        verify(agentRepository, never()).findByName(any(String.class));
        verify(agentRepository, never()).save(any(Agent.class));
    }

    @Test
    void should_throw_invalid_visibility_scope_exception_when_create_agent_given_invalid_visibility_scope() {
        // Given
        validRequest.setVisibilityScope(null);

        // When & Then
        InvalidVisibilityScopeException exception = assertThrows(
            InvalidVisibilityScopeException.class,
            () -> agentService.createAgent(validRequest, creator)
        );

        assertEquals("Visibility scope cannot be null", exception.getMessage());
        verify(agentRepository, never()).findByName(any(String.class));
        verify(agentRepository, never()).save(any(Agent.class));
    }

    @Test
    void should_throw_invalid_visibility_scope_exception_when_create_agent_given_organization_type_with_empty_values() {
        // Given
        VisibilityScope invalidScope = new VisibilityScope(VisibilityType.ORGANIZATION, Arrays.asList());
        validRequest.setVisibilityScope(invalidScope);

        // When & Then
        InvalidVisibilityScopeException exception = assertThrows(
            InvalidVisibilityScopeException.class,
            () -> agentService.createAgent(validRequest, creator)
        );

        assertTrue(exception.getMessage().contains("Visibility values cannot be empty for ORGANIZATION type"));
        verify(agentRepository, never()).findByName(any(String.class));
        verify(agentRepository, never()).save(any(Agent.class));
    }

    @Test
    void should_throw_invalid_visibility_scope_exception_when_create_agent_given_person_type_with_empty_values() {
        // Given
        VisibilityScope invalidScope = new VisibilityScope(VisibilityType.PERSON, Arrays.asList());
        validRequest.setVisibilityScope(invalidScope);

        // When & Then
        InvalidVisibilityScopeException exception = assertThrows(
            InvalidVisibilityScopeException.class,
            () -> agentService.createAgent(validRequest, creator)
        );

        assertTrue(exception.getMessage().contains("Visibility values cannot be empty for PERSON type"));
        verify(agentRepository, never()).findByName(any(String.class));
        verify(agentRepository, never()).save(any(Agent.class));
    }
} 