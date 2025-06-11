package org.tw.agent_backend_demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.tw.agent_backend_demo.dto.CreateAgentRequest;
import org.tw.agent_backend_demo.exceptions.*;
import org.tw.agent_backend_demo.models.Agent;
import org.tw.agent_backend_demo.models.VisibilityScope;
import org.tw.agent_backend_demo.models.enums.AgentSource;
import org.tw.agent_backend_demo.models.enums.VisibilityType;
import org.tw.agent_backend_demo.service.AgentService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AgentController.class)
class AgentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgentService agentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_return_created_agent_when_create_agent_given_valid_request() throws Exception {
        // Given
        List<String> tags = Arrays.asList("大语言模型");
        VisibilityScope visibilityScope = new VisibilityScope(VisibilityType.ALL, Arrays.asList());
        CreateAgentRequest request = new CreateAgentRequest(
            AgentSource.FASTGPT,
            "测试智能体",
            tags,
            "https://example.com/icon.png",
            "这是一个测试智能体",
            "智能助手",
            "https://example.com/target",
            visibilityScope
        );

        Agent mockAgent = new Agent(
            "test-id-123",
            AgentSource.FASTGPT,
            "测试智能体",
            tags,
            "https://example.com/icon.png",
            "这是一个测试智能体",
            "智能助手",
            "https://example.com/target",
            visibilityScope,
            "system",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        when(agentService.createAgent(any(CreateAgentRequest.class), eq("system")))
            .thenReturn(mockAgent);

        // When & Then
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("test-id-123"))
            .andExpect(jsonPath("$.name").value("测试智能体"))
            .andExpect(jsonPath("$.source").value("FASTGPT"))
            .andExpect(jsonPath("$.description").value("这是一个测试智能体"))
            .andExpect(jsonPath("$.category").value("智能助手"))
            .andExpect(jsonPath("$.creator").value("system"));

        verify(agentService, times(1)).createAgent(any(CreateAgentRequest.class), eq("system"));
    }

    @Test
    void should_return_bad_request_when_create_agent_given_missing_required_fields() throws Exception {
        // Given - 缺少必需字段的请求
        CreateAgentRequest request = new CreateAgentRequest();
        request.setName("测试智能体");
        // 缺少其他必需字段

        // Mock service to throw validation exception
        when(agentService.createAgent(any(CreateAgentRequest.class), eq("system")))
            .thenThrow(new InvalidSourceException("Agent source cannot be null"));

        // When & Then
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_SOURCE"))
            .andExpect(jsonPath("$.message").value("Agent source cannot be null"));

        verify(agentService, times(1)).createAgent(any(CreateAgentRequest.class), eq("system"));
    }

    @Test
    void should_return_conflict_when_create_agent_given_duplicate_name() throws Exception {
        // Given
        List<String> tags = Arrays.asList("大语言模型");
        VisibilityScope visibilityScope = new VisibilityScope(VisibilityType.ALL, Arrays.asList());
        CreateAgentRequest request = new CreateAgentRequest(
            AgentSource.FASTGPT,
            "重复名称智能体",
            tags,
            "https://example.com/icon.png",
            "这是一个测试智能体",
            "智能助手",
            "https://example.com/target",
            visibilityScope
        );

        when(agentService.createAgent(any(CreateAgentRequest.class), eq("system")))
            .thenThrow(new AgentAlreadyExistsException("Agent with name '重复名称智能体' already exists"));

        // When & Then
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("AGENT_ALREADY_EXISTS"))
            .andExpect(jsonPath("$.message").value("Agent with name '重复名称智能体' already exists"));
    }

    @Test
    void should_return_bad_request_when_create_agent_given_invalid_source() throws Exception {
        // Given
        List<String> tags = Arrays.asList("大语言模型");
        VisibilityScope visibilityScope = new VisibilityScope(VisibilityType.ALL, Arrays.asList());
        CreateAgentRequest request = new CreateAgentRequest(
            null, // 无效的source
            "测试智能体",
            tags,
            "https://example.com/icon.png",
            "这是一个测试智能体",
            "智能助手",
            "https://example.com/target",
            visibilityScope
        );

        when(agentService.createAgent(any(CreateAgentRequest.class), eq("system")))
            .thenThrow(new InvalidSourceException("Agent source cannot be null"));

        // When & Then
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_SOURCE"))
            .andExpect(jsonPath("$.message").value("Agent source cannot be null"));
    }

    @Test
    void should_return_bad_request_when_create_agent_given_invalid_url_format() throws Exception {
        // Given
        List<String> tags = Arrays.asList("大语言模型");
        VisibilityScope visibilityScope = new VisibilityScope(VisibilityType.ALL, Arrays.asList());
        CreateAgentRequest request = new CreateAgentRequest(
            AgentSource.FASTGPT,
            "测试智能体",
            tags,
            "invalid-url", // 无效的URL格式
            "这是一个测试智能体",
            "智能助手",
            "https://example.com/target",
            visibilityScope
        );

        when(agentService.createAgent(any(CreateAgentRequest.class), eq("system")))
            .thenThrow(new InvalidIconUrlException("Icon URL must be a valid HTTP/HTTPS URL"));

        // When & Then
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_ICON_URL"))
            .andExpect(jsonPath("$.message").value("Icon URL must be a valid HTTP/HTTPS URL"));
    }
} 