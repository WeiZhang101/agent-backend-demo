-- Create agents table
CREATE TABLE agents (
    id VARCHAR(255) PRIMARY KEY,
    source VARCHAR(50) NOT NULL,
    name VARCHAR(50) NOT NULL UNIQUE,
    tags TEXT,
    icon_url VARCHAR(500),
    description VARCHAR(500) NOT NULL,
    category VARCHAR(100) NOT NULL,
    target_system_url VARCHAR(500) NOT NULL,
    visibility_type VARCHAR(50) NOT NULL,
    visibility_values TEXT,
    creator VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create index on name for faster lookups
CREATE INDEX idx_agents_name ON agents(name);

-- Create index on creator for filtering by creator
CREATE INDEX idx_agents_creator ON agents(creator);

-- Create index on source for filtering by source
CREATE INDEX idx_agents_source ON agents(source); 