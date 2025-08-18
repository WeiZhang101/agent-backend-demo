package org.tw.agent_backend_demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tw.agent_backend_demo.dto.CreateAgentRequest;
import org.tw.agent_backend_demo.dto.CreateAgentResponse;
import org.tw.agent_backend_demo.service.AgentService;

import jakarta.validation.Valid;

/**
 * REST controller for agent management operations.
 * Handles HTTP requests for agent creation and provides standardized responses.
 */
@Slf4j
@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
public class AgentController {
    
    private final AgentService agentService;
    
    /**
     * Creates a new agent based on the provided request.
     * 
     * @param request the validated agent creation request
     * @return ResponseEntity with HTTP 201 status and the created agent information
     */
    @PostMapping
    public ResponseEntity<CreateAgentResponse> createAgent(@RequestBody @Valid CreateAgentRequest request) {
        log.info("Received request to create agent: {}", request.getAgentName());
        
        CreateAgentResponse response = agentService.createAgent(request);
        
        log.info("Successfully created agent with ID: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
