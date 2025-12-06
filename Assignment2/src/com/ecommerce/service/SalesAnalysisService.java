package com.ecommerce.service;

import com.ecommerce.model.SalesRecord;
import com.ecommerce.repository.SalesRepository;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class providing comprehensive sales data analysis using Java Streams.
 * Demonstrates functional programming paradigms including grouping, aggregation,
 * filtering, mapping, and statistical operations.
 */
public class SalesAnalysisService {
    
    private final SalesRepository salesRepository;
    
    /**
     * Creates a new SalesAnalysisService with the specified repository.
     * 
     * @param salesRepository The repository to use for data access
     * @throws IllegalArgumentException if repository is null
     */
    public SalesAnalysisService(SalesRepository salesRepository) {
        if (salesRepository == null) {
            throw new IllegalArgumentException("Sales repository cannot be null");
        }
        this.salesRepository = salesRepository;
    }
    
    /**
     * Analyzes total revenue by product category using grouping and reducing operations.
     * 
     * @return Map of category names to total revenue, sorted by revenue descending
     */
    public Map<String, Double> getRevenueByCategory() {
        List<SalesRecord> records = salesRepository.findAll();
        
        if (records.isEmpty()) {
            return new LinkedHashMap<>();
        }
        
        return records.stream()
                .collect(Collectors.groupingBy(
                    SalesRecord::getCategory,
                    Collectors.summingDouble(SalesRecord::getTotalRevenue)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
    }
    
    /**
     * Identifies top N products by total revenue using sorting and limiting.
     * 
     * @param limit Maximum number of products to return
     * @return List of ProductSummary objects sorted by revenue descending
     * @throws IllegalArgumentException if limit is not positive
     */
    public List<ProductSummary> getTopProductsByRevenue(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        
        List<SalesRecord> records = salesRepository.findAll();
        
        return records.stream()
                .collect(Collectors.groupingBy(
                    SalesRecord::getProductName,
                    Collectors.summarizingDouble(SalesRecord::getTotalRevenue)
                ))
                .entrySet().stream()
                .map(entry -> new ProductSummary(
                    entry.getKey(),
                    entry.getValue().getSum(),
                    (long) entry.getValue().getCount(),
                    entry.getValue().getAverage()
                ))
                .sorted(Comparator.comparing(ProductSummary::getTotalRevenue).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Analyzes seasonal trends by grouping sales by month and calculating statistics.
     * 
     * @return Map of month names to seasonal statistics
     */
    public Map<String, SeasonalStats> getSeasonalTrends() {
        List<SalesRecord> records = salesRepository.findAll();
        
        if (records.isEmpty()) {
            return new LinkedHashMap<>();
        }
        
        return records.stream()
                .collect(Collectors.groupingBy(
                    record -> Month.of(record.getOrderMonth()).name(),
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        monthRecords -> {
                            double totalRevenue = monthRecords.stream()
                                    .mapToDouble(SalesRecord::getTotalRevenue)
                                    .sum();
                            
                            int totalOrders = monthRecords.size();
                            
                            double averageOrderValue = monthRecords.stream()
                                    .mapToDouble(SalesRecord::getTotalRevenue)
                                    .average()
                                    .orElse(0.0);
                            
                            long uniqueProducts = monthRecords.stream()
                                    .map(SalesRecord::getProductName)
                                    .distinct()
                                    .count();
                            
                            return new SeasonalStats(totalRevenue, totalOrders, 
                                    averageOrderValue, uniqueProducts);
                        }
                    )
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
    }
    
    /**
     * Analyzes regional performance using grouping and statistical operations.
     * 
     * @return Map of regions to performance statistics
     */
    public Map<String, RegionalStats> getRegionalPerformance() {
        List<SalesRecord> records = salesRepository.findAll();
        
        if (records.isEmpty()) {
            return new LinkedHashMap<>();
        }
        
        return records.stream()
                .collect(Collectors.groupingBy(
                    SalesRecord::getCustomerRegion,
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        regionRecords -> {
                            DoubleSummaryStatistics revenueStats = regionRecords.stream()
                                    .mapToDouble(SalesRecord::getTotalRevenue)
                                    .summaryStatistics();
                            
                            IntSummaryStatistics quantityStats = regionRecords.stream()
                                    .mapToInt(SalesRecord::getQuantity)
                                    .summaryStatistics();
                            
                            long uniqueCustomers = regionRecords.stream()
                                    .map(SalesRecord::getOrderId)
                                    .distinct()
                                    .count();
                            
                            Map<String, Long> topCategories = regionRecords.stream()
                                    .collect(Collectors.groupingBy(
                                        SalesRecord::getCategory,
                                        Collectors.counting()
                                    ));
                            
                            String mostPopularCategory = topCategories.entrySet().stream()
                                    .max(Map.Entry.comparingByValue())
                                    .map(Map.Entry::getKey)
                                    .orElse("None");
                            
                            return new RegionalStats(
                                    revenueStats.getSum(),
                                    revenueStats.getAverage(),
                                    revenueStats.getMax(),
                                    revenueStats.getMin(),
                                    (long) revenueStats.getCount(),
                                    quantityStats.getSum(),
                                    uniqueCustomers,
                                    mostPopularCategory
                            );
                        }
                    )
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, RegionalStats>comparingByValue(
                        Comparator.comparing(RegionalStats::getTotalRevenue).reversed()))
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
    }
    
    /**
     * Finds high-value orders above a revenue threshold using filtering and sorting.
     * 
     * @param threshold Minimum revenue threshold
     * @return List of high-value sales records
     * @throws IllegalArgumentException if threshold is negative
     */
    public List<SalesRecord> getHighValueOrders(double threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold cannot be negative");
        }
        
        return salesRepository.findAll().stream()
                .filter(record -> record.getTotalRevenue() > threshold)
                .sorted(Comparator.comparing(SalesRecord::getTotalRevenue).reversed())
                .collect(Collectors.toList());
    }
    
    /**
     * Analyzes price distribution across different ranges using partitioning and grouping.
     * 
     * @return Map of price ranges to count of products
     */
    public Map<String, Long> getPriceDistribution() {
        List<SalesRecord> records = salesRepository.findAll();
        
        if (records.isEmpty()) {
            return new LinkedHashMap<>();
        }
        
        return records.stream()
                .collect(Collectors.groupingBy(
                    record -> {
                        double price = record.getPrice();
                        if (price < 50) return "Under $50";
                        else if (price < 100) return "$50 - $99";
                        else if (price < 200) return "$100 - $199";
                        else if (price < 500) return "$200 - $499";
                        else return "$500+";
                    },
                    Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
    }
    
    /**
     * Gets comprehensive summary statistics using various stream operations.
     * 
     * @return Overall sales summary statistics
     */
    public SalesSummary getOverallSummary() {
        List<SalesRecord> records = salesRepository.findAll();
        
        if (records.isEmpty()) {
            return new SalesSummary(0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0);
        }
        
        DoubleSummaryStatistics revenueStats = records.stream()
                .mapToDouble(SalesRecord::getTotalRevenue)
                .summaryStatistics();
        
        DoubleSummaryStatistics priceStats = records.stream()
                .mapToDouble(SalesRecord::getPrice)
                .summaryStatistics();
        
        IntSummaryStatistics quantityStats = records.stream()
                .mapToInt(SalesRecord::getQuantity)
                .summaryStatistics();
        
        long uniqueProducts = records.stream()
                .map(SalesRecord::getProductName)
                .distinct()
                .count();
        
        long uniqueCategories = records.stream()
                .map(SalesRecord::getCategory)
                .distinct()
                .count();
        
        long uniqueRegions = records.stream()
                .map(SalesRecord::getCustomerRegion)
                .distinct()
                .count();
        
        return new SalesSummary(
                records.size(),
                revenueStats.getSum(),
                revenueStats.getAverage(),
                priceStats.getAverage(),
                revenueStats.getMax(),
                quantityStats.getSum(),
                uniqueProducts,
                uniqueCategories,
                uniqueRegions
        );
    }
    
    /**
     * Finds products with low inventory turnover using complex stream operations.
     * 
     * @param maxQuantityThreshold Maximum total quantity sold to be considered low turnover
     * @return List of low turnover products with their statistics
     * @throws IllegalArgumentException if threshold is not positive
     */
    public List<ProductTurnover> getLowTurnoverProducts(int maxQuantityThreshold) {
        if (maxQuantityThreshold <= 0) {
            throw new IllegalArgumentException("Threshold must be positive");
        }
        
        List<SalesRecord> records = salesRepository.findAll();
        
        return records.stream()
                .collect(Collectors.groupingBy(
                    SalesRecord::getProductName,
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        productRecords -> {
                            int totalQuantity = productRecords.stream()
                                    .mapToInt(SalesRecord::getQuantity)
                                    .sum();
                            
                            double totalRevenue = productRecords.stream()
                                    .mapToDouble(SalesRecord::getTotalRevenue)
                                    .sum();
                            
                            double avgPrice = productRecords.stream()
                                    .mapToDouble(SalesRecord::getPrice)
                                    .average()
                                    .orElse(0.0);
                            
                            String category = productRecords.get(0).getCategory();
                            
                            return new ProductTurnover(
                                    productRecords.get(0).getProductName(),
                                    category,
                                    totalQuantity,
                                    totalRevenue,
                                    avgPrice,
                                    productRecords.size()
                            );
                        }
                    )
                ))
                .values().stream()
                .filter(turnover -> turnover.getTotalQuantitySold() <= maxQuantityThreshold)
                .sorted(Comparator.comparing(ProductTurnover::getTotalQuantitySold))
                .collect(Collectors.toList());
    }
    
    // Data classes for analysis results
    
    public static class ProductSummary {
        private final String productName;
        private final double totalRevenue;
        private final long orderCount;
        private final double averageOrderValue;
        
        public ProductSummary(String productName, double totalRevenue, 
                            long orderCount, double averageOrderValue) {
            this.productName = productName;
            this.totalRevenue = totalRevenue;
            this.orderCount = orderCount;
            this.averageOrderValue = averageOrderValue;
        }
        
        // Getters
        public String getProductName() { return productName; }
        public double getTotalRevenue() { return totalRevenue; }
        public long getOrderCount() { return orderCount; }
        public double getAverageOrderValue() { return averageOrderValue; }
        
        @Override
        public String toString() {
            return String.format("%s: Revenue=%.2f, Orders=%d, Avg=%.2f", 
                productName, totalRevenue, orderCount, averageOrderValue);
        }
    }
    
    public static class SeasonalStats {
        private final double totalRevenue;
        private final int totalOrders;
        private final double averageOrderValue;
        private final long uniqueProducts;
        
        public SeasonalStats(double totalRevenue, int totalOrders, 
                           double averageOrderValue, long uniqueProducts) {
            this.totalRevenue = totalRevenue;
            this.totalOrders = totalOrders;
            this.averageOrderValue = averageOrderValue;
            this.uniqueProducts = uniqueProducts;
        }
        
        // Getters
        public double getTotalRevenue() { return totalRevenue; }
        public int getTotalOrders() { return totalOrders; }
        public double getAverageOrderValue() { return averageOrderValue; }
        public long getUniqueProducts() { return uniqueProducts; }
        
        @Override
        public String toString() {
            return String.format("Revenue=%.2f, Orders=%d, Avg=%.2f, Products=%d", 
                totalRevenue, totalOrders, averageOrderValue, uniqueProducts);
        }
    }
    
    public static class RegionalStats {
        private final double totalRevenue;
        private final double averageRevenue;
        private final double maxRevenue;
        private final double minRevenue;
        private final long orderCount;
        private final long totalQuantity;
        private final long uniqueCustomers;
        private final String mostPopularCategory;
        
        public RegionalStats(double totalRevenue, double averageRevenue, 
                           double maxRevenue, double minRevenue, long orderCount,
                           long totalQuantity, long uniqueCustomers, String mostPopularCategory) {
            this.totalRevenue = totalRevenue;
            this.averageRevenue = averageRevenue;
            this.maxRevenue = maxRevenue;
            this.minRevenue = minRevenue;
            this.orderCount = orderCount;
            this.totalQuantity = totalQuantity;
            this.uniqueCustomers = uniqueCustomers;
            this.mostPopularCategory = mostPopularCategory;
        }
        
        // Getters
        public double getTotalRevenue() { return totalRevenue; }
        public double getAverageRevenue() { return averageRevenue; }
        public double getMaxRevenue() { return maxRevenue; }
        public double getMinRevenue() { return minRevenue; }
        public long getOrderCount() { return orderCount; }
        public long getTotalQuantity() { return totalQuantity; }
        public long getUniqueCustomers() { return uniqueCustomers; }
        public String getMostPopularCategory() { return mostPopularCategory; }
        
        @Override
        public String toString() {
            return String.format("Revenue=%.2f, Orders=%d, Top Category=%s", 
                totalRevenue, orderCount, mostPopularCategory);
        }
    }
    
    public static class SalesSummary {
        private final int totalOrders;
        private final double totalRevenue;
        private final double averageOrderValue;
        private final double averagePrice;
        private final double maxOrderValue;
        private final long totalQuantity;
        private final long uniqueProducts;
        private final long uniqueCategories;
        private final long uniqueRegions;
        
        public SalesSummary(int totalOrders, double totalRevenue, double averageOrderValue,
                          double averagePrice, double maxOrderValue, long totalQuantity,
                          long uniqueProducts, long uniqueCategories, long uniqueRegions) {
            this.totalOrders = totalOrders;
            this.totalRevenue = totalRevenue;
            this.averageOrderValue = averageOrderValue;
            this.averagePrice = averagePrice;
            this.maxOrderValue = maxOrderValue;
            this.totalQuantity = totalQuantity;
            this.uniqueProducts = uniqueProducts;
            this.uniqueCategories = uniqueCategories;
            this.uniqueRegions = uniqueRegions;
        }
        
        // Getters
        public int getTotalOrders() { return totalOrders; }
        public double getTotalRevenue() { return totalRevenue; }
        public double getAverageOrderValue() { return averageOrderValue; }
        public double getAveragePrice() { return averagePrice; }
        public double getMaxOrderValue() { return maxOrderValue; }
        public long getTotalQuantity() { return totalQuantity; }
        public long getUniqueProducts() { return uniqueProducts; }
        public long getUniqueCategories() { return uniqueCategories; }
        public long getUniqueRegions() { return uniqueRegions; }
        
        @Override
        public String toString() {
            return String.format("Orders=%d, Revenue=%.2f, Products=%d, Categories=%d, Regions=%d", 
                totalOrders, totalRevenue, uniqueProducts, uniqueCategories, uniqueRegions);
        }
    }
    
    public static class ProductTurnover {
        private final String productName;
        private final String category;
        private final int totalQuantitySold;
        private final double totalRevenue;
        private final double averagePrice;
        private final int orderCount;
        
        public ProductTurnover(String productName, String category, int totalQuantitySold,
                             double totalRevenue, double averagePrice, int orderCount) {
            this.productName = productName;
            this.category = category;
            this.totalQuantitySold = totalQuantitySold;
            this.totalRevenue = totalRevenue;
            this.averagePrice = averagePrice;
            this.orderCount = orderCount;
        }
        
        // Getters
        public String getProductName() { return productName; }
        public String getCategory() { return category; }
        public int getTotalQuantitySold() { return totalQuantitySold; }
        public double getTotalRevenue() { return totalRevenue; }
        public double getAveragePrice() { return averagePrice; }
        public int getOrderCount() { return orderCount; }
        
        @Override
        public String toString() {
            return String.format("%s (%s): Qty=%d, Revenue=%.2f, Orders=%d", 
                productName, category, totalQuantitySold, totalRevenue, orderCount);
        }
    }
}