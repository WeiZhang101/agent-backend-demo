package org.tw.agent_backend_demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tw.agent_backend_demo.dto.AgentResponse;
import org.tw.agent_backend_demo.dto.CreateAgentRequest;
import org.tw.agent_backend_demo.models.Agent;
import org.tw.agent_backend_demo.service.AgentService;

@RestController
@RequestMapping("/api/v1/agents")
public class AgentController {
    
    @Autowired
    private AgentService agentService;
    
    @PostMapping
    public ResponseEntity<AgentResponse> createAgent(@RequestBody CreateAgentRequest request) {
        // Get current user from security context (simplified for now)
        String currentUser = "system"; // TODO: Get from security context
        
        // Create agent
        Agent agent = agentService.createAgent(request, currentUser);
        
        // Map to response DTO
        AgentResponse response = AgentResponse.of(agent);
        
        // Return 201 Created with the created agent data
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
} 