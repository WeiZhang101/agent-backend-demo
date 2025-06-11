package org.tw.agent_backend_demo.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.tw.agent_backend_demo.controller.AgentController;
import org.tw.agent_backend_demo.dto.CreateAgentRequest;
import org.tw.agent_backend_demo.models.VisibilityScope;
import org.tw.agent_backend_demo.models.enums.AgentSource;
import org.tw.agent_backend_demo.models.enums.VisibilityType;
import org.tw.agent_backend_demo.service.AgentService;

import java.util.Arrays;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AgentController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgentService agentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_handle_agent_already_exists_exception_when_exception_thrown() throws Exception {
        // Given
        CreateAgentRequest request = createValidRequest();
        when(agentService.createAgent(any(CreateAgentRequest.class), eq("system")))
            .thenThrow(new AgentAlreadyExistsException("Agent with name '测试智能体' already exists"));

        // When & Then
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.timestamp", notNullValue()))
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.error").value("Conflict"))
            .andExpect(jsonPath("$.code").value("AGENT_ALREADY_EXISTS"))
            .andExpect(jsonPath("$.message").value("Agent with name '测试智能体' already exists"));
    }

    @Test
    void should_handle_invalid_source_exception_when_validation_fails() throws Exception {
        // Given
        CreateAgentRequest request = createValidRequest();
        when(agentService.createAgent(any(CreateAgentRequest.class), eq("system")))
            .thenThrow(new InvalidSourceException("Agent source cannot be null"));

        // When & Then
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", notNullValue()))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.code").value("INVALID_SOURCE"))
            .andExpect(jsonPath("$.message").value("Agent source cannot be null"));
    }

    @Test
    void should_handle_invalid_tag_exception_when_validation_fails() throws Exception {
        // Given
        CreateAgentRequest request = createValidRequest();
        when(agentService.createAgent(any(CreateAgentRequest.class), eq("system")))
            .thenThrow(new InvalidTagException("Invalid tag: 无效标签. Allowed tags are: [大语言模型, 语音模型, 图像模型]"));

        // When & Then
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", notNullValue()))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.code").value("INVALID_TAG"))
            .andExpect(jsonPath("$.message").value("Invalid tag: 无效标签. Allowed tags are: [大语言模型, 语音模型, 图像模型]"));
    }

    @Test
    void should_handle_invalid_icon_url_exception_when_validation_fails() throws Exception {
        // Given
        CreateAgentRequest request = createValidRequest();
        when(agentService.createAgent(any(CreateAgentRequest.class), eq("system")))
            .thenThrow(new InvalidIconUrlException("Icon URL must be a valid HTTP/HTTPS URL"));

        // When & Then
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", notNullValue()))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.code").value("INVALID_ICON_URL"))
            .andExpect(jsonPath("$.message").value("Icon URL must be a valid HTTP/HTTPS URL"));
    }

    @Test
    void should_handle_invalid_category_exception_when_validation_fails() throws Exception {
        // Given
        CreateAgentRequest request = createValidRequest();
        when(agentService.createAgent(any(CreateAgentRequest.class), eq("system")))
            .thenThrow(new InvalidCategoryException("Invalid category: 无效分类. Allowed categories are: [智能助手, 效率工具]"));

        // When & Then
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", notNullValue()))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.code").value("INVALID_CATEGORY"))
            .andExpect(jsonPath("$.message").value("Invalid category: 无效分类. Allowed categories are: [智能助手, 效率工具]"));
    }

    @Test
    void should_handle_invalid_target_system_url_exception_when_validation_fails() throws Exception {
        // Given
        CreateAgentRequest request = createValidRequest();
        when(agentService.createAgent(any(CreateAgentRequest.class), eq("system")))
            .thenThrow(new InvalidTargetSystemUrlException("Target system URL must be a valid HTTP/HTTPS URL"));

        // When & Then
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", notNullValue()))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.code").value("INVALID_TARGET_SYSTEM_URL"))
            .andExpect(jsonPath("$.message").value("Target system URL must be a valid HTTP/HTTPS URL"));
    }

    @Test
    void should_handle_invalid_visibility_scope_exception_when_validation_fails() throws Exception {
        // Given
        CreateAgentRequest request = createValidRequest();
        when(agentService.createAgent(any(CreateAgentRequest.class), eq("system")))
            .thenThrow(new InvalidVisibilityScopeException("Visibility scope cannot be null"));

        // When & Then
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", notNullValue()))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.code").value("INVALID_VISIBILITY_SCOPE"))
            .andExpect(jsonPath("$.message").value("Visibility scope cannot be null"));
    }

    @Test
    void should_handle_generic_exception_when_unexpected_error_occurs() throws Exception {
        // Given
        CreateAgentRequest request = createValidRequest();
        when(agentService.createAgent(any(CreateAgentRequest.class), eq("system")))
            .thenThrow(new RuntimeException("Unexpected error occurred"));

        // When & Then
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.timestamp", notNullValue()))
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.error").value("Internal Server Error"))
            .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
            .andExpect(jsonPath("$.message").value("服务器内部错误"));
    }

    @Test
    void should_handle_null_pointer_exception_when_unexpected_null_occurs() throws Exception {
        // Given
        CreateAgentRequest request = createValidRequest();
        when(agentService.createAgent(any(CreateAgentRequest.class), eq("system")))
            .thenThrow(new NullPointerException("Null pointer exception"));

        // When & Then
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.timestamp", notNullValue()))
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.error").value("Internal Server Error"))
            .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
            .andExpect(jsonPath("$.message").value("服务器内部错误"));
    }

    @Test
    void should_ensure_consistent_error_response_format_for_all_exceptions() throws Exception {
        // Given - 测试不同异常的响应格式一致性
        CreateAgentRequest request = createValidRequest();
        
        // Test AgentAlreadyExistsException
        when(agentService.createAgent(any(CreateAgentRequest.class), eq("system")))
            .thenThrow(new AgentAlreadyExistsException("Test message"));

        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.status").exists())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.code").exists())
            .andExpect(jsonPath("$.message").exists());

        // Test InvalidSourceException
        when(agentService.createAgent(any(CreateAgentRequest.class), eq("system")))
            .thenThrow(new InvalidSourceException("Test message"));

        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.status").exists())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.code").exists())
            .andExpect(jsonPath("$.message").exists());

        // Test Generic Exception
        when(agentService.createAgent(any(CreateAgentRequest.class), eq("system")))
            .thenThrow(new RuntimeException("Test message"));

        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.status").exists())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.code").exists())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void should_handle_multiple_validation_exceptions_correctly() throws Exception {
        // Given
        CreateAgentRequest request = createValidRequest();
        
        // Test InvalidTagException
        when(agentService.createAgent(any(CreateAgentRequest.class), eq("system")))
            .thenThrow(new InvalidTagException("Invalid tag"));

        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_TAG"));

        // Test InvalidCategoryException
        when(agentService.createAgent(any(CreateAgentRequest.class), eq("system")))
            .thenThrow(new InvalidCategoryException("Invalid category"));

        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_CATEGORY"));

        // Test InvalidVisibilityScopeException
        when(agentService.createAgent(any(CreateAgentRequest.class), eq("system")))
            .thenThrow(new InvalidVisibilityScopeException("Invalid visibility scope"));

        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_VISIBILITY_SCOPE"));
    }

    private CreateAgentRequest createValidRequest() {
        return new CreateAgentRequest(
            AgentSource.FASTGPT,
            "测试智能体",
            Arrays.asList("大语言模型"),
            "https://example.com/icon.png",
            "这是一个测试智能体",
            "智能助手",
            "https://example.com/target",
            new VisibilityScope(VisibilityType.ALL, Arrays.asList())
        );
    }
} 