package org.tw.agent_backend_demo.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.tw.agent_backend_demo.domain.model.AgentPO;
import org.tw.agent_backend_demo.domain.model.ScopeType;
import org.tw.agent_backend_demo.domain.model.VisibilityScope;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for AgentRepository.
 * Uses @DataJpaTest to test data access layer with an in-memory database.
 */
@DataJpaTest
@ActiveProfiles("test")
class AgentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AgentRepository agentRepository;

    @Test
    void should_return_true_when_exists_by_agent_name_given_existing_name() {
        // Given
        AgentPO agentPO = createTestAgentPO("ExistingAgent");
        entityManager.persistAndFlush(agentPO);

        // When
        boolean exists = agentRepository.existsByAgentName("ExistingAgent");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void should_return_false_when_exists_by_agent_name_given_non_existing_name() {
        // Given
        // No agents in database

        // When
        boolean exists = agentRepository.existsByAgentName("NonExistingAgent");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void should_return_false_when_exists_by_agent_name_given_null_input() {
        // Given
        AgentPO agentPO = createTestAgentPO("ExistingAgent");
        entityManager.persistAndFlush(agentPO);

        // When
        boolean exists = agentRepository.existsByAgentName(null);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void should_save_and_return_agent_po_when_save_given_valid_agent_po() {
        // Given
        AgentPO agentPO = createTestAgentPO("NewAgent");

        // When
        AgentPO savedAgentPO = agentRepository.save(agentPO);

        // Then
        assertThat(savedAgentPO).isNotNull();
        assertThat(savedAgentPO.getId()).isNotNull();
        assertThat(savedAgentPO.getAgentName()).isEqualTo("NewAgent");
        assertThat(savedAgentPO.getSource()).isEqualTo("fastgpt");
        assertThat(savedAgentPO.getDescription()).isEqualTo("Test Description");
        assertThat(savedAgentPO.getCategory()).isEqualTo("AI");
        assertThat(savedAgentPO.getTargetSystemUrl()).isEqualTo("https://test.com");
        assertThat(savedAgentPO.getCreator()).isEqualTo("testuser");
        assertThat(savedAgentPO.getCreatedAt()).isNotNull();
        assertThat(savedAgentPO.getVisibilityScope().getType()).isEqualTo(ScopeType.ORGANIZATION);
        assertThat(savedAgentPO.getVisibilityScope().getValues()).containsExactly("org1", "org2");

        // Verify persistence by finding the saved entity
        Optional<AgentPO> foundAgent = agentRepository.findById(savedAgentPO.getId());
        assertThat(foundAgent).isPresent();
        assertThat(foundAgent.get().getAgentName()).isEqualTo("NewAgent");
    }

    @Test
    void should_return_agent_po_when_find_by_id_given_existing_id() {
        // Given
        AgentPO agentPO = createTestAgentPO("FindableAgent");
        AgentPO savedAgent = entityManager.persistAndFlush(agentPO);
        UUID existingId = savedAgent.getId();

        // When
        Optional<AgentPO> foundAgent = agentRepository.findById(existingId);

        // Then
        assertThat(foundAgent).isPresent();
        assertThat(foundAgent.get().getId()).isEqualTo(existingId);
        assertThat(foundAgent.get().getAgentName()).isEqualTo("FindableAgent");
        assertThat(foundAgent.get().getSource()).isEqualTo("fastgpt");
        assertThat(foundAgent.get().getDescription()).isEqualTo("Test Description");
        assertThat(foundAgent.get().getCategory()).isEqualTo("AI");
        assertThat(foundAgent.get().getTargetSystemUrl()).isEqualTo("https://test.com");
        assertThat(foundAgent.get().getCreator()).isEqualTo("testuser");
        assertThat(foundAgent.get().getCreatedAt()).isNotNull();
    }

    @Test
    void should_return_empty_optional_when_find_by_id_given_non_existing_id() {
        // Given
        UUID nonExistingId = UUID.randomUUID();

        // When
        Optional<AgentPO> foundAgent = agentRepository.findById(nonExistingId);

        // Then
        assertThat(foundAgent).isEmpty();
    }

    @Test
    void should_enforce_unique_constraint_when_save_given_duplicate_agent_name() {
        // Given
        AgentPO firstAgent = createTestAgentPO("DuplicateName");
        agentRepository.save(firstAgent);

        AgentPO secondAgent = createTestAgentPO("DuplicateName");

        // When & Then
        assertThatThrownBy(() -> {
            agentRepository.save(secondAgent);
            entityManager.flush(); // Force the database operation
        }).isInstanceOf(Exception.class); // Accept any exception from constraint violation
    }

    private AgentPO createTestAgentPO(String agentName) {
        return AgentPO.builder()
                .source("fastgpt")
                .agentName(agentName)
                .tags(List.of("AI", "Test"))
                .iconUrl("https://example.com/icon.png")
                .description("Test Description")
                .category("AI")
                .targetSystemUrl("https://test.com")
                .visibilityScope(VisibilityScope.builder()
                        .type(ScopeType.ORGANIZATION)
                        .values(List.of("org1", "org2"))
                        .build())
                .creator("testuser")
                .createdAt(LocalDateTime.now())
                .build();
    }
}
