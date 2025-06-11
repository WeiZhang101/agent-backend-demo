package org.tw.agent_backend_demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tw.agent_backend_demo.dto.CreateAgentRequest;
import org.tw.agent_backend_demo.exceptions.*;
import org.tw.agent_backend_demo.models.Agent;
import org.tw.agent_backend_demo.models.enums.AgentSource;
import org.tw.agent_backend_demo.models.enums.VisibilityType;
import org.tw.agent_backend_demo.repository.AgentRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class AgentServiceImpl implements AgentService {
    
    private static final List<String> ALLOWED_TAGS = Arrays.asList("大语言模型", "语音模型", "图像模型");
    private static final List<String> ALLOWED_CATEGORIES = Arrays.asList("智能助手", "效率工具");
    private static final Pattern URL_PATTERN = Pattern.compile("^https?://.*");
    
    @Autowired
    private AgentRepository agentRepository;

    @Override
    public Agent createAgent(CreateAgentRequest request, String creator) {
        // Validate source enum value
        validateSource(request.getSource());
        
        // Validate tags
        validateTags(request.getTags());
        
        // Validate icon URL format
        validateIconUrl(request.getIconUrl());
        
        // Validate category
        validateCategory(request.getCategory());
        
        // Validate target system URL format
        validateTargetSystemUrl(request.getTargetSystemUrl());
        
        // Validate visibility scope
        validateVisibilityScope(request.getVisibilityScope());
        
        // Check if agent name already exists
        checkAgentNameUniqueness(request.getName());
        
        // Create new Agent object
        Agent agent = Agent.fromRequest(request, creator);
        
        // Save the entity
        return agentRepository.save(agent);
    }
    
    private void validateSource(AgentSource source) {
        if (source == null) {
            throw new InvalidSourceException("Agent source cannot be null");
        }
        // AgentSource is an enum, so if it's not null, it's valid
    }
    
    private void validateTags(List<String> tags) {
        if (tags != null) {
            for (String tag : tags) {
                if (!ALLOWED_TAGS.contains(tag)) {
                    throw new InvalidTagException("Invalid tag: " + tag + ". Allowed tags are: " + ALLOWED_TAGS);
                }
            }
        }
    }
    
    private void validateIconUrl(String iconUrl) {
        if (iconUrl != null && !iconUrl.isEmpty()) {
            if (!URL_PATTERN.matcher(iconUrl).matches()) {
                throw new InvalidIconUrlException("Icon URL must be a valid HTTP/HTTPS URL");
            }
        }
    }
    
    private void validateCategory(String category) {
        if (category == null || !ALLOWED_CATEGORIES.contains(category)) {
            throw new InvalidCategoryException("Invalid category: " + category + ". Allowed categories are: " + ALLOWED_CATEGORIES);
        }
    }
    
    private void validateTargetSystemUrl(String targetSystemUrl) {
        if (targetSystemUrl == null || targetSystemUrl.isEmpty()) {
            throw new InvalidTargetSystemUrlException("Target system URL cannot be null or empty");
        }
        if (!URL_PATTERN.matcher(targetSystemUrl).matches()) {
            throw new InvalidTargetSystemUrlException("Target system URL must be a valid HTTP/HTTPS URL");
        }
    }
    
    private void validateVisibilityScope(org.tw.agent_backend_demo.models.VisibilityScope visibilityScope) {
        if (visibilityScope == null) {
            throw new InvalidVisibilityScopeException("Visibility scope cannot be null");
        }
        if (visibilityScope.getType() == null) {
            throw new InvalidVisibilityScopeException("Visibility type cannot be null");
        }
        
        // For ORGANIZATION and PERSON types, values should not be empty
        if ((visibilityScope.getType() == VisibilityType.ORGANIZATION || 
             visibilityScope.getType() == VisibilityType.PERSON) &&
            (visibilityScope.getValues() == null || visibilityScope.getValues().isEmpty())) {
            throw new InvalidVisibilityScopeException("Visibility values cannot be empty for " + visibilityScope.getType() + " type");
        }
    }
    
    private void checkAgentNameUniqueness(String name) {
        Optional<Agent> existingAgent = agentRepository.findByName(name);
        if (existingAgent.isPresent()) {
            throw new AgentAlreadyExistsException("Agent with name '" + name + "' already exists");
        }
    }
} 