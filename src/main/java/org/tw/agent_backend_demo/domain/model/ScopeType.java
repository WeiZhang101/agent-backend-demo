package org.tw.agent_backend_demo.domain.model;

/**
 * Enumeration defining allowed visibility scope types for agents.
 * Provides type safety and clear definition of supported scope categories.
 */
public enum ScopeType {
    
    /**
     * Scope limited to specific organizations
     */
    ORGANIZATION,
    
    /**
     * Scope limited to specific personnel/users
     */
    PERSONNEL
}
