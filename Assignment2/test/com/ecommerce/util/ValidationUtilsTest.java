package com.ecommerce.util;

import com.ecommerce.util.ValidationUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Comprehensive JUnit 5 unit tests for ValidationUtils.
 * Tests all validation methods with valid inputs, edge cases, and error conditions.
 */
@DisplayName("ValidationUtils Unit Tests")
public class ValidationUtilsTest {
    
    // String validation tests
    @Test
    @DisplayName("Valid string should return trimmed value")
    void testValidateString_ValidInput() {
        String result = ValidationUtils.validateString("  valid string  ", "testField");
        assertEquals("valid string", result);
    }
    
    @Test
    @DisplayName("Null string should throw IllegalArgumentException")
    void testValidateString_NullInput() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validateString(null, "testField")
        );
        assertTrue(exception.getMessage().contains("testField cannot be null"));
    }
    
    @Test
    @DisplayName("Empty string should throw IllegalArgumentException")
    void testValidateString_EmptyInput() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validateString("", "testField")
        );
        assertTrue(exception.getMessage().contains("testField cannot be empty"));
    }
    
    @Test
    @DisplayName("Whitespace-only string should throw IllegalArgumentException")
    void testValidateString_WhitespaceOnly() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validateString("   ", "testField")
        );
        assertTrue(exception.getMessage().contains("testField cannot be empty"));
    }
    
    // Positive double validation tests
    @Test
    @DisplayName("Positive double should return unchanged value")
    void testValidatePositiveDouble_ValidInput() {
        double result = ValidationUtils.validatePositiveDouble(10.5, "testField");
        assertEquals(10.5, result);
    }
    
    @Test
    @DisplayName("Zero double should throw IllegalArgumentException")
    void testValidatePositiveDouble_ZeroInput() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validatePositiveDouble(0.0, "testField")
        );
        assertTrue(exception.getMessage().contains("testField must be positive"));
    }
    
    @Test
    @DisplayName("Negative double should throw IllegalArgumentException")
    void testValidatePositiveDouble_NegativeInput() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validatePositiveDouble(-5.0, "testField")
        );
        assertTrue(exception.getMessage().contains("testField must be positive"));
    }
    
    @Test
    @DisplayName("NaN double should throw IllegalArgumentException")
    void testValidatePositiveDouble_NaN() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validatePositiveDouble(Double.NaN, "testField")
        );
        assertTrue(exception.getMessage().contains("testField cannot be NaN"));
    }
    
    @Test
    @DisplayName("Infinite double should throw IllegalArgumentException")
    void testValidatePositiveDouble_Infinite() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validatePositiveDouble(Double.POSITIVE_INFINITY, "testField")
        );
        assertTrue(exception.getMessage().contains("testField cannot be infinite"));
    }
    
    // Positive integer validation tests
    @Test
    @DisplayName("Positive integer should return unchanged value")
    void testValidatePositiveInteger_ValidInput() {
        int result = ValidationUtils.validatePositiveInteger(5, "testField");
        assertEquals(5, result);
    }
    
    @Test
    @DisplayName("Zero integer should throw IllegalArgumentException")
    void testValidatePositiveInteger_ZeroInput() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validatePositiveInteger(0, "testField")
        );
        assertTrue(exception.getMessage().contains("testField must be positive"));
    }
    
    @Test
    @DisplayName("Negative integer should throw IllegalArgumentException")
    void testValidatePositiveInteger_NegativeInput() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validatePositiveInteger(-1, "testField")
        );
        assertTrue(exception.getMessage().contains("testField must be positive"));
    }
    
    // Non-negative double validation tests
    @Test
    @DisplayName("Non-negative double should return unchanged value")
    void testValidateNonNegativeDouble_ValidInputs() {
        assertEquals(10.5, ValidationUtils.validateNonNegativeDouble(10.5, "testField"));
        assertEquals(0.0, ValidationUtils.validateNonNegativeDouble(0.0, "testField"));
    }
    
    @Test
    @DisplayName("Negative double should throw IllegalArgumentException")
    void testValidateNonNegativeDouble_NegativeInput() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validateNonNegativeDouble(-1.0, "testField")
        );
        assertTrue(exception.getMessage().contains("testField cannot be negative"));
    }
    
    // Non-negative integer validation tests
    @Test
    @DisplayName("Non-negative integer should return unchanged value")
    void testValidateNonNegativeInteger_ValidInputs() {
        assertEquals(5, ValidationUtils.validateNonNegativeInteger(5, "testField"));
        assertEquals(0, ValidationUtils.validateNonNegativeInteger(0, "testField"));
    }
    
    @Test
    @DisplayName("Negative integer should throw IllegalArgumentException")
    void testValidateNonNegativeInteger_NegativeInput() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validateNonNegativeInteger(-1, "testField")
        );
        assertTrue(exception.getMessage().contains("testField cannot be negative"));
    }
    
    // Not null validation tests
    @Test
    @DisplayName("Non-null object should return unchanged value")
    void testValidateNotNull_ValidInput() {
        String input = "test";
        String result = ValidationUtils.validateNotNull(input, "testField");
        assertSame(input, result);
    }
    
    @Test
    @DisplayName("Null object should throw IllegalArgumentException")
    void testValidateNotNull_NullInput() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validateNotNull(null, "testField")
        );
        assertTrue(exception.getMessage().contains("testField cannot be null"));
    }
    
    // Collection validation tests
    @Test
    @DisplayName("Non-empty collection should return unchanged value")
    void testValidateNotEmpty_ValidInput() {
        List<String> input = Arrays.asList("item1", "item2");
        Collection<String> result = ValidationUtils.validateNotEmpty(input, "testField");
        assertSame(input, result);
    }
    
    @Test
    @DisplayName("Null collection should throw IllegalArgumentException")
    void testValidateNotEmpty_NullInput() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validateNotEmpty(null, "testField")
        );
        assertTrue(exception.getMessage().contains("testField cannot be null"));
    }
    
    @Test
    @DisplayName("Empty collection should throw IllegalArgumentException")
    void testValidateNotEmpty_EmptyInput() {
        List<String> emptyList = new ArrayList<>();
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validateNotEmpty(emptyList, "testField")
        );
        assertTrue(exception.getMessage().contains("testField cannot be empty"));
    }
    
    // Date range validation tests
    @Test
    @DisplayName("Valid date range should not throw exception")
    void testValidateDateRange_ValidRange() {
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 12, 31);
        assertDoesNotThrow(() -> ValidationUtils.validateDateRange(start, end));
    }
    
    @Test
    @DisplayName("Same start and end date should not throw exception")
    void testValidateDateRange_SameDate() {
        LocalDate date = LocalDate.of(2023, 6, 15);
        assertDoesNotThrow(() -> ValidationUtils.validateDateRange(date, date));
    }
    
    @Test
    @DisplayName("Start date after end date should throw IllegalArgumentException")
    void testValidateDateRange_InvalidRange() {
        LocalDate start = LocalDate.of(2023, 12, 31);
        LocalDate end = LocalDate.of(2023, 1, 1);
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validateDateRange(start, end)
        );
        assertTrue(exception.getMessage().contains("Start date"));
        assertTrue(exception.getMessage().contains("cannot be after end date"));
    }
    
    @Test
    @DisplayName("Null start date should throw IllegalArgumentException")
    void testValidateDateRange_NullStartDate() {
        LocalDate end = LocalDate.of(2023, 12, 31);
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validateDateRange(null, end)
        );
        assertTrue(exception.getMessage().contains("Start date cannot be null"));
    }
    
    // Price range validation tests
    @Test
    @DisplayName("Valid price range should not throw exception")
    void testValidatePriceRange_ValidRange() {
        assertDoesNotThrow(() -> ValidationUtils.validatePriceRange(10.0, 100.0));
    }
    
    @Test
    @DisplayName("Same min and max price should not throw exception")
    void testValidatePriceRange_SamePrice() {
        assertDoesNotThrow(() -> ValidationUtils.validatePriceRange(50.0, 50.0));
    }
    
    @Test
    @DisplayName("Min price greater than max should throw IllegalArgumentException")
    void testValidatePriceRange_InvalidRange() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validatePriceRange(100.0, 10.0)
        );
        assertTrue(exception.getMessage().contains("Minimum price"));
        assertTrue(exception.getMessage().contains("cannot be greater than maximum price"));
    }
    
    // Date parsing tests
    @Test
    @DisplayName("Valid date string should parse correctly")
    void testValidateAndParseDate_ValidInput() {
        LocalDate result = ValidationUtils.validateAndParseDate("2023-06-15", "testField");
        assertEquals(LocalDate.of(2023, 6, 15), result);
    }
    
    @Test
    @DisplayName("Invalid date string should throw IllegalArgumentException")
    void testValidateAndParseDate_InvalidInput() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validateAndParseDate("invalid-date", "testField")
        );
        assertTrue(exception.getMessage().contains("testField must be in YYYY-MM-DD format"));
    }
    
    @Test
    @DisplayName("Null date string should throw IllegalArgumentException")
    void testValidateAndParseDate_NullInput() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validateAndParseDate(null, "testField")
        );
        assertTrue(exception.getMessage().contains("testField cannot be null"));
    }
    
    // Double parsing tests
    @Test
    @DisplayName("Valid double string should parse correctly")
    void testValidateAndParseDouble_ValidInput() {
        double result = ValidationUtils.validateAndParseDouble("123.45", "testField");
        assertEquals(123.45, result);
    }
    
    @Test
    @DisplayName("Invalid double string should throw IllegalArgumentException")
    void testValidateAndParseDouble_InvalidInput() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validateAndParseDouble("not-a-number", "testField")
        );
        assertTrue(exception.getMessage().contains("testField must be a valid number"));
    }
    
    // Integer parsing tests
    @Test
    @DisplayName("Valid integer string should parse correctly")
    void testValidateAndParseInteger_ValidInput() {
        int result = ValidationUtils.validateAndParseInteger("123", "testField");
        assertEquals(123, result);
    }
    
    @Test
    @DisplayName("Invalid integer string should throw IllegalArgumentException")
    void testValidateAndParseInteger_InvalidInput() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validateAndParseInteger("not-an-integer", "testField")
        );
        assertTrue(exception.getMessage().contains("testField must be a valid integer"));
    }
    
    // Limit validation tests
    @Test
    @DisplayName("Valid limit should return unchanged value")
    void testValidateLimit_ValidInput() {
        int result = ValidationUtils.validateLimit(5, 10);
        assertEquals(5, result);
    }
    
    @Test
    @DisplayName("Limit exceeding maximum should throw IllegalArgumentException")
    void testValidateLimit_ExceedsMaximum() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validateLimit(15, 10)
        );
        assertTrue(exception.getMessage().contains("Limit"));
        assertTrue(exception.getMessage().contains("cannot exceed maximum allowed"));
    }
    
    @Test
    @DisplayName("Zero limit should throw IllegalArgumentException")
    void testValidateLimit_ZeroInput() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validateLimit(0, 10)
        );
        assertTrue(exception.getMessage().contains("Limit must be positive"));
    }
    
    // Percentage validation tests
    @Test
    @DisplayName("Valid percentage should return unchanged value")
    void testValidatePercentage_ValidInputs() {
        assertEquals(0.0, ValidationUtils.validatePercentage(0.0, "testField"));
        assertEquals(50.0, ValidationUtils.validatePercentage(50.0, "testField"));
        assertEquals(100.0, ValidationUtils.validatePercentage(100.0, "testField"));
    }
    
    @Test
    @DisplayName("Negative percentage should throw IllegalArgumentException")
    void testValidatePercentage_NegativeInput() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validatePercentage(-1.0, "testField")
        );
        assertTrue(exception.getMessage().contains("testField must be between 0 and 100"));
    }
    
    @Test
    @DisplayName("Percentage over 100 should throw IllegalArgumentException")
    void testValidatePercentage_OverHundred() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validatePercentage(101.0, "testField")
        );
        assertTrue(exception.getMessage().contains("testField must be between 0 and 100"));
    }
    
    @Test
    @DisplayName("NaN percentage should throw IllegalArgumentException")
    void testValidatePercentage_NaN() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.validatePercentage(Double.NaN, "testField")
        );
        assertTrue(exception.getMessage().contains("testField must be a valid number"));
    }
    
    // Safe error message tests
    @Test
    @DisplayName("Safe error message should be created correctly")
    void testCreateSafeErrorMessage_BasicOperation() {
        String result = ValidationUtils.createSafeErrorMessage("test operation", null);
        assertEquals("Failed to test operation", result);
    }
    
    @Test
    @DisplayName("Safe error message with cause should include cause message")
    void testCreateSafeErrorMessage_WithCause() {
        Exception cause = new RuntimeException("Safe error message");
        String result = ValidationUtils.createSafeErrorMessage("test operation", cause);
        assertEquals("Failed to test operation: Safe error message", result);
    }
    
    @Test
    @DisplayName("Safe error message should filter sensitive information")
    void testCreateSafeErrorMessage_FiltersSensitive() {
        Exception cause = new RuntimeException("Error with password information");
        String result = ValidationUtils.createSafeErrorMessage("test operation", cause);
        assertEquals("Failed to test operation", result); // Should not include sensitive info
    }
    
    @Test
    @DisplayName("Safe error message should filter token information")
    void testCreateSafeErrorMessage_FiltersToken() {
        Exception cause = new RuntimeException("Token expired");
        String result = ValidationUtils.createSafeErrorMessage("test operation", cause);
        assertEquals("Failed to test operation", result); // Should not include sensitive info
    }
    
    @Test
    @DisplayName("Safe error message should filter key information")
    void testCreateSafeErrorMessage_FiltersKey() {
        Exception cause = new RuntimeException("Invalid key provided");
        String result = ValidationUtils.createSafeErrorMessage("test operation", cause);
        assertEquals("Failed to test operation", result); // Should not include sensitive info
    }
    
    @Test
    @DisplayName("Safe error message should filter secret information")
    void testCreateSafeErrorMessage_FiltersSecret() {
        Exception cause = new RuntimeException("Secret not found");
        String result = ValidationUtils.createSafeErrorMessage("test operation", cause);
        assertEquals("Failed to test operation", result); // Should not include sensitive info
    }
    
    @Test
    @DisplayName("Edge case: Very small positive numbers should be accepted")
    void testEdgeCases_SmallPositiveNumbers() {
        assertDoesNotThrow(() -> ValidationUtils.validatePositiveDouble(Double.MIN_VALUE, "testField"));
        assertDoesNotThrow(() -> ValidationUtils.validatePositiveInteger(1, "testField"));
    }
    
    @Test
    @DisplayName("Edge case: Large numbers should be accepted")
    void testEdgeCases_LargeNumbers() {
        assertDoesNotThrow(() -> ValidationUtils.validatePositiveDouble(Double.MAX_VALUE, "testField"));
        assertDoesNotThrow(() -> ValidationUtils.validatePositiveInteger(Integer.MAX_VALUE, "testField"));
    }
    
    @Test
    @DisplayName("Edge case: Boundary date values should work")
    void testEdgeCases_BoundaryDates() {
        LocalDate minDate = LocalDate.MIN;
        LocalDate maxDate = LocalDate.MAX;
        assertDoesNotThrow(() -> ValidationUtils.validateDateRange(minDate, maxDate));
    }
}