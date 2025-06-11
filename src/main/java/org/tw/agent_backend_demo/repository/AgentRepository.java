package org.tw.agent_backend_demo.repository;

import org.tw.agent_backend_demo.models.Agent;

import java.util.Optional;

public interface AgentRepository {
    Agent save(Agent entity);
    Optional<Agent> findByName(String name);
} 