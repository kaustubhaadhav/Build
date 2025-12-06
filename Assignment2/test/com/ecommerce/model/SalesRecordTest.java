package com.ecommerce.model;

import com.ecommerce.model.SalesRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

/**
 * Comprehensive JUnit 5 unit tests for SalesRecord model class.
 * Tests validation, creation, calculation methods, and edge cases.
 */
@DisplayName("SalesRecord Unit Tests")
public class SalesRecordTest {
    
    @Test
    @DisplayName("Valid construction should create SalesRecord correctly")
    void testValidConstruction() {
        SalesRecord record = new SalesRecord(
            "ORD001", "Test Product", "Electronics", 
            99.99, 2, LocalDate.of(2023, 6, 15), "North America"
        );
        
        assertEquals("ORD001", record.getOrderId());
        assertEquals("Test Product", record.getProductName());
        assertEquals("Electronics", record.getCategory());
        assertEquals(99.99, record.getPrice());
        assertEquals(2, record.getQuantity());
        assertEquals(LocalDate.of(2023, 6, 15), record.getOrderDate());
        assertEquals("North America", record.getCustomerRegion());
    }
    
    @Test
    @DisplayName("String parameter construction should parse values correctly")
    void testStringParameterConstruction() {
        SalesRecord record = new SalesRecord(
            "ORD002", "Test Product", "Electronics",
            "99.99", "2", "2023-06-15", "North America"
        );
        
        assertEquals(99.99, record.getPrice());
        assertEquals(2, record.getQuantity());
        assertEquals(LocalDate.of(2023, 6, 15), record.getOrderDate());
    }
    
    @Test
    @DisplayName("Null order ID should throw IllegalArgumentException")
    void testNullOrderIdValidation() {
        assertThrows(IllegalArgumentException.class, () ->
            new SalesRecord(null, "Product", "Category", 10.0, 1, LocalDate.now(), "Region")
        );
    }
    
    @Test
    @DisplayName("Empty order ID should throw IllegalArgumentException")
    void testEmptyOrderIdValidation() {
        assertThrows(IllegalArgumentException.class, () ->
            new SalesRecord("", "Product", "Category", 10.0, 1, LocalDate.now(), "Region")
        );
    }
    
    @Test
    @DisplayName("Null product name should throw IllegalArgumentException")
    void testNullProductNameValidation() {
        assertThrows(IllegalArgumentException.class, () ->
            new SalesRecord("ORD001", null, "Category", 10.0, 1, LocalDate.now(), "Region")
        );
    }
    
    @Test
    @DisplayName("Empty product name should throw IllegalArgumentException")
    void testEmptyProductNameValidation() {
        assertThrows(IllegalArgumentException.class, () ->
            new SalesRecord("ORD001", "", "Category", 10.0, 1, LocalDate.now(), "Region")
        );
    }
    
    @Test
    @DisplayName("Null category should throw IllegalArgumentException")
    void testNullCategoryValidation() {
        assertThrows(IllegalArgumentException.class, () ->
            new SalesRecord("ORD001", "Product", null, 10.0, 1, LocalDate.now(), "Region")
        );
    }
    
    @Test
    @DisplayName("Empty category should throw IllegalArgumentException")
    void testEmptyCategoryValidation() {
        assertThrows(IllegalArgumentException.class, () ->
            new SalesRecord("ORD001", "Product", "", 10.0, 1, LocalDate.now(), "Region")
        );
    }
    
    @Test
    @DisplayName("Negative price should throw IllegalArgumentException")
    void testNegativePriceValidation() {
        assertThrows(IllegalArgumentException.class, () ->
            new SalesRecord("ORD001", "Product", "Category", -10.0, 1, LocalDate.now(), "Region")
        );
    }
    
    @Test
    @DisplayName("Zero price should throw IllegalArgumentException")
    void testZeroPriceValidation() {
        assertThrows(IllegalArgumentException.class, () ->
            new SalesRecord("ORD001", "Product", "Category", 0.0, 1, LocalDate.now(), "Region")
        );
    }
    
    @Test
    @DisplayName("NaN price should throw IllegalArgumentException")
    void testInvalidPriceValidation() {
        assertThrows(IllegalArgumentException.class, () ->
            new SalesRecord("ORD001", "Product", "Category", Double.NaN, 1, LocalDate.now(), "Region")
        );
    }
    
    @Test
    @DisplayName("Negative quantity should throw IllegalArgumentException")
    void testNegativeQuantityValidation() {
        assertThrows(IllegalArgumentException.class, () ->
            new SalesRecord("ORD001", "Product", "Category", 10.0, -1, LocalDate.now(), "Region")
        );
    }
    
    @Test
    @DisplayName("Zero quantity should throw IllegalArgumentException")
    void testZeroQuantityValidation() {
        assertThrows(IllegalArgumentException.class, () ->
            new SalesRecord("ORD001", "Product", "Category", 10.0, 0, LocalDate.now(), "Region")
        );
    }
    
    @Test
    @DisplayName("Null date should throw IllegalArgumentException")
    void testNullDateValidation() {
        assertThrows(IllegalArgumentException.class, () ->
            new SalesRecord("ORD001", "Product", "Category", 10.0, 1, null, "Region")
        );
    }
    
    @Test
    @DisplayName("Null region should throw IllegalArgumentException")
    void testNullRegionValidation() {
        assertThrows(IllegalArgumentException.class, () ->
            new SalesRecord("ORD001", "Product", "Category", 10.0, 1, LocalDate.now(), null)
        );
    }
    
    @Test
    @DisplayName("Empty region should throw IllegalArgumentException")
    void testEmptyRegionValidation() {
        assertThrows(IllegalArgumentException.class, () ->
            new SalesRecord("ORD001", "Product", "Category", 10.0, 1, LocalDate.now(), "")
        );
    }
    
    @Test
    @DisplayName("Invalid price string should throw IllegalArgumentException")
    void testInvalidPriceStringValidation() {
        assertThrows(IllegalArgumentException.class, () ->
            new SalesRecord("ORD001", "Product", "Category", "invalid", "1", "2023-01-01", "Region")
        );
    }
    
    @Test
    @DisplayName("Invalid quantity string should throw IllegalArgumentException")
    void testInvalidQuantityStringValidation() {
        assertThrows(IllegalArgumentException.class, () ->
            new SalesRecord("ORD001", "Product", "Category", "10.0", "invalid", "2023-01-01", "Region")
        );
    }
    
    @Test
    @DisplayName("Invalid date string should throw IllegalArgumentException")
    void testInvalidDateStringValidation() {
        assertThrows(IllegalArgumentException.class, () ->
            new SalesRecord("ORD001", "Product", "Category", "10.0", "1", "invalid-date", "Region")
        );
    }
    
    @Test
    @DisplayName("Total revenue calculation should multiply price by quantity")
    void testTotalRevenueCalculation() {
        SalesRecord record = new SalesRecord(
            "ORD001", "Product", "Category", 25.50, 3, LocalDate.now(), "Region"
        );
        
        double expectedRevenue = 25.50 * 3;
        assertEquals(expectedRevenue, record.getTotalRevenue());
    }
    
    @Test
    @DisplayName("Date extraction methods should return correct values")
    void testDateExtraction() {
        SalesRecord record = new SalesRecord(
            "ORD001", "Product", "Category", 10.0, 1, 
            LocalDate.of(2023, 7, 15), "Region"
        );
        
        assertEquals(2023, record.getOrderYear());
        assertEquals(7, record.getOrderMonth());
    }
    
    @Test
    @DisplayName("Quarter calculation should return correct quarters")
    void testQuarterCalculation() {
        // Test Q1 (January)
        SalesRecord q1 = new SalesRecord("ORD001", "Product", "Category", 10.0, 1, 
            LocalDate.of(2023, 1, 15), "Region");
        assertEquals(1, q1.getOrderQuarter());
        
        // Test Q2 (April)
        SalesRecord q2 = new SalesRecord("ORD002", "Product", "Category", 10.0, 1, 
            LocalDate.of(2023, 4, 15), "Region");
        assertEquals(2, q2.getOrderQuarter());
        
        // Test Q3 (July)
        SalesRecord q3 = new SalesRecord("ORD003", "Product", "Category", 10.0, 1, 
            LocalDate.of(2023, 7, 15), "Region");
        assertEquals(3, q3.getOrderQuarter());
        
        // Test Q4 (October)
        SalesRecord q4 = new SalesRecord("ORD004", "Product", "Category", 10.0, 1, 
            LocalDate.of(2023, 10, 15), "Region");
        assertEquals(4, q4.getOrderQuarter());
    }
    
    @Test
    @DisplayName("Equals method should correctly compare SalesRecord objects")
    void testEquals() {
        SalesRecord record1 = new SalesRecord(
            "ORD001", "Product", "Category", 10.0, 1, LocalDate.of(2023, 1, 15), "Region"
        );
        SalesRecord record2 = new SalesRecord(
            "ORD001", "Product", "Category", 10.0, 1, LocalDate.of(2023, 1, 15), "Region"
        );
        SalesRecord record3 = new SalesRecord(
            "ORD002", "Product", "Category", 10.0, 1, LocalDate.of(2023, 1, 15), "Region"
        );
        
        assertEquals(record1, record2);
        assertNotEquals(record1, record3);
        assertNotEquals(record1, null);
        assertNotEquals(record1, "string");
    }
    
    @Test
    @DisplayName("HashCode method should return consistent values for equal objects")
    void testHashCode() {
        SalesRecord record1 = new SalesRecord(
            "ORD001", "Product", "Category", 10.0, 1, LocalDate.of(2023, 1, 15), "Region"
        );
        SalesRecord record2 = new SalesRecord(
            "ORD001", "Product", "Category", 10.0, 1, LocalDate.of(2023, 1, 15), "Region"
        );
        
        assertEquals(record1.hashCode(), record2.hashCode());
    }
    
    @Test
    @DisplayName("ToString method should contain key information")
    void testToString() {
        SalesRecord record = new SalesRecord(
            "ORD001", "Product", "Category", 10.0, 2, LocalDate.of(2023, 1, 15), "Region"
        );
        
        String toString = record.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("ORD001"));
        assertTrue(toString.contains("Product"));
        assertTrue(toString.contains("20.00")); // Total revenue
    }
    
    @Test
    @DisplayName("Boundary values should be handled correctly")
    void testBoundaryValues() {
        // Test very small positive price
        SalesRecord smallPrice = new SalesRecord(
            "ORD001", "Product", "Category", 0.01, 1, LocalDate.now(), "Region"
        );
        assertEquals(0.01, smallPrice.getTotalRevenue());
        
        // Test large quantities
        SalesRecord largeQuantity = new SalesRecord(
            "ORD002", "Product", "Category", 10.0, Integer.MAX_VALUE, LocalDate.now(), "Region"
        );
        double expectedRevenue = 10.0 * Integer.MAX_VALUE;
        assertEquals(expectedRevenue, largeQuantity.getTotalRevenue());
    }
    
    @Test
    @DisplayName("Whitespace should be trimmed from string fields")
    void testWhitespaceHandling() {
        SalesRecord record = new SalesRecord(
            " ORD001 ", " Product Name ", " Category ", 
            10.0, 1, LocalDate.now(), " Region "
        );
        
        assertEquals("ORD001", record.getOrderId());
        assertEquals("Product Name", record.getProductName());
        assertEquals("Category", record.getCategory());
        assertEquals("Region", record.getCustomerRegion());
    }
}