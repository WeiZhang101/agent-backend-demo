package org.tw.agent_backend_demo.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tw.agent_backend_demo.models.Agent;
import org.tw.agent_backend_demo.models.VisibilityScope;
import org.tw.agent_backend_demo.models.enums.AgentSource;
import org.tw.agent_backend_demo.models.enums.VisibilityType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AgentResponseTest {

    private Agent testAgent;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        testTime = LocalDateTime.now();
        List<String> tags = Arrays.asList("大语言模型", "语音模型");
        VisibilityScope visibilityScope = new VisibilityScope(VisibilityType.ORGANIZATION, Arrays.asList("org1", "org2"));
        
        testAgent = new Agent(
            "test-id-123",
            AgentSource.FASTGPT,
            "测试智能体",
            tags,
            "https://example.com/icon.png",
            "这是一个测试智能体",
            "智能助手",
            "https://example.com/target",
            visibilityScope,
            "test-creator",
            testTime,
            testTime
        );
    }

    @Test
    void should_create_response_from_agent_when_of_given_valid_agent() {
        // When
        AgentResponse response = AgentResponse.of(testAgent);

        // Then
        assertNotNull(response);
        assertEquals(testAgent.getId(), response.getId());
        assertEquals(testAgent.getSource(), response.getSource());
        assertEquals(testAgent.getName(), response.getName());
        assertEquals(testAgent.getTags(), response.getTags());
        assertEquals(testAgent.getIconUrl(), response.getIconUrl());
        assertEquals(testAgent.getDescription(), response.getDescription());
        assertEquals(testAgent.getCategory(), response.getCategory());
        assertEquals(testAgent.getTargetSystemUrl(), response.getTargetSystemUrl());
        assertEquals(testAgent.getVisibilityScope(), response.getVisibilityScope());
        assertEquals(testAgent.getCreator(), response.getCreator());
        assertEquals(testAgent.getCreatedAt(), response.getCreatedAt());
        
        // 验证不包含updatedAt字段（AgentResponse中没有这个字段）
        // 这通过类定义已经保证了
    }

    @Test
    void should_map_all_properties_correctly_when_of_given_complete_agent() {
        // Given - 包含所有字段的Agent
        List<String> tags = Arrays.asList("大语言模型", "图像模型");
        VisibilityScope visibilityScope = new VisibilityScope(VisibilityType.PERSON, Arrays.asList("user1", "user2"));
        Agent completeAgent = new Agent(
            "complete-id",
            AgentSource.HAND,
            "完整智能体",
            tags,
            "https://example.com/complete-icon.png",
            "这是一个完整的智能体描述",
            "效率工具",
            "https://example.com/complete-target",
            visibilityScope,
            "complete-creator",
            testTime,
            testTime.plusHours(1) // updatedAt不同于createdAt
        );

        // When
        AgentResponse response = AgentResponse.of(completeAgent);

        // Then
        assertEquals("complete-id", response.getId());
        assertEquals(AgentSource.HAND, response.getSource());
        assertEquals("完整智能体", response.getName());
        assertEquals(2, response.getTags().size());
        assertTrue(response.getTags().contains("大语言模型"));
        assertTrue(response.getTags().contains("图像模型"));
        assertEquals("https://example.com/complete-icon.png", response.getIconUrl());
        assertEquals("这是一个完整的智能体描述", response.getDescription());
        assertEquals("效率工具", response.getCategory());
        assertEquals("https://example.com/complete-target", response.getTargetSystemUrl());
        assertEquals(VisibilityType.PERSON, response.getVisibilityScope().getType());
        assertEquals(2, response.getVisibilityScope().getValues().size());
        assertTrue(response.getVisibilityScope().getValues().contains("user1"));
        assertTrue(response.getVisibilityScope().getValues().contains("user2"));
        assertEquals("complete-creator", response.getCreator());
        assertEquals(testTime, response.getCreatedAt());
    }

    @Test
    void should_handle_null_optional_fields_when_of_given_agent_with_nulls() {
        // Given - Agent with some null optional fields
        Agent agentWithNulls = new Agent(
            "null-test-id",
            AgentSource.FASTGPT,
            "空值测试智能体",
            null, // null tags
            null, // null iconUrl
            "描述",
            "智能助手",
            "https://example.com/target",
            new VisibilityScope(VisibilityType.ALL, Arrays.asList()),
            "creator",
            testTime,
            testTime
        );

        // When
        AgentResponse response = AgentResponse.of(agentWithNulls);

        // Then
        assertEquals("null-test-id", response.getId());
        assertEquals(AgentSource.FASTGPT, response.getSource());
        assertEquals("空值测试智能体", response.getName());
        assertNull(response.getTags()); // 保持null值
        assertNull(response.getIconUrl()); // 保持null值
        assertEquals("描述", response.getDescription());
        assertEquals("智能助手", response.getCategory());
        assertEquals("https://example.com/target", response.getTargetSystemUrl());
        assertEquals(VisibilityType.ALL, response.getVisibilityScope().getType());
        assertEquals("creator", response.getCreator());
        assertEquals(testTime, response.getCreatedAt());
    }

    @Test
    void should_handle_empty_tags_when_of_given_agent_with_empty_tags() {
        // Given
        testAgent.setTags(Arrays.asList());

        // When
        AgentResponse response = AgentResponse.of(testAgent);

        // Then
        assertNotNull(response.getTags());
        assertTrue(response.getTags().isEmpty());
    }

    @Test
    void should_preserve_visibility_scope_structure_when_of_given_agent() {
        // Given - 测试不同的可见范围类型
        VisibilityScope allScope = new VisibilityScope(VisibilityType.ALL, Arrays.asList());
        testAgent.setVisibilityScope(allScope);

        // When
        AgentResponse response = AgentResponse.of(testAgent);

        // Then
        assertNotNull(response.getVisibilityScope());
        assertEquals(VisibilityType.ALL, response.getVisibilityScope().getType());
        assertNotNull(response.getVisibilityScope().getValues());
        assertTrue(response.getVisibilityScope().getValues().isEmpty());
    }

    @Test
    void should_create_response_with_correct_api_structure_when_of_given_agent() {
        // When
        AgentResponse response = AgentResponse.of(testAgent);

        // Then - 验证响应对象结构符合API要求
        assertNotNull(response.getId());
        assertNotNull(response.getSource());
        assertNotNull(response.getName());
        assertNotNull(response.getDescription());
        assertNotNull(response.getCategory());
        assertNotNull(response.getTargetSystemUrl());
        assertNotNull(response.getVisibilityScope());
        assertNotNull(response.getCreator());
        assertNotNull(response.getCreatedAt());
        
        // 验证枚举类型正确映射
        assertTrue(response.getSource() instanceof AgentSource);
        assertTrue(response.getVisibilityScope().getType() instanceof VisibilityType);
        
        // 验证时间类型正确映射
        assertTrue(response.getCreatedAt() instanceof LocalDateTime);
    }

    @Test
    void should_handle_different_agent_sources_when_of_given_agents_with_different_sources() {
        // Test FASTGPT source
        testAgent.setSource(AgentSource.FASTGPT);
        AgentResponse fastgptResponse = AgentResponse.of(testAgent);
        assertEquals(AgentSource.FASTGPT, fastgptResponse.getSource());

        // Test HAND source
        testAgent.setSource(AgentSource.HAND);
        AgentResponse handResponse = AgentResponse.of(testAgent);
        assertEquals(AgentSource.HAND, handResponse.getSource());
    }

    @Test
    void should_handle_different_visibility_types_when_of_given_agents_with_different_scopes() {
        // Test ORGANIZATION type
        VisibilityScope orgScope = new VisibilityScope(VisibilityType.ORGANIZATION, Arrays.asList("org1"));
        testAgent.setVisibilityScope(orgScope);
        AgentResponse orgResponse = AgentResponse.of(testAgent);
        assertEquals(VisibilityType.ORGANIZATION, orgResponse.getVisibilityScope().getType());
        assertEquals(1, orgResponse.getVisibilityScope().getValues().size());
        assertEquals("org1", orgResponse.getVisibilityScope().getValues().get(0));

        // Test PERSON type
        VisibilityScope personScope = new VisibilityScope(VisibilityType.PERSON, Arrays.asList("user1", "user2"));
        testAgent.setVisibilityScope(personScope);
        AgentResponse personResponse = AgentResponse.of(testAgent);
        assertEquals(VisibilityType.PERSON, personResponse.getVisibilityScope().getType());
        assertEquals(2, personResponse.getVisibilityScope().getValues().size());

        // Test ALL type
        VisibilityScope allScope = new VisibilityScope(VisibilityType.ALL, Arrays.asList());
        testAgent.setVisibilityScope(allScope);
        AgentResponse allResponse = AgentResponse.of(testAgent);
        assertEquals(VisibilityType.ALL, allResponse.getVisibilityScope().getType());
        assertTrue(allResponse.getVisibilityScope().getValues().isEmpty());
    }
} 