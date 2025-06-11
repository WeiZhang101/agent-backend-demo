package org.tw.agent_backend_demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tw.agent_backend_demo.models.AgentPO;

import java.util.Optional;

public interface AgentDAO extends JpaRepository<AgentPO, String> {
    Optional<AgentPO> findByName(String name);
} 