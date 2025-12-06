package com.ecommerce.repository;

import com.ecommerce.model.SalesRecord;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of SalesRepository using Collections.
 * Thread-safe implementation suitable for concurrent access.
 * Uses streams extensively for data filtering and processing.
 */
public class InMemorySalesRepository implements SalesRepository {
    
    private final ConcurrentHashMap<String, SalesRecord> salesData;
    
    /**
     * Creates a new empty in-memory repository.
     */
    public InMemorySalesRepository() {
        this.salesData = new ConcurrentHashMap<>();
    }
    
    /**
     * Creates a new in-memory repository with initial data.
     * 
     * @param initialData Initial sales records to populate the repository
     * @throws IllegalArgumentException if initialData contains null records or duplicate order IDs
     */
    public InMemorySalesRepository(List<SalesRecord> initialData) {
        this();
        if (initialData != null) {
            saveAll(initialData);
        }
    }
    
    @Override
    public List<SalesRecord> findAll() {
        return new ArrayList<>(salesData.values());
    }
    
    @Override
    public Optional<SalesRecord> findByOrderId(String orderId) {
        validateOrderId(orderId);
        return Optional.ofNullable(salesData.get(orderId));
    }
    
    @Override
    public List<SalesRecord> findByCategory(String category) {
        validateStringField(category, "Category");
        
        return salesData.values().stream()
                .filter(record -> category.equalsIgnoreCase(record.getCategory()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SalesRecord> findByRegion(String region) {
        validateStringField(region, "Region");
        
        return salesData.values().stream()
                .filter(record -> region.equalsIgnoreCase(record.getCustomerRegion()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SalesRecord> findByDateRange(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        
        return salesData.values().stream()
                .filter(record -> {
                    LocalDate orderDate = record.getOrderDate();
                    return !orderDate.isBefore(startDate) && !orderDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SalesRecord> findByProductName(String productName) {
        validateStringField(productName, "Product Name");
        
        return salesData.values().stream()
                .filter(record -> productName.equalsIgnoreCase(record.getProductName()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SalesRecord> findByPriceRange(double minPrice, double maxPrice) {
        validatePriceRange(minPrice, maxPrice);
        
        return salesData.values().stream()
                .filter(record -> record.getPrice() >= minPrice && record.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }
    
    @Override
    public SalesRecord save(SalesRecord salesRecord) {
        validateSalesRecord(salesRecord);
        
        if (salesData.containsKey(salesRecord.getOrderId())) {
            throw new IllegalStateException("Sales record with order ID '" + 
                salesRecord.getOrderId() + "' already exists");
        }
        
        salesData.put(salesRecord.getOrderId(), salesRecord);
        return salesRecord;
    }
    
    @Override
    public List<SalesRecord> saveAll(List<SalesRecord> salesRecords) {
        if (salesRecords == null) {
            throw new IllegalArgumentException("Sales records list cannot be null");
        }
        
        // Validate all records first
        salesRecords.forEach(this::validateSalesRecord);
        
        // Check for duplicates within the list
        List<String> orderIds = salesRecords.stream()
                .map(SalesRecord::getOrderId)
                .collect(Collectors.toList());
        
        long distinctCount = orderIds.stream().distinct().count();
        if (distinctCount != orderIds.size()) {
            throw new IllegalArgumentException("Duplicate order IDs found in the provided list");
        }
        
        // Check for existing records
        List<String> existingIds = orderIds.stream()
                .filter(salesData::containsKey)
                .collect(Collectors.toList());
        
        if (!existingIds.isEmpty()) {
            throw new IllegalStateException("Sales records with the following order IDs already exist: " + 
                existingIds);
        }
        
        // Save all records
        salesRecords.forEach(record -> salesData.put(record.getOrderId(), record));
        
        return new ArrayList<>(salesRecords);
    }
    
    @Override
    public boolean deleteByOrderId(String orderId) {
        validateOrderId(orderId);
        return salesData.remove(orderId) != null;
    }
    
    @Override
    public void deleteAll() {
        salesData.clear();
    }
    
    @Override
    public long count() {
        return salesData.size();
    }
    
    @Override
    public boolean existsByOrderId(String orderId) {
        validateOrderId(orderId);
        return salesData.containsKey(orderId);
    }
    
    @Override
    public List<String> findDistinctCategories() {
        return salesData.values().stream()
                .map(SalesRecord::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> findDistinctRegions() {
        return salesData.values().stream()
                .map(SalesRecord::getCustomerRegion)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    // Additional utility methods for stream-based queries
    
    /**
     * Finds sales records with total revenue above a threshold.
     * 
     * @param threshold Minimum revenue threshold
     * @return List of high-value sales records
     */
    public List<SalesRecord> findHighValueSales(double threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold must be non-negative");
        }
        
        return salesData.values().stream()
                .filter(record -> record.getTotalRevenue() > threshold)
                .sorted((r1, r2) -> Double.compare(r2.getTotalRevenue(), r1.getTotalRevenue()))
                .collect(Collectors.toList());
    }
    
    /**
     * Finds top N products by total quantity sold.
     * 
     * @param limit Maximum number of products to return
     * @return List of product names sorted by total quantity sold
     */
    public List<String> findTopProductsByQuantity(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        
        return salesData.values().stream()
                .collect(Collectors.groupingBy(
                    SalesRecord::getProductName,
                    Collectors.summingInt(SalesRecord::getQuantity)
                ))
                .entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(limit)
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
    }
    
    // Validation methods
    private void validateOrderId(String orderId) {
        validateStringField(orderId, "Order ID");
    }
    
    private void validateStringField(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }
    
    private void validateSalesRecord(SalesRecord salesRecord) {
        if (salesRecord == null) {
            throw new IllegalArgumentException("Sales record cannot be null");
        }
    }
    
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
    }
    
    private void validatePriceRange(double minPrice, double maxPrice) {
        if (minPrice < 0) {
            throw new IllegalArgumentException("Minimum price cannot be negative");
        }
        if (maxPrice < 0) {
            throw new IllegalArgumentException("Maximum price cannot be negative");
        }
        if (minPrice > maxPrice) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }
    }
    
    /**
     * Gets repository statistics for debugging and monitoring.
     * 
     * @return String containing repository statistics
     */
    public String getStatistics() {
        long totalRecords = count();
        if (totalRecords == 0) {
            return "Repository Statistics: Empty repository";
        }
        
        double totalRevenue = salesData.values().stream()
                .mapToDouble(SalesRecord::getTotalRevenue)
                .sum();
        
        long distinctCategories = findDistinctCategories().size();
        long distinctRegions = findDistinctRegions().size();
        
        return String.format(
            "Repository Statistics: Records=%d, Categories=%d, Regions=%d, Total Revenue=%.2f",
            totalRecords, distinctCategories, distinctRegions, totalRevenue
        );
    }
}