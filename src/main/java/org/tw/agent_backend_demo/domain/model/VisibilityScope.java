package org.tw.agent_backend_demo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import jakarta.persistence.Embeddable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import java.util.List;

/**
 * Value object representing visibility scope configuration for an agent.
 * Encapsulates the type of scope (organization or personnel) and the specific values.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class VisibilityScope {
    
    /**
     * Scope type - either "organization" or "personnel"
     */
    @Column(name = "visibility_type")
    private String type;
    
    /**
     * List of specific organizations or personnel identifiers
     */
    @ElementCollection
    @CollectionTable(name = "agent_visibility_values", joinColumns = @JoinColumn(name = "agent_id"))
    @Column(name = "visibility_value")
    private List<String> values;
    
    /**
     * Validates the visibility scope configuration.
     * 
     * @throws org.tw.agent_backend_demo.exception.InvalidVisibilityScopeException if the scope type is invalid
     */
    public void validateScope() {
        if (type == null || (!type.equals("organization") && !type.equals("personnel"))) {
            throw new org.tw.agent_backend_demo.exception.InvalidVisibilityScopeException("Invalid visibility scope type: " + type);
        }
        
        if (values == null || values.isEmpty()) {
            throw new org.tw.agent_backend_demo.exception.InvalidVisibilityScopeException("Visibility scope values cannot be empty");
        }
    }
}
