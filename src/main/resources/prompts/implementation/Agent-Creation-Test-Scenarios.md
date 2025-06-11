# Test Scenarios for Agent Creation Feature

## 1. AgentController Test Scenarios
### Create AgentControllerTest class
1. Create AgentControllerTest class
2. Use @WebMvcTest annotation to test the AgentController class
3. Use @MockBean annotation to mock the AgentService class
4. Use @Autowired annotation to inject the MockMvc instance
5. Create test scenarios for AgentController based on the prompts below
6. Generate test code for each test scenario

#### should_return_created_agent_when_create_agent_given_valid_request
- Description: 测试使用有效请求创建智能体时返回201状态码和创建的智能体信息
- Input: 有效的CreateAgentRequest对象，包含所有必需字段
- Expected Output: 201 Created状态码，返回AgentResponse对象
- Verification Points:
    - HTTP状态码为201 Created
    - 返回的AgentResponse包含正确的智能体信息
    - 调用AgentService.createAgent方法一次
    - 响应体包含id、name、source等字段

#### should_return_bad_request_when_create_agent_given_missing_required_fields
- Description: 测试缺少必需字段时返回400错误
- Input: 缺少必需字段的CreateAgentRequest对象
- Expected Output: 400 Bad Request状态码，包含验证错误信息
- Verification Points:
    - HTTP状态码为400 Bad Request
    - 返回错误信息包含字段验证失败详情
    - 不调用AgentService.createAgent方法

#### should_return_conflict_when_create_agent_given_duplicate_name
- Description: 测试智能体名称重复时返回409冲突错误
- Input: 有效的CreateAgentRequest，但名称已存在
- Expected Output: 409 Conflict状态码，包含冲突错误信息
- Verification Points:
    - HTTP状态码为409 Conflict
    - 返回错误信息包含"AGENT_ALREADY_EXISTS"错误码
    - AgentService抛出AgentAlreadyExistsException

#### should_return_bad_request_when_create_agent_given_invalid_source
- Description: 测试无效的source值时返回400错误
- Input: CreateAgentRequest包含无效的source值
- Expected Output: 400 Bad Request状态码，包含无效source错误信息
- Verification Points:
    - HTTP状态码为400 Bad Request
    - 返回错误信息包含"INVALID_SOURCE"错误码
    - AgentService抛出InvalidSourceException

#### should_return_bad_request_when_create_agent_given_invalid_url_format
- Description: 测试无效的URL格式时返回400错误
- Input: CreateAgentRequest包含无效格式的iconUrl或targetSystemUrl
- Expected Output: 400 Bad Request状态码，包含URL格式错误信息
- Verification Points:
    - HTTP状态码为400 Bad Request
    - 返回错误信息包含相应的URL错误码
    - AgentService抛出相应的URL异常

## 2. AgentService Test Scenarios
### Create AgentServiceTest class
1. Create AgentServiceTest class
2. Use @Mock annotation to mock the AgentRepository class
3. Use @InjectMocks annotation to inject the AgentServiceImpl instance
4. Create test scenarios for AgentServiceImpl based on the prompts below
5. Generate test code for each test scenario

#### should_create_agent_successfully_when_create_agent_given_valid_request
- Description: 测试使用有效请求成功创建智能体
- Input: 有效的CreateAgentRequest和creator字符串
- Expected Output: 返回创建的Agent对象
- Verification Points:
    - 返回的Agent对象包含正确的属性值
    - Agent对象有生成的UUID作为id
    - createdAt和updatedAt字段被设置为当前时间
    - 调用AgentRepository.save方法一次

#### should_throw_agent_already_exists_exception_when_create_agent_given_duplicate_name
- Description: 测试智能体名称重复时抛出AgentAlreadyExistsException
- Input: CreateAgentRequest包含已存在的智能体名称
- Expected Output: 抛出AgentAlreadyExistsException
- Verification Points:
    - 抛出AgentAlreadyExistsException异常
    - 异常消息包含重复名称信息
    - 调用AgentRepository.findByName方法
    - 不调用AgentRepository.save方法

#### should_throw_invalid_source_exception_when_create_agent_given_invalid_source
- Description: 测试无效source值时抛出InvalidSourceException
- Input: CreateAgentRequest包含无效的source值
- Expected Output: 抛出InvalidSourceException
- Verification Points:
    - 抛出InvalidSourceException异常
    - 异常消息包含无效source信息
    - 不调用AgentRepository相关方法

#### should_throw_invalid_tag_exception_when_create_agent_given_invalid_tags
- Description: 测试无效标签时抛出InvalidTagException
- Input: CreateAgentRequest包含不在预定义列表中的标签
- Expected Output: 抛出InvalidTagException
- Verification Points:
    - 抛出InvalidTagException异常
    - 异常消息包含无效标签信息
    - 不调用AgentRepository相关方法

#### should_throw_invalid_icon_url_exception_when_create_agent_given_invalid_icon_url
- Description: 测试无效图标URL时抛出InvalidIconUrlException
- Input: CreateAgentRequest包含无效格式的iconUrl
- Expected Output: 抛出InvalidIconUrlException
- Verification Points:
    - 抛出InvalidIconUrlException异常
    - 异常消息包含URL格式错误信息
    - 不调用AgentRepository相关方法

#### should_throw_invalid_category_exception_when_create_agent_given_invalid_category
- Description: 测试无效分类时抛出InvalidCategoryException
- Input: CreateAgentRequest包含不在配置分类中的category
- Expected Output: 抛出InvalidCategoryException
- Verification Points:
    - 抛出InvalidCategoryException异常
    - 异常消息包含无效分类信息
    - 不调用AgentRepository相关方法

#### should_throw_invalid_target_system_url_exception_when_create_agent_given_invalid_target_url
- Description: 测试无效目标系统URL时抛出InvalidTargetSystemUrlException
- Input: CreateAgentRequest包含无效格式的targetSystemUrl
- Expected Output: 抛出InvalidTargetSystemUrlException
- Verification Points:
    - 抛出InvalidTargetSystemUrlException异常
    - 异常消息包含URL格式错误信息
    - 不调用AgentRepository相关方法

#### should_throw_invalid_visibility_scope_exception_when_create_agent_given_invalid_visibility_scope
- Description: 测试无效可见范围时抛出InvalidVisibilityScopeException
- Input: CreateAgentRequest包含无效的visibilityScope
- Expected Output: 抛出InvalidVisibilityScopeException
- Verification Points:
    - 抛出InvalidVisibilityScopeException异常
    - 异常消息包含可见范围错误信息
    - 不调用AgentRepository相关方法

## 3. AgentRepository Test Scenarios
### Create AgentRepositoryTest class
1. Create AgentRepositoryTest class
2. Use @Mock annotation to mock the AgentDAO
3. Use @InjectMocks annotation to inject the AgentRepositoryImpl instance
4. Create test scenarios for AgentRepositoryImpl based on the prompts below
5. Generate test code for each test scenario

#### should_save_agent_successfully_when_save_agent_given_valid_agent
- Description: 测试成功保存智能体
- Input: 有效的Agent对象
- Expected Output: 返回保存的Agent对象
- Verification Points:
    - 返回的Agent对象与输入对象属性一致
    - 调用AgentDAO.save方法一次
    - Agent对象正确转换为AgentPO对象
    - AgentPO对象正确转换回Agent对象

#### should_find_agent_by_name_when_find_by_name_given_existing_name
- Description: 测试根据名称查找存在的智能体
- Input: 存在的智能体名称
- Expected Output: 返回包含Agent对象的Optional
- Verification Points:
    - 返回的Optional包含正确的Agent对象
    - 调用AgentDAO.findByName方法一次
    - AgentPO对象正确转换为Agent对象

#### should_return_empty_optional_when_find_by_name_given_non_existing_name
- Description: 测试根据名称查找不存在的智能体
- Input: 不存在的智能体名称
- Expected Output: 返回空的Optional
- Verification Points:
    - 返回的Optional为空
    - 调用AgentDAO.findByName方法一次

## 4. AgentDAO Test Scenarios
### Create AgentDAOTest class
1. Create AgentDAOTest class
2. Use @DataJpaTest annotation to test the AgentDAO interface
3. Use @Autowired annotation to inject the TestEntityManager
4. Create test scenarios for AgentDAO based on the prompts below
5. Generate test code for each test scenario

#### should_save_agent_po_successfully_when_save_given_valid_agent_po
- Description: 测试成功保存AgentPO对象
- Input: 有效的AgentPO对象
- Expected Output: 返回保存的AgentPO对象，包含生成的ID
- Verification Points:
    - 返回的AgentPO对象包含所有属性
    - 对象被正确持久化到数据库
    - 可以通过ID查询到保存的对象

#### should_find_agent_po_by_name_when_find_by_name_given_existing_name
- Description: 测试根据名称查找存在的AgentPO
- Input: 存在的智能体名称
- Expected Output: 返回包含AgentPO对象的Optional
- Verification Points:
    - 返回的Optional包含正确的AgentPO对象
    - 对象属性与保存时一致

#### should_return_empty_optional_when_find_by_name_given_non_existing_name
- Description: 测试根据名称查找不存在的AgentPO
- Input: 不存在的智能体名称
- Expected Output: 返回空的Optional
- Verification Points:
    - 返回的Optional为空

#### should_enforce_unique_constraint_when_save_given_duplicate_name
- Description: 测试名称唯一约束
- Input: 两个具有相同名称的AgentPO对象
- Expected Output: 抛出数据完整性违反异常
- Verification Points:
    - 第一个对象保存成功
    - 第二个对象保存时抛出异常
    - 异常类型为DataIntegrityViolationException

## 5. Model Class Test Scenarios
### Create AgentTest class
1. Create AgentTest class
2. Create test scenarios for Agent class based on the prompts below
3. Generate test code for each test scenario

#### should_create_agent_from_request_when_from_request_given_valid_request_and_creator
- Description: 测试从CreateAgentRequest创建Agent对象
- Input: 有效的CreateAgentRequest和creator字符串
- Expected Output: 返回正确初始化的Agent对象
- Verification Points:
    - Agent对象包含生成的UUID作为id
    - 所有属性从request正确映射
    - creator字段设置正确
    - createdAt和updatedAt设置为当前时间
    - tags初始化为空列表（如果request中为null）

#### should_create_agent_from_po_when_from_po_given_valid_agent_po
- Description: 测试从AgentPO创建Agent对象
- Input: 有效的AgentPO对象
- Expected Output: 返回正确转换的Agent对象
- Verification Points:
    - 所有基本属性正确转换
    - JSON字符串正确转换为tags列表
    - visibilityScope正确重构
    - 日期时间字段正确转换

### Create AgentPOTest class
1. Create AgentPOTest class
2. Create test scenarios for AgentPO class based on the prompts below
3. Generate test code for each test scenario

#### should_create_agent_po_from_agent_when_of_given_valid_agent
- Description: 测试从Agent创建AgentPO对象
- Input: 有效的Agent对象
- Expected Output: 返回正确转换的AgentPO对象
- Verification Points:
    - 所有基本属性正确转换
    - tags列表正确转换为JSON字符串
    - visibilityScope正确分解为separate字段
    - 日期时间字段正确转换

### Create AgentResponseTest class
1. Create AgentResponseTest class
2. Create test scenarios for AgentResponse class based on the prompts below
3. Generate test code for each test scenario

#### should_create_response_from_agent_when_of_given_valid_agent
- Description: 测试从Agent创建AgentResponse对象
- Input: 有效的Agent对象
- Expected Output: 返回正确映射的AgentResponse对象
- Verification Points:
    - 所有属性正确映射
    - 不包含updatedAt字段
    - 对象结构符合API响应要求

## 6. Integration Test Scenarios
### Create AgentControllerIntegrationTest class
1. Create AgentControllerIntegrationTest class
2. Use @SpringBootTest annotation to test the AgentController class
3. Use @AutoConfigureMockMvc annotation to mock the AgentController class
4. Create test scenarios for AgentController based on the prompts below
5. Generate test code for each test scenario

#### should_create_agent_end_to_end_when_post_agents_given_valid_request
- Description: 测试端到端智能体创建流程
- Input: 完整的HTTP POST请求到/api/v1/agents
- Expected Output: 201 Created状态码和完整的AgentResponse
- Verification Points:
    - HTTP状态码为201 Created
    - 响应体包含完整的智能体信息
    - 数据库中存在创建的智能体记录
    - 所有业务逻辑正确执行

#### should_return_conflict_end_to_end_when_post_agents_given_duplicate_name
- Description: 测试端到端重复名称处理
- Input: 两次相同名称的智能体创建请求
- Expected Output: 第一次成功，第二次返回409 Conflict
- Verification Points:
    - 第一次请求返回201 Created
    - 第二次请求返回409 Conflict
    - 错误响应包含正确的错误信息
    - 数据库中只有一条记录

#### should_validate_request_end_to_end_when_post_agents_given_invalid_request
- Description: 测试端到端请求验证
- Input: 包含各种无效字段的请求
- Expected Output: 400 Bad Request状态码和验证错误信息
- Verification Points:
    - HTTP状态码为400 Bad Request
    - 响应包含详细的验证错误信息
    - 数据库中没有创建任何记录

## 7. Exception Handler Test Scenarios
### Create GlobalExceptionHandlerTest class
1. Create GlobalExceptionHandlerTest class
2. Use @WebMvcTest annotation to test exception handling
3. Create test scenarios for GlobalExceptionHandler based on the prompts below
4. Generate test code for each test scenario

#### should_handle_agent_already_exists_exception_when_exception_thrown
- Description: 测试AgentAlreadyExistsException处理
- Input: AgentAlreadyExistsException异常
- Expected Output: 409 Conflict状态码和标准错误响应
- Verification Points:
    - HTTP状态码为409 Conflict
    - 错误码为"AGENT_ALREADY_EXISTS"
    - 响应包含timestamp、status、error、code、message字段

#### should_handle_validation_exceptions_when_validation_fails
- Description: 测试各种验证异常处理
- Input: InvalidSourceException、InvalidTagException等验证异常
- Expected Output: 400 Bad Request状态码和相应错误响应
- Verification Points:
    - HTTP状态码为400 Bad Request
    - 错误码对应具体的验证失败类型
    - 响应格式一致

#### should_handle_generic_exception_when_unexpected_error_occurs
- Description: 测试通用异常处理
- Input: 未预期的Exception
- Expected Output: 500 Internal Server Error状态码和通用错误响应
- Verification Points:
    - HTTP状态码为500 Internal Server Error
    - 错误码为"INTERNAL_ERROR"
    - 错误消息为"服务器内部错误"

## 8. Constraints
- Test name should follow the format: `should_return_[your expected output]_when_[your action]_given_[your input]`
- All test classes should use appropriate Spring Boot test annotations
- Mock external dependencies using @Mock or @MockBean
- Use @InjectMocks or @Autowired for dependency injection in tests
- Verify both positive and negative test scenarios
- Include edge cases and boundary conditions
- Test exception handling scenarios thoroughly
- Use meaningful test data that reflects real-world usage
- Ensure test isolation and independence
- Include integration tests for end-to-end scenarios
- Verify database constraints and data integrity
- Test JSON serialization/deserialization
- Validate HTTP status codes and response formats
- Test security context and user authentication
- Include performance considerations for large datasets 