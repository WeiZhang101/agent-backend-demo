-- Update visibility_type constraint to use uppercase enum values
-- This migration updates the constraint to match the ScopeType enum values

-- Drop the old constraint first
ALTER TABLE agents DROP CONSTRAINT IF EXISTS agents_visibility_type_check;

-- Update existing data to use uppercase values
UPDATE agents SET visibility_type = 'ORGANIZATION' WHERE visibility_type = 'organization';
UPDATE agents SET visibility_type = 'PERSONNEL' WHERE visibility_type = 'personnel';

-- Add new constraint with uppercase enum values
ALTER TABLE agents ADD CONSTRAINT agents_visibility_type_check 
    CHECK (visibility_type IN ('ORGANIZATION', 'PERSONNEL'));
