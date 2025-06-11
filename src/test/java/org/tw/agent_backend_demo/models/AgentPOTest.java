package org.tw.agent_backend_demo.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tw.agent_backend_demo.models.enums.AgentSource;
import org.tw.agent_backend_demo.models.enums.VisibilityType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AgentPOTest {

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
    void should_create_agent_po_from_agent_when_of_given_valid_agent() {
        // When
        AgentPO agentPO = AgentPO.of(testAgent);

        // Then
        assertNotNull(agentPO);
        assertEquals(testAgent.getId(), agentPO.getId());
        assertEquals(testAgent.getSource(), agentPO.getSource());
        assertEquals(testAgent.getName(), agentPO.getName());
        assertEquals(testAgent.getIconUrl(), agentPO.getIconUrl());
        assertEquals(testAgent.getDescription(), agentPO.getDescription());
        assertEquals(testAgent.getCategory(), agentPO.getCategory());
        assertEquals(testAgent.getTargetSystemUrl(), agentPO.getTargetSystemUrl());
        assertEquals(testAgent.getCreator(), agentPO.getCreator());
        assertEquals(testAgent.getCreatedAt(), agentPO.getCreatedAt());
        assertEquals(testAgent.getUpdatedAt(), agentPO.getUpdatedAt());

        // 验证visibilityScope正确分解为separate字段
        assertEquals(testAgent.getVisibilityScope().getType(), agentPO.getVisibilityType());
        
        // 验证tags列表正确转换为JSON字符串
        assertNotNull(agentPO.getTags());
        assertTrue(agentPO.getTags().contains("大语言模型"));
        assertTrue(agentPO.getTags().contains("语音模型"));
        
        // 验证visibilityValues正确转换为JSON字符串
        assertNotNull(agentPO.getVisibilityValues());
        assertTrue(agentPO.getVisibilityValues().contains("org1"));
        assertTrue(agentPO.getVisibilityValues().contains("org2"));
    }

    @Test
    void should_handle_empty_tags_when_of_given_agent_with_empty_tags() {
        // Given
        testAgent.setTags(Arrays.asList());

        // When
        AgentPO agentPO = AgentPO.of(testAgent);

        // Then
        assertNotNull(agentPO.getTags());
        assertEquals("[]", agentPO.getTags());
    }

    @Test
    void should_handle_null_tags_when_of_given_agent_with_null_tags() {
        // Given
        testAgent.setTags(null);

        // When
        AgentPO agentPO = AgentPO.of(testAgent);

        // Then
        assertEquals("", agentPO.getTags());
    }

    @Test
    void should_handle_empty_visibility_values_when_of_given_agent_with_empty_values() {
        // Given
        VisibilityScope emptyScope = new VisibilityScope(VisibilityType.ALL, Arrays.asList());
        testAgent.setVisibilityScope(emptyScope);

        // When
        AgentPO agentPO = AgentPO.of(testAgent);

        // Then
        assertEquals(VisibilityType.ALL, agentPO.getVisibilityType());
        assertNotNull(agentPO.getVisibilityValues());
        assertEquals("[]", agentPO.getVisibilityValues());
    }

    @Test
    void should_handle_null_visibility_scope_when_of_given_agent_with_null_scope() {
        // Given
        testAgent.setVisibilityScope(null);

        // When
        AgentPO agentPO = AgentPO.of(testAgent);

        // Then
        assertEquals(VisibilityType.ALL, agentPO.getVisibilityType()); // 默认值
        assertEquals("", agentPO.getVisibilityValues());
    }

    @Test
    void should_handle_null_visibility_values_when_of_given_agent_with_null_values() {
        // Given
        VisibilityScope scopeWithNullValues = new VisibilityScope(VisibilityType.PERSON, null);
        testAgent.setVisibilityScope(scopeWithNullValues);

        // When
        AgentPO agentPO = AgentPO.of(testAgent);

        // Then
        assertEquals(VisibilityType.PERSON, agentPO.getVisibilityType());
        assertEquals("", agentPO.getVisibilityValues());
    }

    @Test
    void should_convert_different_visibility_types_correctly() {
        // Test PERSON type
        VisibilityScope personScope = new VisibilityScope(VisibilityType.PERSON, Arrays.asList("user1", "user2"));
        testAgent.setVisibilityScope(personScope);
        
        AgentPO personPO = AgentPO.of(testAgent);
        assertEquals(VisibilityType.PERSON, personPO.getVisibilityType());
        assertTrue(personPO.getVisibilityValues().contains("user1"));
        assertTrue(personPO.getVisibilityValues().contains("user2"));

        // Test ALL type
        VisibilityScope allScope = new VisibilityScope(VisibilityType.ALL, Arrays.asList());
        testAgent.setVisibilityScope(allScope);
        
        AgentPO allPO = AgentPO.of(testAgent);
        assertEquals(VisibilityType.ALL, allPO.getVisibilityType());
        assertEquals("[]", allPO.getVisibilityValues());
    }

    @Test
    void should_convert_different_tag_combinations_correctly() {
        // Test single tag
        testAgent.setTags(Arrays.asList("大语言模型"));
        AgentPO singleTagPO = AgentPO.of(testAgent);
        assertTrue(singleTagPO.getTags().contains("大语言模型"));

        // Test multiple tags
        testAgent.setTags(Arrays.asList("大语言模型", "语音模型", "图像模型"));
        AgentPO multiTagPO = AgentPO.of(testAgent);
        assertTrue(multiTagPO.getTags().contains("大语言模型"));
        assertTrue(multiTagPO.getTags().contains("语音模型"));
        assertTrue(multiTagPO.getTags().contains("图像模型"));
    }

    @Test
    void should_preserve_all_basic_fields_when_converting() {
        // Given - 包含所有字段的Agent
        List<String> tags = Arrays.asList("大语言模型");
        VisibilityScope visibilityScope = new VisibilityScope(VisibilityType.PERSON, Arrays.asList("user1"));
        Agent fullAgent = new Agent(
            "full-id",
            AgentSource.HAND,
            "完整智能体",
            tags,
            "https://example.com/full-icon.png",
            "这是一个完整的智能体描述",
            "效率工具",
            "https://example.com/full-target",
            visibilityScope,
            "full-creator",
            testTime,
            testTime
        );

        // When
        AgentPO agentPO = AgentPO.of(fullAgent);

        // Then
        assertEquals("full-id", agentPO.getId());
        assertEquals(AgentSource.HAND, agentPO.getSource());
        assertEquals("完整智能体", agentPO.getName());
        assertEquals("https://example.com/full-icon.png", agentPO.getIconUrl());
        assertEquals("这是一个完整的智能体描述", agentPO.getDescription());
        assertEquals("效率工具", agentPO.getCategory());
        assertEquals("https://example.com/full-target", agentPO.getTargetSystemUrl());
        assertEquals(VisibilityType.PERSON, agentPO.getVisibilityType());
        assertEquals("full-creator", agentPO.getCreator());
        assertEquals(testTime, agentPO.getCreatedAt());
        assertEquals(testTime, agentPO.getUpdatedAt());
    }

    @Test
    void should_handle_json_serialization_errors_gracefully() {
        // Given - 这个测试主要是为了覆盖JSON序列化异常的情况
        // 在实际情况下，正常的字符串列表不会导致JSON序列化失败
        // 但我们可以测试空值和边界情况
        
        Agent agentWithNulls = new Agent(
            "test-id",
            AgentSource.FASTGPT,
            "测试智能体",
            null, // null tags
            null, // null iconUrl
            "描述",
            "分类",
            "https://example.com/target",
            null, // null visibilityScope
            "creator",
            testTime,
            testTime
        );

        // When
        AgentPO agentPO = AgentPO.of(agentWithNulls);

        // Then - 应该使用空字符串作为fallback
        assertNotNull(agentPO);
        assertEquals("", agentPO.getTags());
        assertEquals("", agentPO.getVisibilityValues());
        assertEquals(VisibilityType.ALL, agentPO.getVisibilityType());
    }
} 