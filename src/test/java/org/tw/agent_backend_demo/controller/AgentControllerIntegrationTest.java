package org.tw.agent_backend_demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.tw.agent_backend_demo.dto.CreateAgentRequest;
import org.tw.agent_backend_demo.models.VisibilityScope;
import org.tw.agent_backend_demo.models.enums.AgentSource;
import org.tw.agent_backend_demo.models.enums.VisibilityType;
import org.tw.agent_backend_demo.repository.AgentDAO;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AgentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AgentDAO agentDAO;

    private CreateAgentRequest validRequest;

    @BeforeEach
    void setUp() {
        List<String> tags = Arrays.asList("大语言模型");
        VisibilityScope visibilityScope = new VisibilityScope(VisibilityType.ALL, Arrays.asList());
        
        validRequest = new CreateAgentRequest(
            AgentSource.FASTGPT,
            "集成测试智能体",
            tags,
            "https://example.com/icon.png",
            "这是一个集成测试智能体",
            "智能助手",
            "https://example.com/target",
            visibilityScope
        );
    }

    @Test
    void should_create_agent_end_to_end_when_post_agents_given_valid_request() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.name").value("集成测试智能体"))
            .andExpect(jsonPath("$.source").value("FASTGPT"))
            .andExpect(jsonPath("$.description").value("这是一个集成测试智能体"))
            .andExpect(jsonPath("$.category").value("智能助手"))
            .andExpect(jsonPath("$.creator").value("system"))
            .andExpect(jsonPath("$.tags[0]").value("大语言模型"))
            .andExpect(jsonPath("$.visibilityScope.type").value("ALL"))
            .andExpect(jsonPath("$.createdAt", notNullValue()));

        // 验证数据库中存在创建的智能体记录
        var savedAgent = agentDAO.findByName("集成测试智能体");
        assert savedAgent.isPresent();
        assert savedAgent.get().getName().equals("集成测试智能体");
        assert savedAgent.get().getSource() == AgentSource.FASTGPT;
    }

    @Test
    void should_return_conflict_end_to_end_when_post_agents_given_duplicate_name() throws Exception {
        // Given - 先创建一个智能体
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isCreated());

        // When & Then - 尝试创建同名智能体
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("AGENT_ALREADY_EXISTS"))
            .andExpect(jsonPath("$.message").value("Agent with name '集成测试智能体' already exists"))
            .andExpect(jsonPath("$.timestamp", notNullValue()))
            .andExpect(jsonPath("$.status").value(409));

        // 验证数据库中只有一条记录
        var agents = agentDAO.findAll();
        long count = agents.stream()
            .filter(agent -> agent.getName().equals("集成测试智能体"))
            .count();
        assert count == 1;
    }

    @Test
    void should_validate_request_end_to_end_when_post_agents_given_invalid_request() throws Exception {
        // Given - 无效的请求（缺少必需字段）
        CreateAgentRequest invalidRequest = new CreateAgentRequest();
        invalidRequest.setName("无效请求测试");
        // 缺少其他必需字段

        // When & Then
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());

        // 验证数据库中没有创建任何记录
        var savedAgent = agentDAO.findByName("无效请求测试");
        assert savedAgent.isEmpty();
    }

    @Test
    void should_handle_invalid_source_end_to_end_when_post_agents_given_null_source() throws Exception {
        // Given
        validRequest.setSource(null);

        // When & Then
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_SOURCE"))
            .andExpect(jsonPath("$.message").value("Agent source cannot be null"));

        // 验证数据库中没有创建记录
        var savedAgent = agentDAO.findByName("集成测试智能体");
        assert savedAgent.isEmpty();
    }

    @Test
    void should_handle_invalid_tags_end_to_end_when_post_agents_given_invalid_tags() throws Exception {
        // Given
        validRequest.setTags(Arrays.asList("无效标签"));

        // When & Then
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_TAG"))
            .andExpect(jsonPath("$.message").value("Invalid tag: 无效标签. Allowed tags are: [大语言模型, 语音模型, 图像模型]"));

        // 验证数据库中没有创建记录
        var savedAgent = agentDAO.findByName("集成测试智能体");
        assert savedAgent.isEmpty();
    }

    @Test
    void should_handle_invalid_category_end_to_end_when_post_agents_given_invalid_category() throws Exception {
        // Given
        validRequest.setCategory("无效分类");

        // When & Then
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_CATEGORY"))
            .andExpect(jsonPath("$.message").value("Invalid category: 无效分类. Allowed categories are: [智能助手, 效率工具]"));

        // 验证数据库中没有创建记录
        var savedAgent = agentDAO.findByName("集成测试智能体");
        assert savedAgent.isEmpty();
    }

    @Test
    void should_handle_invalid_icon_url_end_to_end_when_post_agents_given_invalid_url() throws Exception {
        // Given
        validRequest.setIconUrl("invalid-url");

        // When & Then
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_ICON_URL"))
            .andExpect(jsonPath("$.message").value("Icon URL must be a valid HTTP/HTTPS URL"));

        // 验证数据库中没有创建记录
        var savedAgent = agentDAO.findByName("集成测试智能体");
        assert savedAgent.isEmpty();
    }

    @Test
    void should_handle_invalid_target_url_end_to_end_when_post_agents_given_invalid_target_url() throws Exception {
        // Given
        validRequest.setTargetSystemUrl("invalid-url");

        // When & Then
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_TARGET_SYSTEM_URL"))
            .andExpect(jsonPath("$.message").value("Target system URL must be a valid HTTP/HTTPS URL"));

        // 验证数据库中没有创建记录
        var savedAgent = agentDAO.findByName("集成测试智能体");
        assert savedAgent.isEmpty();
    }

    @Test
    void should_handle_invalid_visibility_scope_end_to_end_when_post_agents_given_invalid_scope() throws Exception {
        // Given
        validRequest.setVisibilityScope(null);

        // When & Then
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_VISIBILITY_SCOPE"))
            .andExpect(jsonPath("$.message").value("Visibility scope cannot be null"));

        // 验证数据库中没有创建记录
        var savedAgent = agentDAO.findByName("集成测试智能体");
        assert savedAgent.isEmpty();
    }

    @Test
    void should_create_agent_with_different_sources_end_to_end() throws Exception {
        // Test FASTGPT source
        validRequest.setName("FASTGPT智能体");
        validRequest.setSource(AgentSource.FASTGPT);
        
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.source").value("FASTGPT"));

        // Test HAND source
        validRequest.setName("HAND智能体");
        validRequest.setSource(AgentSource.HAND);
        
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.source").value("HAND"));

        // 验证两个智能体都被创建
        var fastgptAgent = agentDAO.findByName("FASTGPT智能体");
        var handAgent = agentDAO.findByName("HAND智能体");
        
        assert fastgptAgent.isPresent();
        assert handAgent.isPresent();
        assert fastgptAgent.get().getSource() == AgentSource.FASTGPT;
        assert handAgent.get().getSource() == AgentSource.HAND;
    }

    @Test
    void should_create_agent_with_different_visibility_scopes_end_to_end() throws Exception {
        // Test ORGANIZATION scope
        validRequest.setName("组织智能体");
        validRequest.setVisibilityScope(new VisibilityScope(VisibilityType.ORGANIZATION, Arrays.asList("org1", "org2")));
        
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.visibilityScope.type").value("ORGANIZATION"))
            .andExpect(jsonPath("$.visibilityScope.values[0]").value("org1"))
            .andExpect(jsonPath("$.visibilityScope.values[1]").value("org2"));

        // Test PERSON scope
        validRequest.setName("个人智能体");
        validRequest.setVisibilityScope(new VisibilityScope(VisibilityType.PERSON, Arrays.asList("user1")));
        
        mockMvc.perform(post("/api/v1/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.visibilityScope.type").value("PERSON"))
            .andExpect(jsonPath("$.visibilityScope.values[0]").value("user1"));

        // 验证数据库中的可见范围正确保存
        var orgAgent = agentDAO.findByName("组织智能体");
        var personAgent = agentDAO.findByName("个人智能体");
        
        assert orgAgent.isPresent();
        assert personAgent.isPresent();
        assert orgAgent.get().getVisibilityType() == VisibilityType.ORGANIZATION;
        assert personAgent.get().getVisibilityType() == VisibilityType.PERSON;
    }
} 