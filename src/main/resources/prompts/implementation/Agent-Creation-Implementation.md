## Requirements
实现智能体（Agent）创建API端点，用于管理和调用不同来源的智能服务。

## Business Model(mermaid)
```
classDiagram
direction TB

    class Agent {
        +String id
        +AgentSource source
        +String name
        +List~String~ tags
        +String iconUrl
        +String description
        +String category
        +String targetSystemUrl
        +VisibilityScope visibilityScope
        +String creator
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }

    class CreateAgentRequest {
        +AgentSource source
        +String name
        +List~String~ tags
        +String iconUrl
        +String description
        +String category
        +String targetSystemUrl
        +VisibilityScope visibilityScope
    }

    class AgentResponse {
        +String id
        +AgentSource source
        +String name
        +List~String~ tags
        +String iconUrl
        +String description
        +String category
        +String targetSystemUrl
        +VisibilityScope visibilityScope
        +String creator
        +LocalDateTime createdAt
    }

    class VisibilityScope {
        +VisibilityType type
        +List~String~ values
    }

    class AgentSource {
        <<enumeration>>
        FASTGPT
        HAND
    }

    class VisibilityType {
        <<enumeration>>
        ORGANIZATION
        PERSON
        ALL
    }

    Agent "1" -- "1" AgentResponse : maps to
    CreateAgentRequest "1" -- "1" Agent : creates
    Agent "1" -- "1" VisibilityScope : contains
```

## Solution
1. API Design:
    - Create POST endpoint `/api/v1/agents` for creating new Agent
    - Return appropriate HTTP status codes for success and error cases
    - 支持智能体名称唯一性校验
    - 支持可见范围的组织/人员有效性校验

2. Exception Handling Pattern:
    - Create domain-specific business exceptions for different error scenarios
    - Implement global exception handler to provide consistent error response format
    - Map business exceptions to appropriate HTTP status codes
    - Return structured error responses with timestamp, status, error code, and message
    - Common exception types:
        - AgentAlreadyExistsException (409 Conflict) - for duplicate agent name
        - InvalidSourceException (400 Bad Request) - for invalid source values
        - InvalidTagException (400 Bad Request) - for invalid tag values
        - InvalidIconUrlException (400 Bad Request) - for invalid icon URL format
        - InvalidCategoryException (400 Bad Request) - for invalid category values
        - InvalidTargetSystemUrlException (400 Bad Request) - for invalid target system URL format
        - InvalidVisibilityScopeException (400 Bad Request) - for invalid visibility scope

3. Idempotency Implementation:
    - Use natural business keys (agent name uniqueness)
    - Add unique constraints in the database for agent name
    - Implement checking logic in the service layer to avoid duplicate creation
    - For duplicate requests, throw AgentAlreadyExistsException

## Exception Handling Pattern

### Business Exception Design
1. **Exception Hierarchy**:
    - All business exceptions extend RuntimeException
    - Place exceptions in exceptions package: org.tw.agent_backend_demo.exceptions
    - Use descriptive names that clearly indicate the error scenario

2. **Exception Types**:
    - **Resource Conflict**: AgentAlreadyExistsException (409 Conflict)
        - Used when attempting to create an agent with existing name
    - **Validation Errors**: 
        - InvalidSourceException (400 Bad Request) - for invalid source values
        - InvalidTagException (400 Bad Request) - for invalid tag values
        - InvalidIconUrlException (400 Bad Request) - for invalid icon URL format
        - InvalidCategoryException (400 Bad Request) - for invalid category values
        - InvalidTargetSystemUrlException (400 Bad Request) - for invalid target system URL format
        - InvalidVisibilityScopeException (400 Bad Request) - for invalid visibility scope

3. **Global Exception Handler**:
    - Use @RestControllerAdvice annotation
    - Provide consistent error response format across all APIs
    - Map specific exceptions to appropriate HTTP status codes
    - Include fallback handler for unexpected exceptions

4. **Exception Usage in Service Layer**:
    - Validate business rules and throw appropriate exceptions
    - Use descriptive error messages that help users understand the issue
    - Throw exceptions early to fail fast
    - Let GlobalExceptionHandler handle HTTP response mapping

## Structure

### Inheritance/Implementation Relationships
1. AgentService interface defines Agent service methods
2. AgentServiceImpl implements AgentService interface
3. AgentRepository interface defines Agent repository methods
4. AgentRepositoryImpl implements AgentRepository interface

### Dependencies
1. AgentController calls AgentService
2. AgentServiceImpl calls AgentRepository
3. AgentRepositoryImpl calls AgentDAO

## Tasks

### Create Business Exception Classes
1. Create AgentAlreadyExistsException class:
    - Extend RuntimeException
    - Constructor: AgentAlreadyExistsException(String message)
    - Package: org.tw.agent_backend_demo.exceptions
2. Create InvalidSourceException class:
    - Extend RuntimeException
    - Constructor: InvalidSourceException(String message)
    - Package: org.tw.agent_backend_demo.exceptions
3. Create InvalidTagException class:
    - Extend RuntimeException
    - Constructor: InvalidTagException(String message)
    - Package: org.tw.agent_backend_demo.exceptions
4. Create InvalidIconUrlException class:
    - Extend RuntimeException
    - Constructor: InvalidIconUrlException(String message)
    - Package: org.tw.agent_backend_demo.exceptions
5. Create InvalidCategoryException class:
    - Extend RuntimeException
    - Constructor: InvalidCategoryException(String message)
    - Package: org.tw.agent_backend_demo.exceptions
6. Create InvalidTargetSystemUrlException class:
    - Extend RuntimeException
    - Constructor: InvalidTargetSystemUrlException(String message)
    - Package: org.tw.agent_backend_demo.exceptions
7. Create InvalidVisibilityScopeException class:
    - Extend RuntimeException
    - Constructor: InvalidVisibilityScopeException(String message)
    - Package: org.tw.agent_backend_demo.exceptions

### Create/Update GlobalExceptionHandler class
1. Location: org.tw.agent_backend_demo.exceptions.GlobalExceptionHandler
2. Annotations: @RestControllerAdvice
3. Exception handlers:
    - @ExceptionHandler(AgentAlreadyExistsException.class)
        - Return buildErrorResponse(HttpStatus.CONFLICT, "AGENT_ALREADY_EXISTS", e.getMessage())
    - @ExceptionHandler(InvalidSourceException.class)
        - Return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_SOURCE", e.getMessage())
    - @ExceptionHandler(InvalidTagException.class)
        - Return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_TAG", e.getMessage())
    - @ExceptionHandler(InvalidIconUrlException.class)
        - Return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_ICON_URL", e.getMessage())
    - @ExceptionHandler(InvalidCategoryException.class)
        - Return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_CATEGORY", e.getMessage())
    - @ExceptionHandler(InvalidTargetSystemUrlException.class)
        - Return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_TARGET_SYSTEM_URL", e.getMessage())
    - @ExceptionHandler(InvalidVisibilityScopeException.class)
        - Return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_VISIBILITY_SCOPE", e.getMessage())
    - @ExceptionHandler(Exception.class)
        - Return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "服务器内部错误")
4. Helper method: buildErrorResponse(HttpStatus status, String code, String message)
    - Return ResponseEntity<Map<String, Object>>
    - Response structure:
        - timestamp: LocalDateTime.now()
        - status: status.value()
        - error: status.getReasonPhrase()
        - code: code
        - message: message

### Create Enum Classes
1. Create AgentSource enum:
    - Values: FASTGPT, HAND
    - Package: org.tw.agent_backend_demo.models.enums
2. Create VisibilityType enum:
    - Values: ORGANIZATION, PERSON, ALL
    - Package: org.tw.agent_backend_demo.models.enums

### Create VisibilityScope class
1. Attributes:
    - type: VisibilityType
    - values: List<String>
2. Constructor:
    - Use @AllArgsConstructor annotation
3. Package: org.tw.agent_backend_demo.models

### Create Agent class (Domain Model)
1. Package: org.tw.agent_backend_demo.models
2. Attributes:
    - id: String
    - source: AgentSource
    - name: String
    - tags: List<String>
    - iconUrl: String
    - description: String
    - category: String
    - targetSystemUrl: String
    - visibilityScope: VisibilityScope
    - creator: String
    - createdAt: LocalDateTime
    - updatedAt: LocalDateTime
3. Constructor:
    - Use @AllArgsConstructor annotation
4. Add static method: fromRequest(CreateAgentRequest request, String creator): Agent
    - Logic:
        - Return a new Agent object with id set to a newly generated UUID
        - Extract and set other attributes from the request
        - Set creator from parameter
        - Set createdAt and updatedAt to current time
        - Initialize tags as empty list if null
5. Add static method: fromPO(AgentPO po): Agent
    - Logic:
        - Convert persistence object to domain model object

### Create AgentPO class (Persistence Model)
1. Package: org.tw.agent_backend_demo.models
2. Attributes:
    - id: String
    - source: AgentSource
    - name: String
    - tags: String (JSON format)
    - iconUrl: String
    - description: String
    - category: String
    - targetSystemUrl: String
    - visibilityType: VisibilityType
    - visibilityValues: String (JSON format)
    - creator: String
    - createdAt: LocalDateTime
    - updatedAt: LocalDateTime
3. Add unique constraint:
    - Use @Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"})) annotation
4. Add static method: of(Agent entity): AgentPO
    - Logic:
        - Convert domain model object to persistence object
        - Convert tags list to JSON string
        - Convert visibilityScope to separate fields

### Create CreateAgentRequest class
1. Package: org.tw.agent_backend_demo.dto
2. Attributes:
    - source: AgentSource (required)
    - name: String (required, max 50 characters)
    - tags: List<String> (optional)
    - iconUrl: String (optional, URL format)
    - description: String (required, max 500 characters)
    - category: String (required)
    - targetSystemUrl: String (required, URL format)
    - visibilityScope: VisibilityScope (required)
3. Constructor:
    - Use @AllArgsConstructor annotation
4. Validation:
    - source: @NotNull
    - name: @NotBlank, @Size(max = 50)
    - description: @NotBlank, @Size(max = 500)
    - category: @NotBlank
    - targetSystemUrl: @NotBlank
    - visibilityScope: @NotNull

### Create AgentResponse class
1. Package: org.tw.agent_backend_demo.dto
2. Attributes:
    - id: String
    - source: AgentSource
    - name: String
    - tags: List<String>
    - iconUrl: String
    - description: String
    - category: String
    - targetSystemUrl: String
    - visibilityScope: VisibilityScope
    - creator: String
    - createdAt: LocalDateTime
3. Constructor:
    - Use @AllArgsConstructor annotation
4. Add static method: of(Agent entity): AgentResponse
    - Logic:
        - Map domain model object to response object
        - Return response object

### Create AgentController class to implement creation API
1. Package: org.tw.agent_backend_demo.controller
2. Endpoint: `/api/v1/agents`
    1. Method: POST
    2. Request Body: CreateAgentRequest
    3. Response Body: Returns data of type AgentResponse
    4. Logic:
        - Get current user from security context
        - Call agentService.createAgent(request, currentUser)
        - Map Agent entity to AgentResponse DTO
        - Return 201 Created status code and the created data
        - Exception handling is delegated to GlobalExceptionHandler:
            - AgentAlreadyExistsException → 409 Conflict
            - InvalidSourceException → 400 Bad Request
            - InvalidTagException → 400 Bad Request
            - InvalidIconUrlException → 400 Bad Request
            - InvalidCategoryException → 400 Bad Request
            - InvalidTargetSystemUrlException → 400 Bad Request
            - InvalidVisibilityScopeException → 400 Bad Request
            - Validation errors → 400 Bad Request
            - Generic exceptions → 500 Internal Server Error

### Create AgentService interface
1. Package: org.tw.agent_backend_demo.service
2. Add method: createAgent(CreateAgentRequest request, String creator): Agent

### Create AgentServiceImpl class to implement creation logic
1. Package: org.tw.agent_backend_demo.service
2. Implement createAgent method:
    - Logic:
        - Validate source enum value, throw InvalidSourceException if invalid
        - Validate tags against allowed values, throw InvalidTagException if invalid
        - Validate iconUrl format (http/https), throw InvalidIconUrlException if invalid
        - Validate category against configured categories, throw InvalidCategoryException if invalid
        - Validate targetSystemUrl format (http/https), throw InvalidTargetSystemUrlException if invalid
        - Validate visibilityScope (check organizations/persons exist), throw InvalidVisibilityScopeException if invalid
        - Check if agent name already exists, throw AgentAlreadyExistsException if exists
        - Create new Agent object using Agent.fromRequest(request, creator)
        - Call agentRepository.save(entity) to save the entity
        - Return the saved Agent object

### Create AgentRepository interface
1. Package: org.tw.agent_backend_demo.repository
2. Add method: save(Agent entity): Agent
3. Add method: findByName(String name): Optional<Agent>

### Create AgentRepositoryImpl class to implement storage logic
1. Package: org.tw.agent_backend_demo.repository
2. Implement save method:
    - Logic:
        - Convert Agent entity to AgentPO using AgentPO.of(entity)
        - Call agentDAO.save(po) to save the entity
        - Convert saved AgentPO back to Agent entity using Agent.fromPO(po)
        - Return the saved Agent object
3. Implement findByName method:
    - Logic:
        - Call agentDAO.findByName(name) to find the entity
        - If found, convert AgentPO to Agent
        - Return Optional<Agent>

### Create AgentDAO interface
1. Package: org.tw.agent_backend_demo.repository
2. Extend JpaRepository<AgentPO, String>
3. Add method: findByName(String name): Optional<AgentPO>

## Common Tasks
1. All repository implementation classes should be annotated with @Repository
2. All Repository classes should implement JPA repository
3. All Service classes should be annotated with @Service
4. All Controller classes should be annotated with @RestController
5. All DTO and model classes should be annotated with @Data
6. All POJO classes should be annotated with @Data, @Entity, @Table, @AllArgsConstructor, @NoArgsConstructor
7. Use enum classes for AgentSource and VisibilityType
8. All business exception classes should extend RuntimeException and be placed in exceptions package
9. GlobalExceptionHandler should be annotated with @RestControllerAdvice
10. Service layer should throw business exceptions for validation failures and business rule violations
11. Controllers should not handle exceptions directly - delegate to GlobalExceptionHandler

## Constraints
- If request body is missing required fields, return 400 Bad Request
- After successful creation, return 201 Created status code
- If agent name already exists, throw AgentAlreadyExistsException (409 Conflict)
- Manually generate a unique ID for the entity, do not rely on database auto-generation
- Initialize tags as empty lists to prevent null pointer exceptions
- Add unique constraints at the database level for agent name
- Use domain-specific business exceptions for different error scenarios
- All exceptions should be handled by GlobalExceptionHandler for consistent error response format
- Error responses should include timestamp, status, error code, and descriptive message
- Source field must be one of: fastgpt, hand
- Agent name must be unique and not exceed 50 characters
- Description must not exceed 500 characters
- Icon URL and target system URL must be valid HTTP/HTTPS URLs
- Tags must be from predefined list: "大语言模型", "语音模型", "图像模型"
- Category must be from configured categories: "智能助手", "效率工具"
- Visibility scope must contain valid organizations or persons
- Creator field should be automatically set from current user context 