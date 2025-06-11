package org.tw.agent_backend_demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.tw.agent_backend_demo.models.Agent;
import org.tw.agent_backend_demo.models.AgentPO;

import java.util.Optional;

@Repository
public class AgentRepositoryImpl implements AgentRepository {
    
    @Autowired
    private AgentDAO agentDAO;

    @Override
    public Agent save(Agent entity) {
        AgentPO po = AgentPO.of(entity);
        AgentPO savedPO = agentDAO.save(po);
        return Agent.fromPO(savedPO);
    }

    @Override
    public Optional<Agent> findByName(String name) {
        Optional<AgentPO> po = agentDAO.findByName(name);
        return po.map(Agent::fromPO);
    }
} 