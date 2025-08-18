package org.tw.agent_backend_demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tw.agent_backend_demo.domain.model.AgentPO;

import java.util.Optional;
import java.util.UUID;
import org.springframework.lang.NonNull;

/**
 * Repository interface for Agent data access operations.
 * Extends JpaRepository to provide standard CRUD operations and custom query methods.
 */
@Repository
public interface AgentRepository extends JpaRepository<AgentPO, UUID> {
    
    /**
     * Checks if an agent with the given name already exists.
     * 
     * @param agentName the agent name to check for existence
     * @return true if an agent with the given name exists, false otherwise
     */
    boolean existsByAgentName(String agentName);
    
    /**
     * Finds an agent by its unique identifier.
     * 
     * @param id the unique identifier of the agent
     * @return Optional containing the agent if found, empty otherwise
     */
    @NonNull
    Optional<AgentPO> findById(@NonNull UUID id);
    
    /**
     * Saves an agent entity to the database.
     * 
     * @param agentPO the agent entity to save
     * @return the saved agent entity with generated/updated fields
     */
    @NonNull
    <S extends AgentPO> S save(@NonNull S agentPO);
}
