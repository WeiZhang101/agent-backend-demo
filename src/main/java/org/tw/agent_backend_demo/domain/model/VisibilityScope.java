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
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
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
     * Scope type - either ORGANIZATION or PERSONNEL
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility_type")
    private ScopeType type;
    
    /**
     * List of specific organizations or personnel identifiers
     */
    @ElementCollection
    @CollectionTable(name = "agent_visibility_values", joinColumns = @JoinColumn(name = "agent_id"))
    @Column(name = "visibility_value")
    private List<String> values;
    
    /**
     * Validates the visibility scope configuration.
     * Focuses on validating values list since enum ensures type validity.
     * 
     * @throws org.tw.agent_backend_demo.exception.InvalidVisibilityScopeException if the scope values are invalid
     */
    public void validateScope() {
        if (type == null) {
            throw new org.tw.agent_backend_demo.exception.InvalidVisibilityScopeException("Visibility scope type cannot be null");
        }
        
        if (values == null || values.isEmpty()) {
            throw new org.tw.agent_backend_demo.exception.InvalidVisibilityScopeException("Visibility scope values cannot be empty");
        }
    }
}
