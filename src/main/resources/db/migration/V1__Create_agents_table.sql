-- Create agents table
CREATE TABLE agents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    source VARCHAR(10) NOT NULL CHECK (source IN ('fastgpt', 'hand')),
    agent_name VARCHAR(50) NOT NULL UNIQUE,
    icon_url TEXT,
    description VARCHAR(500) NOT NULL,
    category VARCHAR(100) NOT NULL,
    target_system_url TEXT NOT NULL,
    visibility_type VARCHAR(20) NOT NULL CHECK (visibility_type IN ('ORGANIZATION', 'PERSONNEL')),
    creator VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create agent_tags table for storing tags
CREATE TABLE agent_tags (
    agent_id UUID NOT NULL REFERENCES agents(id) ON DELETE CASCADE,
    tag VARCHAR(100) NOT NULL,
    PRIMARY KEY (agent_id, tag)
);

-- Create agent_visibility_values table for storing visibility scope values
CREATE TABLE agent_visibility_values (
    agent_id UUID NOT NULL REFERENCES agents(id) ON DELETE CASCADE,
    visibility_value VARCHAR(100) NOT NULL,
    PRIMARY KEY (agent_id, visibility_value)
);

-- Create indexes for better performance
CREATE INDEX idx_agents_agent_name ON agents(agent_name);
CREATE INDEX idx_agents_source ON agents(source);
CREATE INDEX idx_agents_category ON agents(category);
CREATE INDEX idx_agents_creator ON agents(creator);
CREATE INDEX idx_agents_created_at ON agents(created_at);
CREATE INDEX idx_agent_tags_tag ON agent_tags(tag);
CREATE INDEX idx_agent_visibility_values_value ON agent_visibility_values(visibility_value);
