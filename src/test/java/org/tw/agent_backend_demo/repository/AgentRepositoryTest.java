package org.tw.agent_backend_demo.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tw.agent_backend_demo.models.Agent;
import org.tw.agent_backend_demo.models.AgentPO;
import org.tw.agent_backend_demo.models.VisibilityScope;
import org.tw.agent_backend_demo.models.enums.AgentSource;
import org.tw.agent_backend_demo.models.enums.VisibilityType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgentRepositoryTest {

    @Mock
    private AgentDAO agentDAO;

    @InjectMocks
    private AgentRepositoryImpl agentRepository;

    private Agent testAgent;
    private AgentPO testAgentPO;

    @BeforeEach
    void setUp() {
        List<String> tags = Arrays.asList("大语言模型");
        VisibilityScope visibilityScope = new VisibilityScope(VisibilityType.ALL, Arrays.asList());
        LocalDateTime now = LocalDateTime.now();

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
            now,
            now
        );

        testAgentPO = AgentPO.of(testAgent);
    }

    @Test
    void should_save_agent_successfully_when_save_agent_given_valid_agent() {
        // Given
        when(agentDAO.save(any(AgentPO.class))).thenReturn(testAgentPO);

        // When
        Agent result = agentRepository.save(testAgent);

        // Then
        assertNotNull(result);
        assertEquals(testAgent.getId(), result.getId());
        assertEquals(testAgent.getName(), result.getName());
        assertEquals(testAgent.getSource(), result.getSource());
        assertEquals(testAgent.getDescription(), result.getDescription());
        assertEquals(testAgent.getCategory(), result.getCategory());
        assertEquals(testAgent.getCreator(), result.getCreator());
        assertEquals(testAgent.getTags(), result.getTags());
        assertEquals(testAgent.getVisibilityScope().getType(), result.getVisibilityScope().getType());

        verify(agentDAO, times(1)).save(any(AgentPO.class));
    }

    @Test
    void should_find_agent_by_name_when_find_by_name_given_existing_name() {
        // Given
        String existingName = "测试智能体";
        when(agentDAO.findByName(existingName)).thenReturn(Optional.of(testAgentPO));

        // When
        Optional<Agent> result = agentRepository.findByName(existingName);

        // Then
        assertTrue(result.isPresent());
        Agent foundAgent = result.get();
        assertEquals(testAgent.getId(), foundAgent.getId());
        assertEquals(testAgent.getName(), foundAgent.getName());
        assertEquals(testAgent.getSource(), foundAgent.getSource());
        assertEquals(testAgent.getDescription(), foundAgent.getDescription());
        assertEquals(testAgent.getCategory(), foundAgent.getCategory());
        assertEquals(testAgent.getCreator(), foundAgent.getCreator());

        verify(agentDAO, times(1)).findByName(existingName);
    }

    @Test
    void should_return_empty_optional_when_find_by_name_given_non_existing_name() {
        // Given
        String nonExistingName = "不存在的智能体";
        when(agentDAO.findByName(nonExistingName)).thenReturn(Optional.empty());

        // When
        Optional<Agent> result = agentRepository.findByName(nonExistingName);

        // Then
        assertFalse(result.isPresent());
        verify(agentDAO, times(1)).findByName(nonExistingName);
    }

    @Test
    void should_convert_agent_to_po_correctly_when_save_agent() {
        // Given
        AgentPO capturedPO = null;
        when(agentDAO.save(any(AgentPO.class))).thenAnswer(invocation -> {
            AgentPO po = invocation.getArgument(0);
            // 验证转换是否正确
            assertEquals(testAgent.getId(), po.getId());
            assertEquals(testAgent.getName(), po.getName());
            assertEquals(testAgent.getSource(), po.getSource());
            assertEquals(testAgent.getDescription(), po.getDescription());
            assertEquals(testAgent.getCategory(), po.getCategory());
            assertEquals(testAgent.getCreator(), po.getCreator());
            assertEquals(testAgent.getVisibilityScope().getType(), po.getVisibilityType());
            return po;
        });

        // When
        agentRepository.save(testAgent);

        // Then
        verify(agentDAO, times(1)).save(any(AgentPO.class));
    }

    @Test
    void should_convert_po_to_agent_correctly_when_find_by_name() {
        // Given
        String existingName = "测试智能体";
        when(agentDAO.findByName(existingName)).thenReturn(Optional.of(testAgentPO));

        // When
        Optional<Agent> result = agentRepository.findByName(existingName);

        // Then
        assertTrue(result.isPresent());
        Agent agent = result.get();
        
        // 验证从PO转换为Agent是否正确
        assertEquals(testAgentPO.getId(), agent.getId());
        assertEquals(testAgentPO.getName(), agent.getName());
        assertEquals(testAgentPO.getSource(), agent.getSource());
        assertEquals(testAgentPO.getDescription(), agent.getDescription());
        assertEquals(testAgentPO.getCategory(), agent.getCategory());
        assertEquals(testAgentPO.getCreator(), agent.getCreator());
        assertEquals(testAgentPO.getVisibilityType(), agent.getVisibilityScope().getType());

        verify(agentDAO, times(1)).findByName(existingName);
    }
} 