package com.ecommerce.service;

import com.ecommerce.model.SalesRecord;
import com.ecommerce.service.CsvDataLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/**
 * Comprehensive JUnit 5 unit tests for CsvDataLoader.
 * Tests valid CSV loading, error handling, and edge cases.
 */
@DisplayName("CsvDataLoader Unit Tests")
public class CsvDataLoaderTest {
    
    @TempDir
    Path tempDir;
    
    private Path validCsvFile;
    private Path invalidPathCsv;
    private Path malformedCsv;
    private Path emptyCsv;
    private Path headerOnlyCsv;
    private Path invalidHeadersCsv;
    
    @BeforeEach
    void setUp() throws IOException {
        // Create valid CSV file
        validCsvFile = tempDir.resolve("valid.csv");
        Files.write(validCsvFile, List.of(
            "orderId,productName,category,price,quantity,orderDate,customerRegion",
            "ORD001,Wireless Headphones,Electronics,99.99,2,2023-01-15,North America",
            "ORD002,Running Shoes,Apparel,129.99,1,2023-02-20,Europe"
        ));
        
        // Create malformed CSV file
        malformedCsv = tempDir.resolve("malformed.csv");
        Files.write(malformedCsv, List.of(
            "orderId,productName,category,price,quantity,orderDate,customerRegion",
            "ORD001,Wireless Headphones,Electronics,99.99,2,2023-01-15,North America",
            "ORD002,Running Shoes,Apparel,invalid-price,1,2023-02-20,Europe",
            "ORD003,Phone,Electronics,599.99,invalid-quantity,2023-03-10,Asia",
            "ORD004,Tablet,Electronics,399.99,1,invalid-date,North America"
        ));
        
        // Create empty CSV file
        emptyCsv = tempDir.resolve("empty.csv");
        Files.write(emptyCsv, List.of());
        
        // Create header-only CSV file
        headerOnlyCsv = tempDir.resolve("header-only.csv");
        Files.write(headerOnlyCsv, List.of(
            "orderId,productName,category,price,quantity,orderDate,customerRegion"
        ));
        
        // Create CSV with invalid headers
        invalidHeadersCsv = tempDir.resolve("invalid-headers.csv");
        Files.write(invalidHeadersCsv, List.of(
            "id,name,cat,price,qty,date,region",
            "ORD001,Product,Category,10.0,1,2023-01-01,Region"
        ));
    }
    
    @Test
    @DisplayName("Valid CSV should load correct count and data")
    void testValidCsvLoading() throws Exception {
        List<SalesRecord> records = CsvDataLoader.loadSalesData(validCsvFile.toString());
        
        assertNotNull(records);
        assertEquals(2, records.size());
        
        // Test first record
        SalesRecord first = records.get(0);
        assertEquals("ORD001", first.getOrderId());
        assertEquals("Wireless Headphones", first.getProductName());
        assertEquals("Electronics", first.getCategory());
        assertEquals(99.99, first.getPrice());
        assertEquals(2, first.getQuantity());
        assertEquals(LocalDate.of(2023, 1, 15), first.getOrderDate());
        assertEquals("North America", first.getCustomerRegion());
        
        // Test second record
        SalesRecord second = records.get(1);
        assertEquals("ORD002", second.getOrderId());
        assertEquals("Running Shoes", second.getProductName());
        assertEquals("Apparel", second.getCategory());
        assertEquals(129.99, second.getPrice());
        assertEquals(1, second.getQuantity());
        assertEquals(LocalDate.of(2023, 2, 20), second.getOrderDate());
        assertEquals("Europe", second.getCustomerRegion());
    }
    
    @Test
    @DisplayName("Invalid file path should throw CsvLoadingException")
    void testInvalidPath() {
        String nonExistentPath = tempDir.resolve("missing.csv").toString();
        
        CsvDataLoader.CsvLoadingException exception = assertThrows(
            CsvDataLoader.CsvLoadingException.class,
            () -> CsvDataLoader.loadSalesData(nonExistentPath)
        );
        
        assertTrue(exception.getMessage().contains("CSV file not found"));
    }
    
    @Test
    @DisplayName("Null file path should throw CsvLoadingException")
    void testNullPath() {
        CsvDataLoader.CsvLoadingException exception = assertThrows(
            CsvDataLoader.CsvLoadingException.class,
            () -> CsvDataLoader.loadSalesData(null)
        );
        
        assertTrue(exception.getMessage().contains("File path cannot be null or empty"));
    }
    
    @Test
    @DisplayName("Empty file path should throw CsvLoadingException")
    void testEmptyPath() {
        CsvDataLoader.CsvLoadingException exception = assertThrows(
            CsvDataLoader.CsvLoadingException.class,
            () -> CsvDataLoader.loadSalesData("")
        );
        
        assertTrue(exception.getMessage().contains("File path cannot be null or empty"));
    }
    
    @Test
    @DisplayName("Malformed lines should cause parsing failure")
    void testMalformedLines() {
        CsvDataLoader.CsvLoadingException exception = assertThrows(
            CsvDataLoader.CsvLoadingException.class,
            () -> CsvDataLoader.loadSalesData(malformedCsv.toString())
        );
        
        assertTrue(exception.getMessage().contains("CSV parsing failed with errors"));
        // Should mention specific line errors
        assertTrue(exception.getMessage().contains("Line"));
    }
    
    @Test
    @DisplayName("Empty file should throw CsvLoadingException")
    void testEmptyFile() {
        assertThrows(
            CsvDataLoader.CsvLoadingException.class,
            () -> CsvDataLoader.loadSalesData(emptyCsv.toString())
        );
    }
    
    @Test
    @DisplayName("Header-only file should throw CsvLoadingException")
    void testHeaderOnlyFile() {
        CsvDataLoader.CsvLoadingException exception = assertThrows(
            CsvDataLoader.CsvLoadingException.class,
            () -> CsvDataLoader.loadSalesData(headerOnlyCsv.toString())
        );
        
        assertTrue(exception.getMessage().contains("No valid sales records found"));
    }
    
    @Test
    @DisplayName("Invalid headers should throw CsvLoadingException")
    void testInvalidHeaders() {
        CsvDataLoader.CsvLoadingException exception = assertThrows(
            CsvDataLoader.CsvLoadingException.class,
            () -> CsvDataLoader.loadSalesData(invalidHeadersCsv.toString())
        );
        
        assertTrue(exception.getMessage().contains("CSV parsing failed with errors"));
        assertTrue(exception.getMessage().contains("Invalid header"));
    }
    
    @Test
    @DisplayName("CSV file info should return correct metadata")
    void testGetCsvFileInfo() throws Exception {
        CsvDataLoader.CsvFileInfo info = CsvDataLoader.getCsvFileInfo(validCsvFile.toString());
        
        assertNotNull(info);
        assertEquals(validCsvFile.toString(), info.getFilePath());
        assertEquals(3, info.getTotalLines()); // Header + 2 data lines
        assertEquals(2, info.getDataLines());   // 2 data lines
    }
    
    @Test
    @DisplayName("CSV file info with invalid path should throw exception")
    void testGetCsvFileInfoInvalidPath() {
        String nonExistentPath = tempDir.resolve("missing.csv").toString();
        
        assertThrows(CsvDataLoader.CsvLoadingException.class,
            () -> CsvDataLoader.getCsvFileInfo(nonExistentPath)
        );
    }
    
    @Test
    @DisplayName("Default CSV loading should work when file exists")
    void testDefaultCsvLoading() {
        // This test will only pass if sales_data.csv exists in the project root
        // We'll make it conditional
        try {
            List<SalesRecord> records = CsvDataLoader.loadSalesData();
            assertNotNull(records);
            assertFalse(records.isEmpty());
        } catch (CsvDataLoader.CsvLoadingException e) {
            // Expected if default file doesn't exist
            assertTrue(e.getMessage().contains("CSV file not found"));
        }
    }
    
    @Test
    @DisplayName("loadSalesDataWithStreams should work correctly")
    void testLoadSalesDataWithStreams() throws Exception {
        List<SalesRecord> records = CsvDataLoader.loadSalesDataWithStreams(validCsvFile.toString());
        
        assertNotNull(records);
        assertEquals(2, records.size());
        
        // Verify the data is loaded correctly
        SalesRecord first = records.get(0);
        assertEquals("ORD001", first.getOrderId());
        assertEquals("Wireless Headphones", first.getProductName());
    }
    
    @Test
    @DisplayName("loadSalesDataWithStreams should handle invalid path")
    void testLoadSalesDataWithStreamsInvalidPath() {
        String nonExistentPath = tempDir.resolve("missing.csv").toString();
        
        assertThrows(CsvDataLoader.CsvLoadingException.class,
            () -> CsvDataLoader.loadSalesDataWithStreams(nonExistentPath)
        );
    }
    
    @Test
    @DisplayName("Whitespace in CSV fields should be trimmed")
    void testWhitespaceHandling() throws Exception {
        Path whitespaceCsv = tempDir.resolve("whitespace.csv");
        Files.write(whitespaceCsv, List.of(
            "orderId,productName,category,price,quantity,orderDate,customerRegion",
            " ORD001 , Product Name , Electronics ,99.99,1,2023-01-01, North America "
        ));
        
        List<SalesRecord> records = CsvDataLoader.loadSalesData(whitespaceCsv.toString());
        
        assertEquals(1, records.size());
        SalesRecord record = records.get(0);
        assertEquals("ORD001", record.getOrderId());
        assertEquals("Product Name", record.getProductName());
        assertEquals("Electronics", record.getCategory());
        assertEquals("North America", record.getCustomerRegion());
    }
    
    @Test
    @DisplayName("CSV with wrong column count should fail")
    void testWrongColumnCount() throws Exception {
        Path wrongColumnsCsv = tempDir.resolve("wrong-columns.csv");
        Files.write(wrongColumnsCsv, List.of(
            "orderId,productName,category,price,quantity,orderDate,customerRegion",
            "ORD001,Product,Electronics,99.99,1" // Missing columns
        ));
        
        CsvDataLoader.CsvLoadingException exception = assertThrows(
            CsvDataLoader.CsvLoadingException.class,
            () -> CsvDataLoader.loadSalesData(wrongColumnsCsv.toString())
        );
        
        assertTrue(exception.getMessage().contains("CSV parsing failed with errors"));
        assertTrue(exception.getMessage().contains("Expected 7 fields but found"));
    }
    
    @Test
    @DisplayName("Empty lines in CSV should be skipped")
    void testEmptyLinesSkipped() throws Exception {
        Path emptyLinesCsv = tempDir.resolve("empty-lines.csv");
        Files.write(emptyLinesCsv, List.of(
            "orderId,productName,category,price,quantity,orderDate,customerRegion",
            "ORD001,Product1,Electronics,99.99,1,2023-01-01,North America",
            "",  // Empty line
            "   ", // Whitespace-only line
            "ORD002,Product2,Apparel,49.99,2,2023-02-01,Europe"
        ));
        
        List<SalesRecord> records = CsvDataLoader.loadSalesData(emptyLinesCsv.toString());
        
        assertEquals(2, records.size());
        assertEquals("ORD001", records.get(0).getOrderId());
        assertEquals("ORD002", records.get(1).getOrderId());
    }
}