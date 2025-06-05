## Requirements
Implement an API endpoint for creating new `[entity name]`.

## Business Model(mermaid)
```
classDiagram
direction TB

    class [EntityClass] {
        +String id
        +[OtherAttributeType] [otherAttributeName]
        +timestamp createdAt
        +timestamp updatedAt
    }

    class [RequestClass] {
        +[AttributeType] [attributeName]
        +[AttributeType] [attributeName]
        ...
    }

    class [ResponseClass] {
        +String id
        +[AttributeType] [attributeName]
        +timestamp createdAt
        +timestamp updatedAt
        ...
    }

    [EntityClass] "1" -- "1" [ResponseClass] : maps to
    [RequestClass] "1" -- "1" [EntityClass] : creates
```

## Solution
1. API Design:
    - Create POST endpoint `/api/v1/[entityNamePlural]` for creating new [Entity Name]
    - Return appropriate HTTP status codes for success and error cases
    - [Special handling logic, if any]

2. Exception Handling Pattern:
    - Create domain-specific business exceptions for different error scenarios
    - Implement global exception handler to provide consistent error response format
    - Map business exceptions to appropriate HTTP status codes
    - Return structured error responses with timestamp, status, error code, and message
    - Common exception types:
        - [EntityName]AlreadyExistsException (409 Conflict) - for duplicate resource creation
        - Invalid[AttributeName]Exception (400 Bad Request) - for invalid attribute values
        - [EntityName]NotFoundException (404 Not Found) - for resource not found scenarios
        - Invalid[BusinessRule]Exception (400 Bad Request) - for business rule violations

3. Idempotency Implementation:
    - Add unique identifiers to requests (such as client-generated UUID or business unique keys)
    - Add unique constraints in the database (such as name+owner combination or other business unique keys)
    - Implement checking logic in the service layer to avoid duplicate creation
    - For duplicate requests, return the existing resource instead of creating a new one
    - Implementation approaches:
        - Approach 1: Use natural business keys (e.g., add findBy[BusinessKey] method in [EntityClass]DAO)
        - Approach 2: Use request ID (e.g., add idempotencyKey parameter and store processed request IDs)
        - Approach 3: Use conditional checks (e.g., check if resource already exists before creation)

    - Idempotency implementation approach recommendations:
        - For simple business cases: Prefer Approach 1 (natural business keys) or Approach 3 (conditional checks), as they are straightforward to implement
        - For complex business cases with performance requirements: Prefer Approach 2 (request ID), as it can avoid duplicate business logic processing and improve performance
        - In practice, multiple approaches can be combined based on specific requirements to provide stronger idempotency guarantees

## Exception Handling Pattern

### Business Exception Design
1. **Exception Hierarchy**:
    - All business exceptions extend RuntimeException
    - Place exceptions in domain package: org.tw.[project].domain.[entityPackage]
    - Use descriptive names that clearly indicate the error scenario

2. **Exception Types**:
    - **Resource Conflict**: [EntityName]AlreadyExistsException (409 Conflict)
        - Used when attempting to create a resource that already exists
        - Example: "智能体名称已存在: 智能客服助手"
    - **Validation Errors**: Invalid[AttributeName]Exception (400 Bad Request)
        - Used for invalid attribute values or format violations
        - Example: "无效的分类: 无效分类"
    - **Business Rule Violations**: Invalid[BusinessRule]Exception (400 Bad Request)
        - Used when business logic constraints are violated
        - Example: "无效的可见范围"
    - **Resource Not Found**: [EntityName]NotFoundException (404 Not Found)
        - Used when requested resource cannot be found
        - Example: "智能体不存在: agent-id-123"

3. **Global Exception Handler**:
    - Use @RestControllerAdvice annotation
    - Provide consistent error response format across all APIs
    - Map specific exceptions to appropriate HTTP status codes
    - Include fallback handler for unexpected exceptions

4. **Error Response Format**:
    ```json
    {
        "timestamp": "2024-01-01T10:00:00",
        "status": 409,
        "error": "Conflict",
        "code": "AGENT_ALREADY_EXISTS",
        "message": "智能体名称已存在: 智能客服助手"
    }
    ```

5. **Exception Usage in Service Layer**:
    - Validate business rules and throw appropriate exceptions
    - Use descriptive error messages that help users understand the issue
    - Throw exceptions early to fail fast
    - Let GlobalExceptionHandler handle HTTP response mapping

## Structure

### Inheritance/Implementation Relationships
1. [EntityClass]Service interface defines [EntityClass] service methods
2. [EntityClass]ServiceImpl implements [EntityClass]Service interface
3. [EntityClass]Repository interface defines [EntityClass] repository methods
4. [EntityClass]RepositoryImpl implements [EntityClass]Repository interface

### Dependencies
1. [EntityClass]Controller calls [EntityClass]Service
2. [EntityClass]ServiceImpl calls [EntityClass]Repository
3. [EntityClass]RepositoryImpl calls [EntityClass]DAO

## Tasks

### Create Business Exception Classes
1. Create [EntityName]AlreadyExistsException class:
    - Extend RuntimeException
    - Constructor: [EntityName]AlreadyExistsException(String message)
    - Package: org.tw.[project].domain.[entityPackage]
2. Create Invalid[AttributeName]Exception class:
    - Extend RuntimeException
    - Constructor: Invalid[AttributeName]Exception(String message)
    - Package: org.tw.[project].domain.[entityPackage]
3. Create [EntityName]NotFoundException class (if needed):
    - Extend RuntimeException
    - Constructor: [EntityName]NotFoundException(String message)
    - Package: org.tw.[project].domain.[entityPackage]
4. Create Invalid[BusinessRule]Exception class (if needed):
    - Extend RuntimeException
    - Constructor: Invalid[BusinessRule]Exception(String message)
    - Package: org.tw.[project].domain.[entityPackage]

### Create/Update GlobalExceptionHandler class
1. Location: org.tw.[project].web.exception.GlobalExceptionHandler
2. Annotations: @RestControllerAdvice
3. Exception handlers:
    - @ExceptionHandler([EntityName]AlreadyExistsException.class)
        - Return buildErrorResponse(HttpStatus.CONFLICT, "[ENTITY_NAME]_ALREADY_EXISTS", e.getMessage())
    - @ExceptionHandler(Invalid[AttributeName]Exception.class)
        - Return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_[ATTRIBUTE_NAME]", e.getMessage())
    - @ExceptionHandler([EntityName]NotFoundException.class)
        - Return buildErrorResponse(HttpStatus.NOT_FOUND, "[ENTITY_NAME]_NOT_FOUND", e.getMessage())
    - @ExceptionHandler(Invalid[BusinessRule]Exception.class)
        - Return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_[BUSINESS_RULE]", e.getMessage())
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

### Create [EntityClass] class (Domain Model)
1. Attributes:
    - id: String
    - `[otherAttributeName]`: `[otherAttributeType]`
    - createdAt: LocalDateTime
    - updatedAt: LocalDateTime
2. Constructor:
    - Use @AllArgsConstructor annotation
3. Add static method: fromRequest([RequestClass] request): [EntityClass]
    - Logic:
        - Return a new [EntityClass] object with id set to a newly generated UUID
        - Extract and set other attributes from the request
4. Add static method: fromPO([EntityClass]PO po): [EntityClass]
    - Logic:
        - Convert persistence object to domain model object

### Create [EntityClass]PO class (Persistence Model)
1. Attributes:
    - id: String
    - `[otherAttributeName]`: `[otherAttributeType]`
    - createdAt: LocalDateTime
    - updatedAt: LocalDateTime
2. Add unique constraint:
    - Use @Table(uniqueConstraints = @UniqueConstraint(columnNames = {"[businessKeyField1]", "[businessKeyField2]"})) annotation
3. Add static method: of([EntityClass] entity): [EntityClass]PO
    - Logic:
        - Convert domain model object to persistence object

### Create [RequestClass] class
1. Attributes:
    - `[attributeName]`: `[attributeType]` (required/optional)
    - `[attributeName]`: `[attributeType]` (required/optional)
    - [If using Approach 2] `idempotencyKey`: String (for idempotency control)
    - ...
2. Constructor:
    - Use @AllArgsConstructor annotation
3. Validation:
    - `[requiredAttribute]`: @NotBlank/@NotNull
    - [Other validation annotations]

### Create [ResponseClass] class
1. Attributes:
    - id: String
    - `[otherAttributeName]`: `[otherAttributeType]`
    - createdAt: LocalDateTime
    - updatedAt: LocalDateTime
2. Constructor:
    - Use @AllArgsConstructor annotation
3. Add static method: of([EntityClass] entity): [ResponseClass]
    - Logic:
        - Map domain model object to response object
        - Return response object

### Create [EntityClass]Controller class to implement creation API
1. Endpoint: `/api/v1/[entityNamePlural]`
    1. Method: POST
    2. Request Body: [RequestClass]
    3. Response Body: Returns data of type [ResponseClass]
    4. Logic:
        - Call [entityClassLowerCase]Service.create[EntityClass](request)
        - Map [EntityClass] entity to [ResponseClass] DTO
        - Return 201 Created status code and the created data
        - Exception handling is delegated to GlobalExceptionHandler:
            - [EntityName]AlreadyExistsException → 409 Conflict
            - Invalid[AttributeName]Exception → 400 Bad Request
            - Invalid[BusinessRule]Exception → 400 Bad Request
            - Validation errors → 400 Bad Request
            - Generic exceptions → 500 Internal Server Error

### Create [EntityClass]Service interface
1. Add method: create[EntityClass]([RequestClass] request): [EntityClass]

### Create [EntityClass]ServiceImpl class to implement creation logic
1. Implement create[EntityClass] method:
    - Logic:
        - [Data validation logic, if any]
        - [If using Approach 1] Check if resource already exists by business key, if it does, throw [EntityName]AlreadyExistsException
        - [If using Approach 2] Check if idempotencyKey has been processed, if it has, return previous result
        - [If using Approach 3] Check if resource already exists based on conditions, if it does, throw [EntityName]AlreadyExistsException
        - Validate business rules and throw corresponding exceptions:
            - Throw Invalid[AttributeName]Exception for invalid attribute values
            - Throw Invalid[BusinessRule]Exception for business rule violations
        - Create new [EntityClass] object using [EntityClass].fromRequest(request)
        - Set necessary fields (id, createdAt, updatedAt, etc.)
        - [Null check logic, if any]
        - Call [entityClassLowerCase]Repository.save(entity) to save the entity
        - [If using Approach 2] Record the processed idempotencyKey and corresponding resource ID
        - Return the saved [EntityClass] object

### Create [EntityClass]Repository interface
1. Add method: save([EntityClass] entity): [EntityClass]
2. [If using Approach 1 or 3] Add method: findBy[BusinessKey]([BusinessKeyType] [businessKey]): Optional<[EntityClass]>

### Create [EntityClass]RepositoryImpl class to implement storage logic
1. Implement save method:
    - Logic:
        - Convert [EntityClass] entity to [EntityClass]PO using [EntityClass]PO.of(entity)
        - Call [entityClassLowerCase]DAO.save(po) to save the entity
        - Convert saved [EntityClass]PO back to [EntityClass] entity using [EntityClass].fromPO(po)
        - Return the saved [EntityClass] object
2. [If using Approach 1 or 3] Implement findBy[BusinessKey] method:
    - Logic:
        - Call [entityClassLowerCase]DAO.findBy[BusinessKey]([businessKey]) to find the entity
        - If found, convert [EntityClass]PO to [EntityClass]
        - Return Optional<[EntityClass]>

### Create [EntityClass]DAO interface
1. Extend JpaRepository<[EntityClass]PO, String>
2. [If using Approach 1 or 3] Add method: findBy[BusinessKey]([BusinessKeyType] [businessKey]): Optional<[EntityClass]PO>

### [If using Approach 2] Create IdempotencyKeyRepository interface and implementation class
1. For storing and checking processed idempotency keys
2. Add method: save(String key, String resourceId): void
3. Add method: findByKey(String key): Optional<String>

## Common Tasks
1. All repository implementation classes should be annotated with @Repository
2. All Repository classes should implement JPA repository
3. All Service classes should be annotated with @Service
4. All Controller classes should be annotated with @RestController
5. All DTO and model classes should be annotated with @Data
6. All POJO classes should be annotated with @Data, @Entity, @Table, @AllArgsConstructor, @NoArgsConstructor
7. Use enum classes if the data involves types
8. All business exception classes should extend RuntimeException and be placed in domain package
9. GlobalExceptionHandler should be annotated with @RestControllerAdvice
10. Service layer should throw business exceptions for validation failures and business rule violations
11. Controllers should not handle exceptions directly - delegate to GlobalExceptionHandler

## Constraints
- If request body is missing required fields, return 400 Bad Request
- After successful creation, return 201 Created status code
- If resource already exists, throw [EntityName]AlreadyExistsException (409 Conflict)
- Manually generate a unique ID for the entity, do not rely on database auto-generation
- Initialize collections as empty lists to prevent null pointer exceptions
- Add unique constraints at the database level to ensure idempotency
- Use domain-specific business exceptions for different error scenarios
- All exceptions should be handled by GlobalExceptionHandler for consistent error response format
- Error responses should include timestamp, status, error code, and descriptive message
- [Other specific constraints] 