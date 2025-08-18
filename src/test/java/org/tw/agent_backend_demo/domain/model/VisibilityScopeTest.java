package org.tw.agent_backend_demo.domain.model;

import org.junit.jupiter.api.Test;
import org.tw.agent_backend_demo.exception.InvalidVisibilityScopeException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for VisibilityScope value object.
 * Tests validation logic and business rules.
 */
class VisibilityScopeTest {

    @Test
    void should_pass_validation_when_validate_scope_given_valid_organization_scope() {
        // Given
        VisibilityScope visibilityScope = VisibilityScope.builder()
                .type(ScopeType.ORGANIZATION)
                .values(List.of("org1", "org2", "org3"))
                .build();

        // When & Then
        assertThatCode(() -> visibilityScope.validateScope())
                .doesNotThrowAnyException();
    }

    @Test
    void should_pass_validation_when_validate_scope_given_valid_personnel_scope() {
        // Given
        VisibilityScope visibilityScope = VisibilityScope.builder()
                .type(ScopeType.PERSONNEL)
                .values(List.of("user1", "user2"))
                .build();

        // When & Then
        assertThatCode(() -> visibilityScope.validateScope())
                .doesNotThrowAnyException();
    }

    @Test
    void should_pass_validation_when_validate_scope_given_single_value() {
        // Given
        VisibilityScope visibilityScope = VisibilityScope.builder()
                .type(ScopeType.ORGANIZATION)
                .values(List.of("single-org"))
                .build();

        // When & Then
        assertThatCode(() -> visibilityScope.validateScope())
                .doesNotThrowAnyException();
    }

    @Test
    void should_throw_exception_when_validate_scope_given_null_type() {
        // Given
        VisibilityScope visibilityScope = VisibilityScope.builder()
                .type(null)
                .values(List.of("org1"))
                .build();

        // When & Then
        assertThatThrownBy(() -> visibilityScope.validateScope())
                .isInstanceOf(InvalidVisibilityScopeException.class)
                .hasMessageContaining("type cannot be null")
                .satisfies(ex -> {
                    InvalidVisibilityScopeException exception = (InvalidVisibilityScopeException) ex;
                    assertThat(exception.getErrorCode()).isEqualTo("INVALID_VISIBILITY_SCOPE");
                });
    }

    @Test
    void should_throw_exception_when_validate_scope_given_null_values() {
        // Given
        VisibilityScope visibilityScope = VisibilityScope.builder()
                .type(ScopeType.ORGANIZATION)
                .values(null)
                .build();

        // When & Then
        assertThatThrownBy(() -> visibilityScope.validateScope())
                .isInstanceOf(InvalidVisibilityScopeException.class)
                .hasMessageContaining("values cannot be empty")
                .satisfies(ex -> {
                    InvalidVisibilityScopeException exception = (InvalidVisibilityScopeException) ex;
                    assertThat(exception.getErrorCode()).isEqualTo("INVALID_VISIBILITY_SCOPE");
                });
    }

    @Test
    void should_throw_exception_when_validate_scope_given_empty_values() {
        // Given
        VisibilityScope visibilityScope = VisibilityScope.builder()
                .type(ScopeType.PERSONNEL)
                .values(List.of())
                .build();

        // When & Then
        assertThatThrownBy(() -> visibilityScope.validateScope())
                .isInstanceOf(InvalidVisibilityScopeException.class)
                .hasMessageContaining("values cannot be empty")
                .satisfies(ex -> {
                    InvalidVisibilityScopeException exception = (InvalidVisibilityScopeException) ex;
                    assertThat(exception.getErrorCode()).isEqualTo("INVALID_VISIBILITY_SCOPE");
                });
    }

    @Test
    void should_maintain_immutability_when_validate_scope_given_valid_scope() {
        // Given
        List<String> originalValues = List.of("org1", "org2");
        VisibilityScope visibilityScope = VisibilityScope.builder()
                .type(ScopeType.ORGANIZATION)
                .values(originalValues)
                .build();

        // When
        visibilityScope.validateScope();

        // Then
        assertThat(visibilityScope.getType()).isEqualTo(ScopeType.ORGANIZATION);
        assertThat(visibilityScope.getValues()).containsExactlyElementsOf(originalValues);
    }

    @Test
    void should_validate_both_scope_types_when_validate_scope_given_different_types() {
        // Given
        VisibilityScope organizationScope = VisibilityScope.builder()
                .type(ScopeType.ORGANIZATION)
                .values(List.of("engineering", "marketing"))
                .build();

        VisibilityScope personnelScope = VisibilityScope.builder()
                .type(ScopeType.PERSONNEL)
                .values(List.of("john.doe", "jane.smith"))
                .build();

        // When & Then
        assertThatCode(() -> {
            organizationScope.validateScope();
            personnelScope.validateScope();
        }).doesNotThrowAnyException();

        assertThat(organizationScope.getType()).isEqualTo(ScopeType.ORGANIZATION);
        assertThat(personnelScope.getType()).isEqualTo(ScopeType.PERSONNEL);
        assertThat(organizationScope.getValues()).containsExactly("engineering", "marketing");
        assertThat(personnelScope.getValues()).containsExactly("john.doe", "jane.smith");
    }

    @Test
    void should_handle_duplicate_values_when_validate_scope_given_duplicate_entries() {
        // Given
        VisibilityScope visibilityScope = VisibilityScope.builder()
                .type(ScopeType.ORGANIZATION)
                .values(List.of("org1", "org2", "org1")) // Duplicate org1
                .build();

        // When & Then
        // Validation should pass - business logic doesn't prevent duplicates
        assertThatCode(() -> visibilityScope.validateScope())
                .doesNotThrowAnyException();
        
        assertThat(visibilityScope.getValues()).containsExactly("org1", "org2", "org1");
    }
}
