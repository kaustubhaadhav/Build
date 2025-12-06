package com.ecommerce.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;

/**
 * Utility class providing comprehensive validation methods for input data.
 * Handles null values, empty collections, and various edge cases.
 */
public final class ValidationUtils {
    
    private ValidationUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Validates that a string is not null or empty.
     * 
     * @param value The string to validate
     * @param fieldName Name of the field for error messages
     * @return Trimmed string value
     * @throws IllegalArgumentException if validation fails
     */
    public static String validateString(String value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
        
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        
        return trimmed;
    }
    
    /**
     * Validates that a number is positive.
     * 
     * @param value The number to validate
     * @param fieldName Name of the field for error messages
     * @return The validated value
     * @throws IllegalArgumentException if validation fails
     */
    public static double validatePositiveDouble(double value, String fieldName) {
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException(fieldName + " cannot be NaN");
        }
        
        if (Double.isInfinite(value)) {
            throw new IllegalArgumentException(fieldName + " cannot be infinite");
        }
        
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive, got: " + value);
        }
        
        return value;
    }
    
    /**
     * Validates that an integer is positive.
     * 
     * @param value The integer to validate
     * @param fieldName Name of the field for error messages
     * @return The validated value
     * @throws IllegalArgumentException if validation fails
     */
    public static int validatePositiveInteger(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive, got: " + value);
        }
        
        return value;
    }
    
    /**
     * Validates that a number is non-negative.
     * 
     * @param value The number to validate
     * @param fieldName Name of the field for error messages
     * @return The validated value
     * @throws IllegalArgumentException if validation fails
     */
    public static double validateNonNegativeDouble(double value, String fieldName) {
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException(fieldName + " cannot be NaN");
        }
        
        if (Double.isInfinite(value)) {
            throw new IllegalArgumentException(fieldName + " cannot be infinite");
        }
        
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative, got: " + value);
        }
        
        return value;
    }
    
    /**
     * Validates that an integer is non-negative.
     * 
     * @param value The integer to validate
     * @param fieldName Name of the field for error messages
     * @return The validated value
     * @throws IllegalArgumentException if validation fails
     */
    public static int validateNonNegativeInteger(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative, got: " + value);
        }
        
        return value;
    }
    
    /**
     * Validates that an object is not null.
     * 
     * @param <T> Type of the object
     * @param value The object to validate
     * @param fieldName Name of the field for error messages
     * @return The validated object
     * @throws IllegalArgumentException if object is null
     */
    public static <T> T validateNotNull(T value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
        
        return value;
    }
    
    /**
     * Validates that a collection is not null or empty.
     * 
     * @param <T> Type of collection elements
     * @param collection The collection to validate
     * @param fieldName Name of the field for error messages
     * @return The validated collection
     * @throws IllegalArgumentException if collection is null or empty
     */
    public static <T> Collection<T> validateNotEmpty(Collection<T> collection, String fieldName) {
        validateNotNull(collection, fieldName);
        
        if (collection.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        
        return collection;
    }
    
    /**
     * Validates a date range ensuring start is not after end.
     * 
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @throws IllegalArgumentException if dates are null or invalid range
     */
    public static void validateDateRange(LocalDate startDate, LocalDate endDate) {
        validateNotNull(startDate, "Start date");
        validateNotNull(endDate, "End date");
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                "Start date (" + startDate + ") cannot be after end date (" + endDate + ")");
        }
    }
    
    /**
     * Validates a price range ensuring min is not greater than max.
     * 
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @throws IllegalArgumentException if prices are negative or invalid range
     */
    public static void validatePriceRange(double minPrice, double maxPrice) {
        validateNonNegativeDouble(minPrice, "Minimum price");
        validateNonNegativeDouble(maxPrice, "Maximum price");
        
        if (minPrice > maxPrice) {
            throw new IllegalArgumentException(
                "Minimum price (" + minPrice + ") cannot be greater than maximum price (" + maxPrice + ")");
        }
    }
    
    /**
     * Validates and parses a date string.
     * 
     * @param dateString Date string to parse
     * @param fieldName Name of the field for error messages
     * @return Parsed LocalDate
     * @throws IllegalArgumentException if parsing fails
     */
    public static LocalDate validateAndParseDate(String dateString, String fieldName) {
        validateString(dateString, fieldName);
        
        try {
            return LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                fieldName + " must be in YYYY-MM-DD format, got: " + dateString, e);
        }
    }
    
    /**
     * Validates and parses a double from string.
     * 
     * @param doubleString String to parse
     * @param fieldName Name of the field for error messages
     * @return Parsed double value
     * @throws IllegalArgumentException if parsing fails
     */
    public static double validateAndParseDouble(String doubleString, String fieldName) {
        validateString(doubleString, fieldName);
        
        try {
            return Double.parseDouble(doubleString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                fieldName + " must be a valid number, got: " + doubleString, e);
        }
    }
    
    /**
     * Validates and parses an integer from string.
     * 
     * @param integerString String to parse
     * @param fieldName Name of the field for error messages
     * @return Parsed integer value
     * @throws IllegalArgumentException if parsing fails
     */
    public static int validateAndParseInteger(String integerString, String fieldName) {
        validateString(integerString, fieldName);
        
        try {
            return Integer.parseInt(integerString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                fieldName + " must be a valid integer, got: " + integerString, e);
        }
    }
    
    /**
     * Validates that a limit parameter is within reasonable bounds.
     * 
     * @param limit The limit value to validate
     * @param maxAllowed Maximum allowed limit
     * @return The validated limit
     * @throws IllegalArgumentException if limit is invalid
     */
    public static int validateLimit(int limit, int maxAllowed) {
        validatePositiveInteger(limit, "Limit");
        
        if (limit > maxAllowed) {
            throw new IllegalArgumentException(
                "Limit (" + limit + ") cannot exceed maximum allowed (" + maxAllowed + ")");
        }
        
        return limit;
    }
    
    /**
     * Validates a percentage value (0-100).
     * 
     * @param percentage The percentage to validate
     * @param fieldName Name of the field for error messages
     * @return The validated percentage
     * @throws IllegalArgumentException if percentage is not between 0 and 100
     */
    public static double validatePercentage(double percentage, String fieldName) {
        if (Double.isNaN(percentage) || Double.isInfinite(percentage)) {
            throw new IllegalArgumentException(fieldName + " must be a valid number");
        }
        
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException(
                fieldName + " must be between 0 and 100, got: " + percentage);
        }
        
        return percentage;
    }
    
    /**
     * Creates a safe error message that doesn't expose sensitive information.
     * 
     * @param operation The operation that failed
     * @param cause The underlying cause (can be null)
     * @return Sanitized error message
     */
    public static String createSafeErrorMessage(String operation, Throwable cause) {
        StringBuilder message = new StringBuilder();
        message.append("Failed to ").append(operation);
        
        if (cause != null) {
            String causeMessage = cause.getMessage();
            if (causeMessage != null && !causeMessage.isEmpty()) {
                // Only include the cause message if it doesn't contain sensitive info
                if (!containsSensitiveInfo(causeMessage)) {
                    message.append(": ").append(causeMessage);
                }
            }
        }
        
        return message.toString();
    }
    
    /**
     * Checks if a message might contain sensitive information.
     * 
     * @param message The message to check
     * @return true if message might contain sensitive info
     */
    private static boolean containsSensitiveInfo(String message) {
        String lowerMessage = message.toLowerCase();
        return lowerMessage.contains("password") || 
               lowerMessage.contains("token") || 
               lowerMessage.contains("key") || 
               lowerMessage.contains("secret");
    }
}