Background
Users need to be able to quickly find and browse intelligent agents based on different filtering criteria, including searching by name, filtering by tags, viewing by categories, etc. The system should provide flexible query interfaces that support multi-dimensional filtering combinations to help users quickly locate the required agents.

## Business Value
* **Improve Search Efficiency**: Users can quickly find specific agents through various condition combinations
* **Enhance User Experience**: Provide intuitive filtering and search functions to lower the usage threshold
* **Enhance Data Visibility**: Help users discover more relevant agents through categorization and tag display
* **Support Business Decisions**: Provide data support for administrators on agent usage statistics
* **Optimize Resource Utilization**: Avoid duplicate creation of agents with similar functions

## Scope In
* Fuzzy search based on agent names
* Exact filtering by tags
* Exact filtering by categories
* Multi-condition combined queries
* Pagination query support
* Basic sorting functions (by creation time, name)
* Permission scope filtering (only return agents visible to current user)
* Standardized return format for query results

## Scope Out
* Advanced search syntax support
* Full-text search functionality
* Returning detailed configuration information of agents
* Complex multi-table join queries
* Search history records
* Personalized recommendations for search results
* Export functionality

## Acceptance Criteria
AC1: Agent Name Fuzzy Search
**Given** user provides agent name keywords
**When** calling query API for name search
**Then** system returns list of all agents whose names contain the keywords, supporting Chinese and English search, case-insensitive

AC2: Tag Exact Filtering
**Given** user selects specific tags (Large Language Model, Voice Model, Image Model)
**When** calling query API for tag filtering
**Then** system returns list of all agents containing the specified tag

AC3: Category Exact Filtering
**Given** user selects specific category (Intelligent Assistant, Productivity Tools)
**When** calling query API for category filtering
**Then** system returns list of all agents under the specified category

AC4: Multi-condition Combined Query
**Given** user provides name keywords, tags, and category conditions simultaneously
**When** calling query API
**Then** system returns list of agents that satisfy all conditions (AND logic)

AC5: Pagination Query Support
**Given** user provides page number and page size parameters
**When** calling query API
**Then** system returns agent data for the specified page, including pagination information such as total records and total pages

AC6: Permission Scope Filtering
**Given** current logged-in user calls query API
**When** system processes the query request
**Then** only return agents that the user has permission to view, automatically filtering agents without access permission

AC7: Empty Query Condition Handling
**Given** user provides no filtering conditions
**When** calling query API
**Then** system returns list of all agents visible to current user, sorted by creation time in descending order

AC8: No Matching Results Handling
**Given** user-provided query conditions
**When** no matching agents exist in the system
**Then** system returns empty list with status code 200, including correct pagination information (total count is 0)

AC9: Sorting Function Support
**Given** user specifies sorting field (creation time, agent name) and sorting direction
**When** calling query API
**Then** system sorts results according to specified rules and returns them

AC10: Query Parameter Validation
**Given** user provides invalid query parameters (such as negative page numbers, oversized page size)
**When** calling query API
**Then** system returns 400 error with specific information about parameter validation failure

AC11: Return Field Standardization
**Given** query executes successfully
**When** system returns agent list
**Then** each agent contains standard fields: ID, name, tags, category, icon, description, creation time, but excludes sensitive information such as target system URLs 