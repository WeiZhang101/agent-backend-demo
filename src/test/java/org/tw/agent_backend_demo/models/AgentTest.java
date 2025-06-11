package org.tw.agent_backend_demo.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tw.agent_backend_demo.dto.CreateAgentRequest;
import org.tw.agent_backend_demo.models.enums.AgentSource;
import org.tw.agent_backend_demo.models.enums.VisibilityType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AgentTest {

    private CreateAgentRequest validRequest;
    private String creator;
    private AgentPO testAgentPO;

    @BeforeEach
    void setUp() {
        creator = "test-creator";
        List<String> tags = Arrays.asList("大语言模型", "语音模型");
        VisibilityScope visibilityScope = new VisibilityScope(VisibilityType.ORGANIZATION, Arrays.asList("org1", "org2"));
        
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

        LocalDateTime now = LocalDateTime.now();
        testAgentPO = new AgentPO(
            "test-id-123",
            AgentSource.FASTGPT,
            "测试智能体",
            "[\"大语言模型\",\"语音模型\"]",
            "https://example.com/icon.png",
            "这是一个测试智能体",
            "智能助手",
            "https://example.com/target",
            VisibilityType.ORGANIZATION,
            "[\"org1\",\"org2\"]",
            "test-creator",
            now,
            now
        );
    }

    @Test
    void should_create_agent_from_request_when_from_request_given_valid_request_and_creator() {
        // When
        Agent agent = Agent.fromRequest(validRequest, creator);

        // Then
        assertNotNull(agent);
        assertNotNull(agent.getId()); // UUID应该被生成
        assertEquals(validRequest.getSource(), agent.getSource());
        assertEquals(validRequest.getName(), agent.getName());
        assertEquals(validRequest.getTags(), agent.getTags());
        assertEquals(validRequest.getIconUrl(), agent.getIconUrl());
        assertEquals(validRequest.getDescription(), agent.getDescription());
        assertEquals(validRequest.getCategory(), agent.getCategory());
        assertEquals(validRequest.getTargetSystemUrl(), agent.getTargetSystemUrl());
        assertEquals(validRequest.getVisibilityScope(), agent.getVisibilityScope());
        assertEquals(creator, agent.getCreator());
        assertNotNull(agent.getCreatedAt());
        assertNotNull(agent.getUpdatedAt());
        
        // 验证createdAt和updatedAt是当前时间附近
        LocalDateTime now = LocalDateTime.now();
        assertTrue(agent.getCreatedAt().isBefore(now.plusSeconds(1)));
        assertTrue(agent.getUpdatedAt().isBefore(now.plusSeconds(1)));
    }

    @Test
    void should_initialize_empty_tags_when_from_request_given_null_tags() {
        // Given
        validRequest.setTags(null);

        // When
        Agent agent = Agent.fromRequest(validRequest, creator);

        // Then
        assertNotNull(agent.getTags());
        assertTrue(agent.getTags().isEmpty());
    }

    @Test
    void should_create_agent_from_po_when_from_po_given_valid_agent_po() {
        // When
        Agent agent = Agent.fromPO(testAgentPO);

        // Then
        assertNotNull(agent);
        assertEquals(testAgentPO.getId(), agent.getId());
        assertEquals(testAgentPO.getSource(), agent.getSource());
        assertEquals(testAgentPO.getName(), agent.getName());
        assertEquals(testAgentPO.getIconUrl(), agent.getIconUrl());
        assertEquals(testAgentPO.getDescription(), agent.getDescription());
        assertEquals(testAgentPO.getCategory(), agent.getCategory());
        assertEquals(testAgentPO.getTargetSystemUrl(), agent.getTargetSystemUrl());
        assertEquals(testAgentPO.getCreator(), agent.getCreator());
        assertEquals(testAgentPO.getCreatedAt(), agent.getCreatedAt());
        assertEquals(testAgentPO.getUpdatedAt(), agent.getUpdatedAt());

        // 验证tags从JSON字符串正确转换为列表
        assertNotNull(agent.getTags());
        assertEquals(2, agent.getTags().size());
        assertTrue(agent.getTags().contains("大语言模型"));
        assertTrue(agent.getTags().contains("语音模型"));

        // 验证visibilityScope正确重构
        assertNotNull(agent.getVisibilityScope());
        assertEquals(VisibilityType.ORGANIZATION, agent.getVisibilityScope().getType());
        assertEquals(2, agent.getVisibilityScope().getValues().size());
        assertTrue(agent.getVisibilityScope().getValues().contains("org1"));
        assertTrue(agent.getVisibilityScope().getValues().contains("org2"));
    }

    @Test
    void should_handle_empty_tags_json_when_from_po_given_empty_tags() {
        // Given
        testAgentPO.setTags("");

        // When
        Agent agent = Agent.fromPO(testAgentPO);

        // Then
        assertNotNull(agent.getTags());
        assertTrue(agent.getTags().isEmpty());
    }

    @Test
    void should_handle_null_tags_json_when_from_po_given_null_tags() {
        // Given
        testAgentPO.setTags(null);

        // When
        Agent agent = Agent.fromPO(testAgentPO);

        // Then
        assertNotNull(agent.getTags());
        assertTrue(agent.getTags().isEmpty());
    }

    @Test
    void should_handle_empty_visibility_values_json_when_from_po_given_empty_values() {
        // Given
        testAgentPO.setVisibilityValues("");

        // When
        Agent agent = Agent.fromPO(testAgentPO);

        // Then
        assertNotNull(agent.getVisibilityScope());
        assertNotNull(agent.getVisibilityScope().getValues());
        assertTrue(agent.getVisibilityScope().getValues().isEmpty());
    }

    @Test
    void should_handle_null_visibility_values_json_when_from_po_given_null_values() {
        // Given
        testAgentPO.setVisibilityValues(null);

        // When
        Agent agent = Agent.fromPO(testAgentPO);

        // Then
        assertNotNull(agent.getVisibilityScope());
        assertNotNull(agent.getVisibilityScope().getValues());
        assertTrue(agent.getVisibilityScope().getValues().isEmpty());
    }

    @Test
    void should_handle_invalid_json_gracefully_when_from_po_given_malformed_json() {
        // Given
        testAgentPO.setTags("invalid-json");
        testAgentPO.setVisibilityValues("invalid-json");

        // When
        Agent agent = Agent.fromPO(testAgentPO);

        // Then - 应该使用空列表作为fallback
        assertNotNull(agent.getTags());
        assertTrue(agent.getTags().isEmpty());
        assertNotNull(agent.getVisibilityScope().getValues());
        assertTrue(agent.getVisibilityScope().getValues().isEmpty());
    }

    @Test
    void should_preserve_all_fields_when_converting_from_request() {
        // Given - 包含所有字段的请求
        List<String> tags = Arrays.asList("大语言模型");
        VisibilityScope visibilityScope = new VisibilityScope(VisibilityType.PERSON, Arrays.asList("user1"));
        CreateAgentRequest fullRequest = new CreateAgentRequest(
            AgentSource.HAND,
            "完整智能体",
            tags,
            "https://example.com/full-icon.png",
            "这是一个完整的智能体描述",
            "效率工具",
            "https://example.com/full-target",
            visibilityScope
        );

        // When
        Agent agent = Agent.fromRequest(fullRequest, "full-creator");

        // Then
        assertEquals(AgentSource.HAND, agent.getSource());
        assertEquals("完整智能体", agent.getName());
        assertEquals(1, agent.getTags().size());
        assertEquals("大语言模型", agent.getTags().get(0));
        assertEquals("https://example.com/full-icon.png", agent.getIconUrl());
        assertEquals("这是一个完整的智能体描述", agent.getDescription());
        assertEquals("效率工具", agent.getCategory());
        assertEquals("https://example.com/full-target", agent.getTargetSystemUrl());
        assertEquals(VisibilityType.PERSON, agent.getVisibilityScope().getType());
        assertEquals(1, agent.getVisibilityScope().getValues().size());
        assertEquals("user1", agent.getVisibilityScope().getValues().get(0));
        assertEquals("full-creator", agent.getCreator());
    }
} 