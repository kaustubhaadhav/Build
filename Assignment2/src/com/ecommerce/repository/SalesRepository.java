package com.ecommerce.repository;

import com.ecommerce.model.SalesRecord;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for sales data access operations.
 * Provides abstraction over data source implementation to support
 * swappable data sources (in-memory, database, file, etc.).
 */
public interface SalesRepository {
    
    /**
     * Retrieves all sales records.
     * 
     * @return List of all sales records, empty list if none found
     */
    List<SalesRecord> findAll();
    
    /**
     * Finds a sales record by its unique order ID.
     * 
     * @param orderId The order ID to search for (cannot be null)
     * @return Optional containing the sales record if found, empty otherwise
     * @throws IllegalArgumentException if orderId is null or empty
     */
    Optional<SalesRecord> findByOrderId(String orderId);
    
    /**
     * Finds all sales records for a specific product category.
     * 
     * @param category The product category to filter by (cannot be null)
     * @return List of sales records in the specified category, empty if none found
     * @throws IllegalArgumentException if category is null or empty
     */
    List<SalesRecord> findByCategory(String category);
    
    /**
     * Finds all sales records for a specific customer region.
     * 
     * @param region The customer region to filter by (cannot be null)
     * @return List of sales records from the specified region, empty if none found
     * @throws IllegalArgumentException if region is null or empty
     */
    List<SalesRecord> findByRegion(String region);
    
    /**
     * Finds all sales records within a specific date range (inclusive).
     * 
     * @param startDate The start date of the range (cannot be null)
     * @param endDate The end date of the range (cannot be null)
     * @return List of sales records within the date range, empty if none found
     * @throws IllegalArgumentException if dates are null or startDate is after endDate
     */
    List<SalesRecord> findByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Finds all sales records for a specific product name.
     * 
     * @param productName The product name to search for (cannot be null)
     * @return List of sales records for the specified product, empty if none found
     * @throws IllegalArgumentException if productName is null or empty
     */
    List<SalesRecord> findByProductName(String productName);
    
    /**
     * Finds all sales records with price within a specified range.
     * 
     * @param minPrice Minimum price (inclusive, must be positive)
     * @param maxPrice Maximum price (inclusive, must be positive)
     * @return List of sales records within the price range, empty if none found
     * @throws IllegalArgumentException if prices are negative or minPrice > maxPrice
     */
    List<SalesRecord> findByPriceRange(double minPrice, double maxPrice);
    
    /**
     * Adds a new sales record to the repository.
     * 
     * @param salesRecord The sales record to add (cannot be null)
     * @return The added sales record
     * @throws IllegalArgumentException if salesRecord is null
     * @throws IllegalStateException if a record with the same order ID already exists
     */
    SalesRecord save(SalesRecord salesRecord);
    
    /**
     * Adds multiple sales records to the repository.
     * 
     * @param salesRecords The list of sales records to add (cannot be null)
     * @return List of added sales records
     * @throws IllegalArgumentException if salesRecords is null or contains null records
     * @throws IllegalStateException if any record with duplicate order ID exists
     */
    List<SalesRecord> saveAll(List<SalesRecord> salesRecords);
    
    /**
     * Removes a sales record by order ID.
     * 
     * @param orderId The order ID of the record to remove (cannot be null)
     * @return true if the record was removed, false if not found
     * @throws IllegalArgumentException if orderId is null or empty
     */
    boolean deleteByOrderId(String orderId);
    
    /**
     * Removes all sales records from the repository.
     */
    void deleteAll();
    
    /**
     * Returns the total number of sales records.
     * 
     * @return The count of sales records
     */
    long count();
    
    /**
     * Checks if a sales record exists with the given order ID.
     * 
     * @param orderId The order ID to check (cannot be null)
     * @return true if a record exists, false otherwise
     * @throws IllegalArgumentException if orderId is null or empty
     */
    boolean existsByOrderId(String orderId);
    
    /**
     * Gets all distinct product categories in the repository.
     * 
     * @return List of unique categories, empty if no records exist
     */
    List<String> findDistinctCategories();
    
    /**
     * Gets all distinct customer regions in the repository.
     * 
     * @return List of unique regions, empty if no records exist
     */
    List<String> findDistinctRegions();
}