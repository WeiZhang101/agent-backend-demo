package org.tw.agent_backend_demo.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.hibernate.exception.ConstraintViolationException;
import org.tw.agent_backend_demo.models.AgentPO;
import org.tw.agent_backend_demo.models.enums.AgentSource;
import org.tw.agent_backend_demo.models.enums.VisibilityType;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AgentDAOTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AgentDAO agentDAO;

    private AgentPO testAgentPO;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        testAgentPO = new AgentPO(
            "test-id-123",
            AgentSource.FASTGPT,
            "测试智能体",
            "[\"大语言模型\"]",
            "https://example.com/icon.png",
            "这是一个测试智能体",
            "智能助手",
            "https://example.com/target",
            VisibilityType.ALL,
            "[]",
            "test-creator",
            now,
            now
        );
    }

    @Test
    void should_save_agent_po_successfully_when_save_given_valid_agent_po() {
        // When
        AgentPO savedAgent = agentDAO.save(testAgentPO);

        // Then
        assertNotNull(savedAgent);
        assertEquals(testAgentPO.getId(), savedAgent.getId());
        assertEquals(testAgentPO.getName(), savedAgent.getName());
        assertEquals(testAgentPO.getSource(), savedAgent.getSource());
        assertEquals(testAgentPO.getDescription(), savedAgent.getDescription());
        assertEquals(testAgentPO.getCategory(), savedAgent.getCategory());
        assertEquals(testAgentPO.getCreator(), savedAgent.getCreator());
        assertEquals(testAgentPO.getTags(), savedAgent.getTags());
        assertEquals(testAgentPO.getVisibilityType(), savedAgent.getVisibilityType());

        // 验证对象被正确持久化到数据库
        AgentPO foundAgent = entityManager.find(AgentPO.class, testAgentPO.getId());
        assertNotNull(foundAgent);
        assertEquals(testAgentPO.getName(), foundAgent.getName());
    }

    @Test
    void should_find_agent_po_by_name_when_find_by_name_given_existing_name() {
        // Given - 先保存一个智能体
        entityManager.persistAndFlush(testAgentPO);

        // When
        Optional<AgentPO> result = agentDAO.findByName("测试智能体");

        // Then
        assertTrue(result.isPresent());
        AgentPO foundAgent = result.get();
        assertEquals(testAgentPO.getId(), foundAgent.getId());
        assertEquals(testAgentPO.getName(), foundAgent.getName());
        assertEquals(testAgentPO.getSource(), foundAgent.getSource());
        assertEquals(testAgentPO.getDescription(), foundAgent.getDescription());
        assertEquals(testAgentPO.getCategory(), foundAgent.getCategory());
        assertEquals(testAgentPO.getCreator(), foundAgent.getCreator());
    }

    @Test
    void should_return_empty_optional_when_find_by_name_given_non_existing_name() {
        // When
        Optional<AgentPO> result = agentDAO.findByName("不存在的智能体");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void should_enforce_unique_constraint_when_save_given_duplicate_name() {
        // Given - 先保存一个智能体
        entityManager.persistAndFlush(testAgentPO);

        // 创建另一个具有相同名称的智能体
        AgentPO duplicateAgent = new AgentPO(
            "test-id-456",
            AgentSource.HAND,
            "测试智能体", // 相同的名称
            "[\"语音模型\"]",
            "https://example.com/icon2.png",
            "这是另一个测试智能体",
            "效率工具",
            "https://example.com/target2",
            VisibilityType.PERSON,
            "[\"user1\"]",
            "another-creator",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        // When & Then
        // H2数据库会抛出ConstraintViolationException，Spring会将其包装为DataIntegrityViolationException
        assertThrows(Exception.class, () -> {
            agentDAO.save(duplicateAgent);
            entityManager.flush(); // 强制执行数据库操作以触发约束检查
        });
    }

    @Test
    void should_save_multiple_agents_with_different_names_successfully() {
        // Given
        AgentPO anotherAgent = new AgentPO(
            "test-id-456",
            AgentSource.HAND,
            "另一个智能体", // 不同的名称
            "[\"语音模型\"]",
            "https://example.com/icon2.png",
            "这是另一个测试智能体",
            "效率工具",
            "https://example.com/target2",
            VisibilityType.PERSON,
            "[\"user1\"]",
            "another-creator",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        // When
        AgentPO savedAgent1 = agentDAO.save(testAgentPO);
        AgentPO savedAgent2 = agentDAO.save(anotherAgent);

        // Then
        assertNotNull(savedAgent1);
        assertNotNull(savedAgent2);
        assertEquals("测试智能体", savedAgent1.getName());
        assertEquals("另一个智能体", savedAgent2.getName());

        // 验证两个智能体都被正确保存
        Optional<AgentPO> found1 = agentDAO.findByName("测试智能体");
        Optional<AgentPO> found2 = agentDAO.findByName("另一个智能体");
        
        assertTrue(found1.isPresent());
        assertTrue(found2.isPresent());
    }

    @Test
    void should_handle_null_optional_fields_correctly() {
        // Given - 创建一个只有必需字段的智能体
        AgentPO minimalAgent = new AgentPO(
            "minimal-id",
            AgentSource.FASTGPT,
            "最小智能体",
            null, // tags可以为null
            null, // iconUrl可以为null
            "最小描述",
            "智能助手",
            "https://example.com/target",
            VisibilityType.ALL,
            "[]",
            "creator",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        // When
        AgentPO savedAgent = agentDAO.save(minimalAgent);

        // Then
        assertNotNull(savedAgent);
        assertEquals("最小智能体", savedAgent.getName());
        assertNull(savedAgent.getTags());
        assertNull(savedAgent.getIconUrl());

        // 验证可以通过名称找到
        Optional<AgentPO> found = agentDAO.findByName("最小智能体");
        assertTrue(found.isPresent());
        assertEquals("最小智能体", found.get().getName());
    }
} 