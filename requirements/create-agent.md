## Background
The AIFSD Agent platform needs to support the creation of "Agents" for managing and invoking intelligent services from different sources (such as large language models, voice models, image models, etc.). The backend needs to provide an API that receives field information from the frontend, validates and stores the data, and returns the creation result.

## Business Value
1. **Rapid Deployment**: Product teams and business lines can quickly create and manage various types of agents through a unified interface, improving development efficiency and quality.
2. **Access Control**: Through the "visibility scope" field, ensure that agent usage scope is controllable, meeting organizational and personal-level access requirements.
3. **Unified Management**: Centrally store agent metadata for subsequent statistics, auditing, and maintenance; support integration with frontend display and calling-end "agent directory" functionality.

## Scope In
* Design and implement POST /api/agents (or equivalent path) endpoint for creating new agents.
* Receive and validate the following fields:
  * Source (enum: fastgpt or hand, required)
  * Agent Name (string ≤50 characters, required, backend needs to validate uniqueness)
  * Tags (optional, dropdown; default support for "Large Language Model, Voice Model, Image Model")
  * Icon (optional, URL)
  * Description (string ≤500 characters, required)
  * Category (required, dropdown linked to backend-configured primary categories, such as "Intelligent Assistant, Productivity Tools")
  * Target System URL (required, format validation: must start with http:// or https://)
  * Visibility Scope (required, support selection by organization or personnel; default "visible to all")
* Return format: Return HTTP 201 with detailed information of the newly created object on success; return corresponding error codes and error messages on failure.
* Backend needs to validate the following scenarios and return clear errors:
  1. Required fields not provided;
  2. Field length exceeds limits;
  3. Enum values not within allowed range;
  4. Invalid URL format;
  5. Agent name already exists;
  6. Invalid visibility scope parameters or non-existent corresponding organization/personnel.

## Scope Out
* Does not involve other operations such as updating, deleting, or querying agent lists, limited to "creation" functionality only.
* Does not involve frontend implementation and styling, only focuses on backend API and business logic.
* Does not include dynamic management of "tags" (backend configuration already exists), only supports enum values or dropdown options passed from frontend.
* Permission authentication logic is assumed to be handled in gateway or upper-layer middleware, backend only needs to use "current user" information in context for visibility scope mapping.

## Acceptance Criteria (ACs)
1. Validate "Source" field - required and enum value valid
   **Given** request does not contain Source or Source is not in [fastgpt, hand]
   **When** backend receives creation request
   **Then** return HTTP 400, error message indicates "Source is required and must be either fastgpt or hand".

2. Validate "Agent Name" field - required, length ≤ 50, and unique
   **Given** request does not contain Agent Name
   **When** backend receives creation request
   **Then** return HTTP 400, error message "Agent Name is required".

   **Given** request contains Agent Name with length > 50
   **When** backend validation finds length exceeds limit
   **Then** return HTTP 400, error message "Agent Name length cannot exceed 50 characters".

   **Given** request contains Agent Name that already exists in database
   **When** backend validation finds duplication
   **Then** return HTTP 409, error message "Agent Name already exists, please use a different name".

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
   **When** backend receives creation request
   **Then** return HTTP 400, error message "Description is required and cannot exceed 500 characters".

   **Given** request contains Description with length > 500
   **When** backend validation finds length exceeds limit
   **Then** return HTTP 400, error message "Description length cannot exceed 500 characters".

6. Validate "Category" field - required and must be in backend-configured primary category list
   **Given** request does not contain Category
   **When** backend receives creation request
   **Then** return HTTP 400, error message "Category is required, please select a valid category".

   **Given** request contains Category, but the category is not in backend-configured list
   **When** backend validates category and finds invalid
   **Then** return HTTP 400, error message "Category is invalid, please select from backend-configured categories".

7. Validate "Target System URL" field - required and format valid
   **Given** request does not contain Target System URL
   **When** backend receives creation request
   **Then** return HTTP 400, error message "Target System URL is required".

   **Given** request contains Target System URL, but does not start with http:// or https://
   **When** backend validates URL format
   **Then** return HTTP 400, error message "Target System URL format is invalid, must start with http:// or https://".

8. Validate "Visibility Scope" field - required and organization/personnel exists
   **Given** request does not contain Visibility Scope or passed value is empty
   **When** backend receives creation request
   **Then** return HTTP 400, error message "Visibility Scope is required, please select organization or personnel".

   **Given** request contains Visibility Scope, but selected organization/personnel does not exist in system or has no permission
   **When** backend validation finds invalid or non-existent
   **Then** return HTTP 400, error message "Visibility Scope contains invalid organization/personnel, please check".

9. Successful creation return result
   **Given** all fields in request pass validation
   **When** backend persists agent information
   **Then** return HTTP 201, response body contains complete information of newly created agent (including ID, creation time, etc.), and return JSON structure example as follows:
   ```json
   {
     "id": "123e4567-e89b-12d3-a456-426614174000",
     "source": "fastgpt",
     "agentName": "Example Agent",
     "tags": ["Large Language Model"],
     "iconUrl": "https://cdn.xuehua.ai/agents/icons/123e4567-e89b-12d3-a456-426614174000.png",
     "description": "This is a newly created agent for demonstration purposes",
     "category": "Intelligent Assistant",
     "targetSystemUrl": "https://api.xuehua.ai/agent/123",
     "visibilityScope": {
       "type": "organization",
       "value": ["R&D Department", "Product Department"]
     },
     "creator": "wwdzhang",
     "createdAt": "2025-06-05T08:30:00Z"
   }
   ```
   Visibility Scope can include two types: "organization" or "personnel"; Creator and Created Time are automatically filled by backend.

10. Exception scenario general return
    **Given** backend encounters system-level exceptions such as database or file storage during request processing
    **When** exception is caught
    **Then** return HTTP 500, error message "Internal server error, please try again later". 