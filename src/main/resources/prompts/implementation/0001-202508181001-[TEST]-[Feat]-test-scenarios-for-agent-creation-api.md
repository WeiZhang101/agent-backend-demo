# Test Scenarios for Agent Creation API

## 1. AgentController Test Scenarios
### Create AgentControllerTest class
1. Create AgentControllerTest class
2. Use @WebMvcTest annotation to test the AgentController class
3. Use @MockBean annotation to mock the AgentService class
4. Use @Autowired annotation to inject the MockMvc instance
5. Create test scenarios for AgentController based on the prompts below
6. Generate test code for each test scenario

#### should_return_201_and_agent_response_when_create_agent_given_valid_request
- Description: Test successful agent creation with valid request data
- Input: Valid CreateAgentRequest with all required fields (source: "fastgpt", agentName: "TestAgent", description: "Test Description", category: "AI", targetSystemUrl: "https://test.com", visibilityScope with ORGANIZATION type)
- Expected Output: HTTP 201 status with CreateAgentResponse containing generated id, creator, createdAt and all input fields
- Verification Points:
    - Response status is 201 CREATED
    - Response body contains all request fields
    - Response body contains system-generated id (UUID format)
    - Response body contains creator field
    - Response body contains createdAt timestamp
    - AgentService.createAgent is called exactly once
    - Content-Type is application/json

#### should_return_400_when_create_agent_given_missing_required_fields
- Description: Test validation failure when required fields are missing
- Input: CreateAgentRequest with missing required fields (null agentName, null description, null category, null targetSystemUrl)
- Expected Output: HTTP 400 status with ErrorResponse containing validation error details
- Verification Points:
    - Response status is 400 BAD REQUEST
    - Response body contains ErrorResponse with appropriate error codes
    - Error message mentions missing required fields
    - AgentService.createAgent is never called
    - Timestamp is present in error response
    - Path is present in error response

#### should_return_400_when_create_agent_given_invalid_agent_name_length
- Description: Test validation failure when agent name exceeds 50 characters
- Input: CreateAgentRequest with agentName longer than 50 characters
- Expected Output: HTTP 400 status with ErrorResponse containing validation error for agentName
- Verification Points:
    - Response status is 400 BAD REQUEST
    - Error message specifically mentions agentName length constraint
    - AgentService.createAgent is never called

#### should_return_400_when_create_agent_given_invalid_description_length
- Description: Test validation failure when description exceeds 500 characters
- Input: CreateAgentRequest with description longer than 500 characters
- Expected Output: HTTP 400 status with ErrorResponse containing validation error for description
- Verification Points:
    - Response status is 400 BAD REQUEST
    - Error message specifically mentions description length constraint
    - AgentService.createAgent is never called

#### should_return_400_when_create_agent_given_invalid_url_format
- Description: Test validation failure when URLs have invalid format
- Input: CreateAgentRequest with invalid iconUrl and targetSystemUrl (not starting with http/https)
- Expected Output: HTTP 400 status with ErrorResponse containing URL validation errors
- Verification Points:
    - Response status is 400 BAD REQUEST
    - Error message mentions URL format validation
    - AgentService.createAgent is never called

#### should_return_409_when_create_agent_given_duplicate_agent_name
- Description: Test business logic failure when agent name already exists
- Input: Valid CreateAgentRequest with existing agentName
- Expected Output: HTTP 409 status with ErrorResponse containing AgentNameExistsException details
- Verification Points:
    - Response status is 409 CONFLICT
    - Error code is AGENT_NAME_EXISTS
    - Error message mentions duplicate agent name
    - AgentService.createAgent throws AgentNameExistsException

#### should_return_400_when_create_agent_given_invalid_visibility_scope
- Description: Test business logic failure when visibility scope is invalid
- Input: CreateAgentRequest with invalid visibilityScope (empty values list)
- Expected Output: HTTP 400 status with ErrorResponse containing InvalidVisibilityScopeException details
- Verification Points:
    - Response status is 400 BAD REQUEST
    - Error code is INVALID_VISIBILITY_SCOPE
    - Error message mentions invalid visibility scope
    - AgentService.createAgent throws InvalidVisibilityScopeException

#### should_return_500_when_create_agent_given_unexpected_error
- Description: Test handling of unexpected system errors
- Input: Valid CreateAgentRequest
- Expected Output: HTTP 500 status with ErrorResponse containing generic error message
- Verification Points:
    - Response status is 500 INTERNAL SERVER ERROR
    - Error message is generic and doesn't expose internal details
    - AgentService.createAgent throws unexpected RuntimeException

## 2. AgentService Test Scenarios
### Create AgentServiceImplTest class
1. Create AgentServiceImplTest class
2. Use @Mock annotation to mock the AgentRepository class
3. Use @InjectMocks annotation to inject the AgentServiceImpl instance
4. Create test scenarios for AgentServiceImpl based on the prompts below
5. Generate test code for each test scenario

#### should_return_create_agent_response_when_create_agent_given_valid_request
- Description: Test successful agent creation with all business logic validation
- Input: Valid CreateAgentRequest with unique agentName and valid visibilityScope
- Expected Output: CreateAgentResponse with all fields populated including system-generated fields
- Verification Points:
    - AgentRepository.existsByAgentName returns false (name is unique)
    - Agent domain object is created with correct fields
    - System fields (id, creator, createdAt) are properly initialized
    - AgentPO is saved to repository
    - Response contains all original request data plus system fields
    - Creator field is properly set
    - CreatedAt timestamp is within reasonable time range

#### should_throw_agent_name_exists_exception_when_create_agent_given_duplicate_name
- Description: Test exception handling when agent name already exists
- Input: CreateAgentRequest with agentName that already exists in database
- Expected Output: AgentNameExistsException with appropriate error details
- Verification Points:
    - AgentRepository.existsByAgentName returns true
    - AgentNameExistsException is thrown with correct error code
    - Exception message contains the duplicate agent name
    - AgentRepository.save is never called
    - Exception error code is AGENT_NAME_EXISTS

#### should_throw_invalid_visibility_scope_exception_when_create_agent_given_empty_values
- Description: Test exception handling when visibility scope values are empty
- Input: CreateAgentRequest with visibilityScope having empty values list
- Expected Output: InvalidVisibilityScopeException with appropriate error details
- Verification Points:
    - InvalidVisibilityScopeException is thrown during validation
    - Exception message mentions empty values
    - Exception error code is INVALID_VISIBILITY_SCOPE
    - AgentRepository.save is never called

#### should_throw_invalid_url_format_exception_when_create_agent_given_malformed_urls
- Description: Test exception handling when URLs are malformed
- Input: CreateAgentRequest with malformed iconUrl or targetSystemUrl
- Expected Output: InvalidUrlFormatException with appropriate error details
- Verification Points:
    - InvalidUrlFormatException is thrown during validation
    - Exception message contains the invalid URL
    - Exception error code is INVALID_URL_FORMAT
    - AgentRepository.save is never called

#### should_handle_data_integrity_violation_when_create_agent_given_database_constraint_error
- Description: Test handling of database constraint violations
- Input: Valid CreateAgentRequest but database throws DataIntegrityViolationException
- Expected Output: AgentNameExistsException converted from database exception
- Verification Points:
    - AgentRepository.save throws DataIntegrityViolationException
    - Exception is caught and converted to AgentNameExistsException
    - Original cause is preserved in the exception

## 3. AgentRepository Test Scenarios
### Create AgentRepositoryTest class
1. Create AgentRepositoryTest class
2. Use @DataJpaTest annotation to test the AgentRepository interface
3. Use @Autowired annotation to inject the TestEntityManager and AgentRepository
4. Create test scenarios for AgentRepository based on the prompts below
5. Generate test code for each test scenario

#### should_return_true_when_exists_by_agent_name_given_existing_name
- Description: Test existsByAgentName method with existing agent name
- Input: AgentPO entity saved in database with specific agentName
- Expected Output: true when checking if agent name exists
- Verification Points:
    - AgentPO is persisted in test database
    - existsByAgentName returns true for existing name
    - Method is case-sensitive

#### should_return_false_when_exists_by_agent_name_given_non_existing_name
- Description: Test existsByAgentName method with non-existing agent name
- Input: Empty database or database without the queried agent name
- Expected Output: false when checking if agent name exists
- Verification Points:
    - existsByAgentName returns false for non-existing name
    - Method handles null input gracefully

#### should_save_and_return_agent_po_when_save_given_valid_agent_po
- Description: Test save method with valid AgentPO entity
- Input: Valid AgentPO with all required fields
- Expected Output: Saved AgentPO with generated id and preserved fields
- Verification Points:
    - AgentPO is successfully saved to database
    - Generated id is not null and is UUID format
    - All field values are preserved after save
    - Database constraints are enforced (unique agentName)

#### should_return_agent_po_when_find_by_id_given_existing_id
- Description: Test findById method with existing agent id
- Input: AgentPO entity saved in database with known id
- Expected Output: Optional containing the AgentPO entity
- Verification Points:
    - findById returns Optional.isPresent() = true
    - Retrieved entity matches saved entity
    - All fields are correctly retrieved

#### should_return_empty_optional_when_find_by_id_given_non_existing_id
- Description: Test findById method with non-existing agent id
- Input: Random UUID that doesn't exist in database
- Expected Output: Empty Optional
- Verification Points:
    - findById returns Optional.isEmpty() = true
    - No exception is thrown for non-existing id

## 4. Model Class Test Scenarios
### Create AgentTest class
1. Create AgentTest class
2. Create test scenarios for Agent domain model based on the prompts below
3. Generate test code for each test scenario

#### should_convert_to_agent_po_when_to_agent_po_given_valid_agent
- Description: Test conversion from Agent domain model to AgentPO entity
- Input: Agent object with all fields populated
- Expected Output: AgentPO object with matching field values
- Verification Points:
    - All fields are correctly mapped from Agent to AgentPO
    - UUID id is preserved
    - VisibilityScope is properly converted
    - Tags list is properly handled

#### should_convert_to_create_agent_response_when_to_create_agent_response_given_valid_agent
- Description: Test conversion from Agent domain model to CreateAgentResponse DTO
- Input: Agent object with all fields including system-generated fields
- Expected Output: CreateAgentResponse object with all field values
- Verification Points:
    - All fields are correctly mapped to response DTO
    - System fields (id, creator, createdAt) are included
    - VisibilityScope is properly serialized

#### should_initialize_system_fields_when_initialize_system_fields_given_creator
- Description: Test system field initialization in Agent domain object
- Input: Agent object without system fields, creator name
- Expected Output: Agent object with initialized id, creator, and createdAt
- Verification Points:
    - Id is generated and is valid UUID
    - Creator field is set to provided value
    - CreatedAt is set to current timestamp
    - Other fields remain unchanged

### Create AgentPOTest class
1. Create AgentPOTest class
2. Create test scenarios for AgentPO entity based on the prompts below
3. Generate test code for each test scenario

#### should_convert_to_agent_when_to_agent_given_valid_agent_po
- Description: Test conversion from AgentPO entity to Agent domain model
- Input: AgentPO object with all fields populated
- Expected Output: Agent object with matching field values
- Verification Points:
    - All fields are correctly mapped from AgentPO to Agent
    - VisibilityScope is properly converted
    - Tags list is properly handled
    - System fields are preserved

### Create VisibilityScopeTest class
1. Create VisibilityScopeTest class
2. Create test scenarios for VisibilityScope value object based on the prompts below
3. Generate test code for each test scenario

#### should_pass_validation_when_validate_scope_given_valid_scope
- Description: Test successful validation of valid visibility scope
- Input: VisibilityScope with valid type and non-empty values list
- Expected Output: No exception thrown
- Verification Points:
    - validateScope() completes without throwing exception
    - Both ORGANIZATION and PERSONNEL types are supported
    - Non-empty values list passes validation

#### should_throw_exception_when_validate_scope_given_null_values
- Description: Test validation failure when values list is null
- Input: VisibilityScope with null values list
- Expected Output: InvalidVisibilityScopeException thrown
- Verification Points:
    - InvalidVisibilityScopeException is thrown
    - Exception message mentions null values
    - Exception error code is INVALID_VISIBILITY_SCOPE

#### should_throw_exception_when_validate_scope_given_empty_values
- Description: Test validation failure when values list is empty
- Input: VisibilityScope with empty values list
- Expected Output: InvalidVisibilityScopeException thrown
- Verification Points:
    - InvalidVisibilityScopeException is thrown
    - Exception message mentions empty values
    - Exception error code is INVALID_VISIBILITY_SCOPE

## 5. DTO Test Scenarios
### Create CreateAgentRequestTest class
1. Create CreateAgentRequestTest class
2. Create test scenarios for CreateAgentRequest DTO based on the prompts below
3. Generate test code for each test scenario

#### should_convert_to_agent_when_to_agent_given_valid_request
- Description: Test conversion from CreateAgentRequest DTO to Agent domain model
- Input: Valid CreateAgentRequest with all fields populated
- Expected Output: Agent object with matching field values
- Verification Points:
    - All fields are correctly mapped from request to Agent
    - VisibilityScope is properly converted
    - Tags list is properly handled
    - System fields are not set (will be initialized later)

#### should_fail_validation_when_bean_validation_given_invalid_fields
- Description: Test Bean Validation annotations on CreateAgentRequest fields
- Input: CreateAgentRequest with various invalid field values
- Expected Output: ConstraintViolationException with validation details
- Verification Points:
    - @NotNull validation works for required fields
    - @Size validation works for agentName and description
    - @Pattern validation works for URL fields
    - @Valid validation works for nested VisibilityScope

## 6. Integration Test Scenarios
### Create AgentControllerIntegrationTest class
1. Create AgentControllerIntegrationTest class
2. Use @SpringBootTest annotation to test the AgentController class
3. Use @AutoConfigureMockMvc annotation to configure MockMvc
4. Use @Transactional annotation for test data isolation
5. Create test scenarios for AgentController integration based on the prompts below
6. Generate test code for each test scenario

#### should_create_agent_successfully_when_post_agent_given_valid_request_end_to_end
- Description: Test complete agent creation flow from HTTP request to database persistence
- Input: Valid JSON request body with all required fields
- Expected Output: HTTP 201 with complete response and agent saved in database
- Verification Points:
    - HTTP POST to /api/agents returns 201
    - Response contains all request data plus generated fields
    - Agent is actually saved in database
    - Database constraints are enforced
    - Response JSON format is correct

#### should_handle_duplicate_name_error_when_post_agent_given_existing_name_end_to_end
- Description: Test complete duplicate name handling from HTTP request to exception response
- Input: Valid JSON request with agent name that already exists in database
- Expected Output: HTTP 409 with detailed error response
- Verification Points:
    - First agent creation succeeds
    - Second agent creation with same name returns 409
    - Error response follows ErrorResponse format
    - Database state is consistent (only one agent exists)

#### should_handle_validation_errors_when_post_agent_given_invalid_request_end_to_end
- Description: Test complete validation error handling from HTTP request to error response
- Input: JSON request with multiple validation errors
- Expected Output: HTTP 400 with detailed validation error response
- Verification Points:
    - Request with invalid fields returns 400
    - Error response contains all validation errors
    - No data is saved to database
    - Error response follows ErrorResponse format

## Constraints
- Test name should follow the format: `should_return_[your expected output]_when_[your action]_given_[your input]`
- All tests should be independent and able to run in any order
- Use @Transactional for database tests to ensure cleanup
- Mock external dependencies appropriately
- Test both positive and negative scenarios
- Include edge cases and boundary conditions
- Verify both functional and non-functional requirements
- Use meaningful test data that reflects real-world usage
- Assert on specific error codes and messages for exception scenarios
- Test the complete request-response cycle in integration tests
- Verify database state changes in persistence tests
- Include performance tests for concurrent scenarios
