package com.ecommerce.service;

import com.ecommerce.model.SalesRecord;
import com.ecommerce.repository.InMemorySalesRepository;
import com.ecommerce.repository.SalesRepository;
import com.ecommerce.service.SalesAnalysisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Comprehensive JUnit 5 unit tests for SalesAnalysisService.
 * Tests all analysis methods with various scenarios and edge cases.
 */
@DisplayName("SalesAnalysisService Unit Tests")
public class SalesAnalysisServiceTest {
    
    private SalesRepository testRepository;
    private SalesAnalysisService analysisService;
    
    @BeforeEach
    void setUp() {
        testRepository = new InMemorySalesRepository();
        analysisService = new SalesAnalysisService(testRepository);
    }
    
    @Test
    @DisplayName("Constructor should reject null repository")
    void testConstructorValidation() {
        assertThrows(IllegalArgumentException.class, () ->
            new SalesAnalysisService(null)
        );
        
        // Test valid construction
        SalesRepository repo = new InMemorySalesRepository();
        SalesAnalysisService service = new SalesAnalysisService(repo);
        assertNotNull(service);
    }
    
    @Test
    @DisplayName("Revenue by category should aggregate correctly")
    void testRevenueByCategory() {
        List<SalesRecord> testData = Arrays.asList(
            new SalesRecord("ORD001", "Product A", "Electronics", 100.0, 2, LocalDate.of(2023, 1, 15), "North America"),
            new SalesRecord("ORD002", "Product B", "Electronics", 50.0, 1, LocalDate.of(2023, 2, 20), "Europe"),
            new SalesRecord("ORD003", "Product C", "Apparel", 75.0, 3, LocalDate.of(2023, 3, 10), "Asia")
        );
        
        SalesRepository repo = new InMemorySalesRepository(testData);
        SalesAnalysisService service = new SalesAnalysisService(repo);
        
        Map<String, Double> result = service.getRevenueByCategory();
        
        assertNotNull(result);
        assertTrue(result.containsKey("Electronics"));
        assertTrue(result.containsKey("Apparel"));
        assertEquals(250.0, result.get("Electronics"));
        assertEquals(225.0, result.get("Apparel"));
        
        // Verify sorting (highest first)
        String[] categories = result.keySet().toArray(new String[0]);
        assertEquals("Electronics", categories[0]);
    }
    
    @Test
    @DisplayName("Top products by revenue should return correct results")
    void testTopProductsByRevenue() {
        List<SalesRecord> testData = Arrays.asList(
            new SalesRecord("ORD001", "Product A", "Electronics", 100.0, 3, LocalDate.now(), "Region"),
            new SalesRecord("ORD002", "Product B", "Electronics", 50.0, 2, LocalDate.now(), "Region"),
            new SalesRecord("ORD003", "Product A", "Electronics", 100.0, 1, LocalDate.now(), "Region"),
            new SalesRecord("ORD004", "Product C", "Apparel", 200.0, 1, LocalDate.now(), "Region")
        );
        
        SalesRepository repo = new InMemorySalesRepository(testData);
        SalesAnalysisService service = new SalesAnalysisService(repo);
        
        List<SalesAnalysisService.ProductSummary> result = service.getTopProductsByRevenue(2);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Product A", result.get(0).getProductName());
        assertEquals(400.0, result.get(0).getTotalRevenue());
        assertEquals(2, result.get(0).getOrderCount());
    }
    
    @Test
    @DisplayName("Seasonal trends should analyze monthly data correctly")
    void testSeasonalTrends() {
        List<SalesRecord> testData = Arrays.asList(
            new SalesRecord("ORD001", "Product A", "Electronics", 100.0, 1, LocalDate.of(2023, 1, 15), "Region"),
            new SalesRecord("ORD002", "Product B", "Electronics", 200.0, 1, LocalDate.of(2023, 1, 20), "Region"),
            new SalesRecord("ORD003", "Product C", "Apparel", 150.0, 1, LocalDate.of(2023, 6, 10), "Region")
        );
        
        SalesRepository repo = new InMemorySalesRepository(testData);
        SalesAnalysisService service = new SalesAnalysisService(repo);
        
        Map<String, SalesAnalysisService.SeasonalStats> result = service.getSeasonalTrends();
        
        assertNotNull(result);
        assertTrue(result.containsKey("JANUARY"));
        assertTrue(result.containsKey("JUNE"));
        
        SalesAnalysisService.SeasonalStats janStats = result.get("JANUARY");
        assertEquals(300.0, janStats.getTotalRevenue());
        assertEquals(2, janStats.getTotalOrders());
        assertEquals(2, janStats.getUniqueProducts());
    }
    
    @Test
    @DisplayName("Regional performance should calculate stats correctly")
    void testRegionalPerformance() {
        List<SalesRecord> testData = Arrays.asList(
            new SalesRecord("ORD001", "Product A", "Electronics", 100.0, 2, LocalDate.now(), "North America"),
            new SalesRecord("ORD002", "Product B", "Electronics", 50.0, 1, LocalDate.now(), "North America"),
            new SalesRecord("ORD003", "Product C", "Apparel", 75.0, 1, LocalDate.now(), "Europe")
        );
        
        SalesRepository repo = new InMemorySalesRepository(testData);
        SalesAnalysisService service = new SalesAnalysisService(repo);
        
        Map<String, SalesAnalysisService.RegionalStats> result = service.getRegionalPerformance();
        
        assertNotNull(result);
        assertTrue(result.containsKey("North America"));
        assertTrue(result.containsKey("Europe"));
        
        SalesAnalysisService.RegionalStats naStats = result.get("North America");
        assertEquals(250.0, naStats.getTotalRevenue());
        assertEquals(2, naStats.getOrderCount());
        assertEquals("Electronics", naStats.getMostPopularCategory());
    }
    
    @Test
    @DisplayName("High value orders should filter and sort correctly")
    void testHighValueOrders() {
        List<SalesRecord> testData = Arrays.asList(
            new SalesRecord("ORD001", "Product A", "Electronics", 100.0, 6, LocalDate.now(), "Region"), // 600.0
            new SalesRecord("ORD002", "Product B", "Electronics", 50.0, 2, LocalDate.now(), "Region"),  // 100.0
            new SalesRecord("ORD003", "Product C", "Apparel", 200.0, 2, LocalDate.now(), "Region")       // 400.0
        );
        
        SalesRepository repo = new InMemorySalesRepository(testData);
        SalesAnalysisService service = new SalesAnalysisService(repo);
        
        List<SalesRecord> result = service.getHighValueOrders(300.0);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(600.0, result.get(0).getTotalRevenue());
        assertEquals(400.0, result.get(1).getTotalRevenue());
    }
    
    @Test
    @DisplayName("Price distribution should categorize correctly")
    void testPriceDistribution() {
        List<SalesRecord> testData = Arrays.asList(
            new SalesRecord("ORD001", "Product A", "Electronics", 25.0, 1, LocalDate.now(), "Region"),  // Under $50
            new SalesRecord("ORD002", "Product B", "Electronics", 75.0, 1, LocalDate.now(), "Region"),  // $50-$99
            new SalesRecord("ORD003", "Product C", "Apparel", 150.0, 1, LocalDate.now(), "Region"),     // $100-$199
            new SalesRecord("ORD004", "Product D", "Electronics", 600.0, 1, LocalDate.now(), "Region")  // $500+
        );
        
        SalesRepository repo = new InMemorySalesRepository(testData);
        SalesAnalysisService service = new SalesAnalysisService(repo);
        
        Map<String, Long> result = service.getPriceDistribution();
        
        assertNotNull(result);
        assertTrue(result.containsKey("Under $50"));
        assertTrue(result.containsKey("$50 - $99"));
        assertTrue(result.containsKey("$100 - $199"));
        assertTrue(result.containsKey("$500+"));
        
        assertEquals(1L, result.get("Under $50"));
        assertEquals(1L, result.get("$50 - $99"));
    }
    
    @Test
    @DisplayName("Overall summary should calculate all metrics correctly")
    void testOverallSummary() {
        List<SalesRecord> testData = Arrays.asList(
            new SalesRecord("ORD001", "Product A", "Electronics", 100.0, 2, LocalDate.now(), "North America"),
            new SalesRecord("ORD002", "Product B", "Apparel", 50.0, 1, LocalDate.now(), "Europe"),
            new SalesRecord("ORD003", "Product C", "Electronics", 75.0, 3, LocalDate.now(), "Asia")
        );
        
        SalesRepository repo = new InMemorySalesRepository(testData);
        SalesAnalysisService service = new SalesAnalysisService(repo);
        
        SalesAnalysisService.SalesSummary result = service.getOverallSummary();
        
        assertNotNull(result);
        assertEquals(3, result.getTotalOrders());
        assertEquals(475.0, result.getTotalRevenue()); // 100*2 + 50*1 + 75*3 = 200 + 50 + 225 = 475
        assertEquals(3, result.getUniqueProducts());
        assertEquals(2, result.getUniqueCategories());
        assertEquals(3, result.getUniqueRegions());
        assertEquals(6, result.getTotalQuantity());
    }
    
    @Test
    @DisplayName("Low turnover products should identify correctly")
    void testLowTurnoverProducts() {
        List<SalesRecord> testData = Arrays.asList(
            new SalesRecord("ORD001", "Product A", "Electronics", 100.0, 1, LocalDate.now(), "Region"), // Low turnover
            new SalesRecord("ORD002", "Product B", "Electronics", 50.0, 5, LocalDate.now(), "Region"),  // High turnover
            new SalesRecord("ORD003", "Product C", "Apparel", 75.0, 2, LocalDate.now(), "Region")       // Low turnover
        );
        
        SalesRepository repo = new InMemorySalesRepository(testData);
        SalesAnalysisService service = new SalesAnalysisService(repo);
        
        List<SalesAnalysisService.ProductTurnover> result = service.getLowTurnoverProducts(3);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Check sorting (lowest first)
        assertTrue(result.get(0).getTotalQuantitySold() <= result.get(1).getTotalQuantitySold());
    }
    
    @Test
    @DisplayName("Empty dataset should be handled gracefully")
    void testEmptyDataset() {
        SalesRepository emptyRepo = new InMemorySalesRepository();
        SalesAnalysisService service = new SalesAnalysisService(emptyRepo);
        
        // All methods should handle empty data gracefully
        Map<String, Double> categories = service.getRevenueByCategory();
        assertTrue(categories.isEmpty());
        
        List<SalesAnalysisService.ProductSummary> topProducts = service.getTopProductsByRevenue(5);
        assertTrue(topProducts.isEmpty());
        
        SalesAnalysisService.SalesSummary summary = service.getOverallSummary();
        assertEquals(0, summary.getTotalOrders());
        assertEquals(0.0, summary.getTotalRevenue());
    }
    
    @Test
    @DisplayName("Single record dataset should be handled correctly")
    void testSingleRecord() {
        List<SalesRecord> singleRecord = Arrays.asList(
            new SalesRecord("ORD001", "Single Product", "Electronics", 99.99, 2, LocalDate.now(), "Region")
        );
        
        SalesRepository repo = new InMemorySalesRepository(singleRecord);
        SalesAnalysisService service = new SalesAnalysisService(repo);
        
        SalesAnalysisService.SalesSummary summary = service.getOverallSummary();
        assertEquals(1, summary.getTotalOrders());
        assertEquals(199.98, summary.getTotalRevenue());
        assertEquals(1, summary.getUniqueProducts());
        
        Map<String, Double> categories = service.getRevenueByCategory();
        assertEquals(1, categories.size());
        assertTrue(categories.containsKey("Electronics"));
    }
    
    @Test
    @DisplayName("Parameter validation should reject invalid inputs")
    void testParameterValidation() {
        SalesRepository repo = new InMemorySalesRepository();
        SalesAnalysisService service = new SalesAnalysisService(repo);
        
        // Test invalid limit for top products
        assertThrows(IllegalArgumentException.class, () ->
            service.getTopProductsByRevenue(0)
        );
        
        assertThrows(IllegalArgumentException.class, () ->
            service.getTopProductsByRevenue(-1)
        );
        
        // Test invalid threshold for high value orders
        assertThrows(IllegalArgumentException.class, () ->
            service.getHighValueOrders(-100.0)
        );
        
        // Test invalid threshold for low turnover products
        assertThrows(IllegalArgumentException.class, () ->
            service.getLowTurnoverProducts(0)
        );
    }
    
    @Test
    @DisplayName("Complex aggregations should handle product grouping correctly")
    void testComplexAggregations() {
        List<SalesRecord> testData = Arrays.asList(
            new SalesRecord("ORD001", "Product A", "Electronics", 100.0, 1, LocalDate.of(2023, 1, 15), "North America"),
            new SalesRecord("ORD002", "Product B", "Electronics", 200.0, 2, LocalDate.of(2023, 1, 20), "North America"),
            new SalesRecord("ORD003", "Product A", "Electronics", 100.0, 3, LocalDate.of(2023, 2, 10), "Europe"),
            new SalesRecord("ORD004", "Product C", "Apparel", 50.0, 1, LocalDate.of(2023, 2, 15), "Europe")
        );
        
        SalesRepository repo = new InMemorySalesRepository(testData);
        SalesAnalysisService service = new SalesAnalysisService(repo);
        
        // Test product aggregation across different orders
        List<SalesAnalysisService.ProductSummary> products = service.getTopProductsByRevenue(3);
        
        // Product A should be aggregated: 100*1 + 100*3 = 400
        SalesAnalysisService.ProductSummary productA = products.stream()
                .filter(p -> "Product A".equals(p.getProductName()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(productA);
        assertEquals(400.0, productA.getTotalRevenue());
        assertEquals(2, productA.getOrderCount());
        assertEquals(200.0, productA.getAverageOrderValue());
    }
    
    @Test
    @DisplayName("Stream operations should maintain correct sorting")
    void testStreamOperations() {
        List<SalesRecord> testData = Arrays.asList(
            new SalesRecord("ORD001", "Product A", "Electronics", 100.0, 1, LocalDate.now(), "Region"),
            new SalesRecord("ORD002", "Product B", "Electronics", 200.0, 1, LocalDate.now(), "Region"),
            new SalesRecord("ORD003", "Product C", "Apparel", 150.0, 1, LocalDate.now(), "Region")
        );
        
        SalesRepository repo = new InMemorySalesRepository(testData);
        SalesAnalysisService service = new SalesAnalysisService(repo);
        
        // Test that results are properly sorted (demonstrating stream sorting)
        Map<String, Double> categories = service.getRevenueByCategory();
        String[] categoryNames = categories.keySet().toArray(new String[0]);
        
        // Should be sorted by revenue descending: Electronics (300) then Apparel (150)
        assertEquals("Electronics", categoryNames[0]);
        assertEquals("Apparel", categoryNames[1]);
        
        // Test filtering operations
        List<SalesRecord> highValue = service.getHighValueOrders(175.0);
        assertEquals(1, highValue.size());
        assertEquals("Product B", highValue.get(0).getProductName());
    }
    
    @Test
    @DisplayName("Functional programming patterns should work correctly")
    void testFunctionalProgramming() {
        List<SalesRecord> testData = Arrays.asList(
            new SalesRecord("ORD001", "Product A", "Electronics", 100.0, 2, LocalDate.of(2023, 3, 15), "North America"),
            new SalesRecord("ORD002", "Product B", "Electronics", 50.0, 1, LocalDate.of(2023, 3, 20), "Europe")
        );
        
        SalesRepository repo = new InMemorySalesRepository(testData);
        SalesAnalysisService service = new SalesAnalysisService(repo);
        
        // Test seasonal trends which uses complex functional operations
        Map<String, SalesAnalysisService.SeasonalStats> seasonal = service.getSeasonalTrends();
        assertTrue(seasonal.containsKey("MARCH"));
        
        SalesAnalysisService.SeasonalStats marchStats = seasonal.get("MARCH");
        assertEquals(250.0, marchStats.getTotalRevenue());
        assertEquals(2, marchStats.getUniqueProducts());
        
        // Test regional performance which uses collectingAndThen
        Map<String, SalesAnalysisService.RegionalStats> regional = service.getRegionalPerformance();
        assertTrue(regional.containsKey("North America"));
        assertTrue(regional.containsKey("Europe"));
    }
    
    @Test
    @DisplayName("Property-based test: Total revenue by category should equal sum of individual record revenues")
    void testPropertyBasedRevenueConsistency() {
        List<SalesRecord> testData = Arrays.asList(
            new SalesRecord("ORD001", "Product A", "Electronics", 100.0, 2, LocalDate.now(), "Region"), // 200.0
            new SalesRecord("ORD002", "Product B", "Electronics", 50.0, 1, LocalDate.now(), "Region"),  // 50.0
            new SalesRecord("ORD003", "Product C", "Apparel", 75.0, 3, LocalDate.now(), "Region"),      // 225.0
            new SalesRecord("ORD004", "Product D", "Apparel", 25.0, 2, LocalDate.now(), "Region"),      // 50.0
            new SalesRecord("ORD005", "Product E", "Home", 150.0, 1, LocalDate.now(), "Region")         // 150.0
        );
        
        SalesRepository repo = new InMemorySalesRepository(testData);
        SalesAnalysisService service = new SalesAnalysisService(repo);
        
        // Get revenue by category from the service
        Map<String, Double> categoryRevenue = service.getRevenueByCategory();
        
        // Calculate expected revenue by manually summing records per category
        Map<String, Double> expectedRevenue = testData.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                SalesRecord::getCategory,
                java.util.stream.Collectors.summingDouble(SalesRecord::getTotalRevenue)
            ));
        
        // Property: The service results should match manual calculation
        assertEquals(expectedRevenue.size(), categoryRevenue.size());
        
        for (String category : expectedRevenue.keySet()) {
            assertTrue(categoryRevenue.containsKey(category),
                "Category " + category + " should be present in service results");
            assertEquals(expectedRevenue.get(category), categoryRevenue.get(category), 0.001,
                "Revenue for category " + category + " should match manual calculation");
        }
        
        // Additional property: Total of all categories should equal sum of all record revenues
        double totalServiceRevenue = categoryRevenue.values().stream()
            .mapToDouble(Double::doubleValue)
            .sum();
        
        double totalExpectedRevenue = testData.stream()
            .mapToDouble(SalesRecord::getTotalRevenue)
            .sum();
        
        assertEquals(totalExpectedRevenue, totalServiceRevenue, 0.001,
            "Total revenue across all categories should equal sum of all individual record revenues");
        
        // Property: getTotalRevenue() method should equal sum of category revenues
        SalesAnalysisService.SalesSummary summary = service.getOverallSummary();
        assertEquals(totalExpectedRevenue, summary.getTotalRevenue(), 0.001,
            "Overall summary total revenue should match sum of all individual revenues");
    }
}