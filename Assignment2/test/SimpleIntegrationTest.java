import com.ecommerce.model.SalesRecord;
import com.ecommerce.repository.InMemorySalesRepository;
import com.ecommerce.service.SalesAnalysisService;
import com.ecommerce.service.CsvDataLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * JUnit 5 integration test demonstrating TDD principles.
 * Tests core functionality without external dependencies.
 */
@DisplayName("Integration Tests for E-Commerce Analysis")
public class SimpleIntegrationTest {
    
    @Test
    @DisplayName("CSV data loading should load all records correctly")
    void testCsvLoading() throws Exception {
        List<SalesRecord> records = CsvDataLoader.loadSalesData("sales_data.csv");
        
        assertNotNull(records, "Records should not be null");
        assertEquals(120, records.size(), "Should load 120 records");
        assertFalse(records.isEmpty(), "Records should not be empty");
        
        // Test first record
        SalesRecord first = records.get(0);
        assertEquals("ORD001", first.getOrderId(), "First order ID should be ORD001");
        assertTrue(first.getTotalRevenue() > 0, "Revenue should be positive");
    }
    
    @Test
    @DisplayName("SalesRecord validation should work correctly")
    void testSalesRecordValidation() {
        // Test valid creation
        SalesRecord valid = new SalesRecord(
            "TEST001", "Test Product", "Electronics", 
            99.99, 2, LocalDate.of(2023, 1, 15), "North America"
        );
        assertEquals(199.98, valid.getTotalRevenue(), "Revenue calculation should be correct");
        
        // Test validation failures
        assertThrows(IllegalArgumentException.class, () ->
            new SalesRecord(null, "Product", "Category", 99.99, 1, LocalDate.now(), "Region"),
            "Should reject null order ID"
        );
        
        assertThrows(IllegalArgumentException.class, () ->
            new SalesRecord("ID", "Product", "Category", -1.0, 1, LocalDate.now(), "Region"),
            "Should reject negative price"
        );
    }
    
    @Test
    @DisplayName("Repository operations should work correctly")
    void testRepositoryOperations() {
        InMemorySalesRepository repo = new InMemorySalesRepository();
        
        // Test empty repository
        assertEquals(0, repo.count(), "Empty repository should have 0 records");
        assertTrue(repo.findAll().isEmpty(), "findAll should return empty list");
        
        // Test adding records
        SalesRecord record = new SalesRecord(
            "TEST001", "Test Product", "Electronics",
            99.99, 1, LocalDate.of(2023, 1, 15), "North America"
        );
        
        repo.save(record);
        assertEquals(1, repo.count(), "Repository should have 1 record");
        assertTrue(repo.existsByOrderId("TEST001"), "Record should exist");
    }
    
    @Test
    @DisplayName("Analysis service should provide correct results")
    void testAnalysisService() {
        // Create test data
        List<SalesRecord> testData = List.of(
            new SalesRecord("ORD001", "Laptop", "Electronics", 999.99, 1, LocalDate.of(2023, 1, 15), "North America"),
            new SalesRecord("ORD002", "Shirt", "Apparel", 29.99, 2, LocalDate.of(2023, 2, 20), "Europe"),
            new SalesRecord("ORD003", "Phone", "Electronics", 699.99, 1, LocalDate.of(2023, 3, 10), "Asia")
        );
        
        InMemorySalesRepository repo = new InMemorySalesRepository(testData);
        SalesAnalysisService service = new SalesAnalysisService(repo);
        
        // Test summary
        SalesAnalysisService.SalesSummary summary = service.getOverallSummary();
        assertEquals(3, summary.getTotalOrders(), "Should have 3 orders");
        assertEquals(2, summary.getUniqueCategories(), "Should have 2 categories");
        
        // Test revenue by category
        Map<String, Double> categoryRevenue = service.getRevenueByCategory();
        assertTrue(categoryRevenue.containsKey("Electronics"), "Should contain Electronics");
        assertTrue(categoryRevenue.containsKey("Apparel"), "Should contain Apparel");
    }
    
    @Test
    @DisplayName("Stream operations should work correctly")
    void testStreamOperations() {
        List<SalesRecord> records = List.of(
            new SalesRecord("ORD001", "Product A", "Category1", 100.0, 1, LocalDate.of(2023, 1, 15), "North America"),
            new SalesRecord("ORD002", "Product B", "Category1", 50.0, 2, LocalDate.of(2023, 2, 20), "Europe"),
            new SalesRecord("ORD003", "Product C", "Category2", 200.0, 1, LocalDate.of(2023, 3, 10), "Asia")
        );
        
        InMemorySalesRepository repo = new InMemorySalesRepository(records);
        SalesAnalysisService service = new SalesAnalysisService(repo);
        
        // Test grouping operation
        Map<String, Double> categoryRevenue = service.getRevenueByCategory();
        double category1Revenue = categoryRevenue.get("Category1");
        assertEquals(200.0, category1Revenue, "Category1 revenue should be 200.0");
        
        // Test filtering operation
        List<SalesRecord> highValue = service.getHighValueOrders(150.0);
        assertEquals(1, highValue.size(), "Should find 1 high value order");
        assertEquals("Product C", highValue.get(0).getProductName(), "Should be Product C");
    }
    
    @Test
    @DisplayName("Edge cases should be handled gracefully")
    void testEdgeCases() {
        // Test empty repository
        InMemorySalesRepository emptyRepo = new InMemorySalesRepository();
        SalesAnalysisService emptyService = new SalesAnalysisService(emptyRepo);
        
        SalesAnalysisService.SalesSummary emptySummary = emptyService.getOverallSummary();
        assertEquals(0, emptySummary.getTotalOrders(), "Empty repo should have 0 orders");
        
        Map<String, Double> emptyCategories = emptyService.getRevenueByCategory();
        assertTrue(emptyCategories.isEmpty(), "Empty repo should have no categories");
        
        // Test null validation
        assertThrows(IllegalArgumentException.class, () ->
            new SalesAnalysisService(null),
            "Should reject null repository"
        );
    }
}