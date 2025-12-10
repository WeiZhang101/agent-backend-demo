## Background

The AIFSD Agent platform already supports the creation of "Agents". As business needs evolve and configurations change, users need the ability to modify existing agent information (such as descriptions, categories, visibility scope, etc.). The backend needs to provide an API for updating agent information while ensuring data validity and consistency.

## Business Value

1. **Flexible Configuration**: Product teams and business lines can adjust agent configurations at any time based on actual usage, avoiding the need to delete and recreate.
2. **Information Timeliness**: Support real-time updates to agent descriptions, categories, and other information to ensure accuracy and timeliness of the agent directory.
3. **Dynamic Permission Management**: Support adjustments to visibility scope to adapt to organizational structure changes or personnel changes.
4. **Audit Trail**: Record modification history for subsequent auditing and traceability (foundation for future enhancement).

## Scope In

- Design and implement PUT /api/agents/{id} (or equivalent path) endpoint for updating existing agents.
- Support updating the following fields:
  - Agent Name (string ≤50 characters, required, backend needs to validate uniqueness excluding current agent)
  - Tags (optional, dropdown; default support for "Large Language Model, Voice Model, Image Model")
  - Icon (optional, URL)
  - Description (string ≤500 characters, required)
  - Category (required, dropdown linked to backend-configured primary categories)
  - Target System URL (required, format validation: must start with http:// or https://)
  - Visibility Scope (required, support selection by organization or personnel)
- Fields that **cannot be modified**:
  - Source (cannot be changed after creation)
  - ID (system-generated identifier)
  - Creator (historical record)
  - Created Time (historical record)
- Return format: Return HTTP 200 with updated complete agent information on success; return corresponding error codes and error messages on failure.
- Backend needs to validate the following scenarios and return clear errors:
  1. Agent with specified ID does not exist;
  2. Required fields not provided;
  3. Field length exceeds limits;
  4. Enum values not within allowed range;
  5. Invalid URL format;
  6. Agent name conflicts with other existing agents;
  7. Invalid visibility scope parameters or non-existent corresponding organization/personnel;
  8. Attempting to modify read-only fields (Source, ID, Creator, Created Time).

## Scope Out

- Does not involve other operations such as creating, deleting, or querying agent lists, limited to "update" functionality only.
- Does not involve frontend implementation and styling, only focuses on backend API and business logic.
- Does not include dynamic management of "tags" (backend configuration already exists), only supports enum values or dropdown options passed from frontend.
- Does not include modification history/audit log functionality (can be added in future versions).
- Permission authentication logic is assumed to be handled in gateway or upper-layer middleware, backend only needs to verify that current user has permission to modify the agent.

## Acceptance Criteria (ACs)

1. Validate agent existence
   **Given** request specifies an agent ID that does not exist in database
   **When** backend attempts to query the agent
   **Then** return HTTP 404, error message "Agent not found with ID: {id}".

2. Validate "Agent Name" field - required, length ≤ 50, and unique (excluding current agent)
   **Given** request does not contain Agent Name
   **When** backend receives update request
   **Then** return HTTP 400, error message "Agent Name is required".

   **Given** request contains Agent Name with length > 50
   **When** backend validation finds length exceeds limit
   **Then** return HTTP 400, error message "Agent Name length cannot exceed 50 characters".

   **Given** request contains Agent Name that already exists in database (different from current agent)
   **When** backend validation finds duplication
   **Then** return HTTP 409, error message "Agent Name already exists, please use a different name".

   **Given** request contains Agent Name that is the same as current agent's name
   **When** backend validates name uniqueness
   **Then** allow the update to proceed (same name is acceptable for the same agent).

3. Validate "Tags" field - optional but if provided must be in available list
   **Given** request contains Tags, but value is not in allowed range (such as "Large Language Model, Voice Model, Image Model" or newly added in backend)
   **When** backend validates tags and finds mismatch
   **Then** return HTTP 400, error message "Tags value is invalid, please select from dropdown list".

4. Validate "Icon" field - optional but if provided must be URL format
   **Given** request contains Icon but URL format is invalid
   **When** backend validates icon URL format
   **Then** return HTTP 400, error message "Icon format is invalid, must start with http:// or https://".

5. Validate "Description" field - required and length ≤ 500
   **Given** request does not contain Description
   **When** backend receives update request
   **Then** return HTTP 400, error message "Description is required and cannot exceed 500 characters".

   **Given** request contains Description with length > 500
   **When** backend validation finds length exceeds limit
   **Then** return HTTP 400, error message "Description length cannot exceed 500 characters".

6. Validate "Category" field - required and must be in backend-configured primary category list
   **Given** request does not contain Category
   **When** backend receives update request
   **Then** return HTTP 400, error message "Category is required, please select a valid category".

   **Given** request contains Category, but the category is not in backend-configured list
   **When** backend validates category and finds invalid
   **Then** return HTTP 400, error message "Category is invalid, please select from backend-configured categories".

7. Validate "Target System URL" field - required and format valid
   **Given** request does not contain Target System URL
   **When** backend receives update request
   **Then** return HTTP 400, error message "Target System URL is required".

   **Given** request contains Target System URL, but does not start with http:// or https://
   **When** backend validates URL format
   **Then** return HTTP 400, error message "Target System URL format is invalid, must start with http:// or https://".

8. Validate "Visibility Scope" field - required and organization/personnel exists
   **Given** request does not contain Visibility Scope or passed value is empty
   **When** backend receives update request
   **Then** return HTTP 400, error message "Visibility Scope is required, please select organization or personnel".

   **Given** request contains Visibility Scope, but selected organization/personnel does not exist in system or has no permission
   **When** backend validation finds invalid or non-existent
   **Then** return HTTP 400, error message "Visibility Scope contains invalid organization/personnel, please check".

9. Prevent modification of read-only fields
   **Given** request attempts to modify Source, ID, Creator, or Created Time
   **When** backend receives update request
   **Then** return HTTP 400, error message "Cannot modify read-only fields: Source, ID, Creator, Created Time".

   **Note**: Implementation can either reject requests containing these fields, or silently ignore them (recommended approach: ignore to improve API compatibility).

10. Successful update return result
    **Given** all fields in request pass validation and agent exists
    **When** backend persists updated agent information
    **Then** return HTTP 200, response body contains complete information of updated agent (including update time), and return JSON structure example as follows:

    ```json
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "source": "fastgpt",
      "agentName": "Updated Agent Name",
      "tags": ["Large Language Model", "Voice Model"],
      "iconUrl": "https://cdn.xuehua.ai/agents/icons/updated-icon.png",
      "description": "This is an updated agent description",
      "category": "Productivity Tools",
      "targetSystemUrl": "https://api.xuehua.ai/agent/456",
      "visibilityScope": {
        "type": "personnel",
        "value": ["wwdzhang", "johndoe"]
      },
      "creator": "wwdzhang",
      "createdAt": "2025-06-05T08:30:00Z",
      "updatedAt": "2025-12-10T10:15:30Z"
    }
    ```

    Response includes original Creator and Created Time (unchanged), and new Updated Time field.

11. Exception scenario general return
    **Given** backend encounters system-level exceptions such as database or file storage during request processing
    **When** exception is caught
    **Then** return HTTP 500, error message "Internal server error, please try again later".

## Additional Considerations

1. **Partial Update Support**: Consider supporting PATCH method for partial updates in the future, allowing clients to only send fields that need to be changed.
2. **Optimistic Locking**: Consider adding version control (e.g., version field) in the future to prevent concurrent update conflicts.
3. **Notification Mechanism**: Consider adding notification functionality after updates in the future (e.g., notify users affected by visibility scope changes).
4. **Audit Log**: Consider recording detailed modification history for each update (who, when, what was changed).
