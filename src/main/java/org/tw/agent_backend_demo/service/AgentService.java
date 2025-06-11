package org.tw.agent_backend_demo.service;

import org.tw.agent_backend_demo.dto.CreateAgentRequest;
import org.tw.agent_backend_demo.models.Agent;

public interface AgentService {
    Agent createAgent(CreateAgentRequest request, String creator);
} 