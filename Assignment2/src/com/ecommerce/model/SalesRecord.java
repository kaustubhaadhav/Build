package com.ecommerce.model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Immutable data model representing a sales record from e-commerce transactions.
 * Contains comprehensive validation and calculated methods for analysis.
 */
public final class SalesRecord {
    
    private final String orderId;
    private final String productName;
    private final String category;
    private final double price;
    private final int quantity;
    private final LocalDate orderDate;
    private final String customerRegion;
    
    /**
     * Creates a new SalesRecord with validation.
     * 
     * @param orderId Unique identifier for the order (cannot be null or empty)
     * @param productName Name of the product (cannot be null or empty)
     * @param category Product category (cannot be null or empty)
     * @param price Unit price (must be positive)
     * @param quantity Number of items ordered (must be positive)
     * @param orderDate Date of the order (cannot be null)
     * @param customerRegion Customer's region (cannot be null or empty)
     * @throws IllegalArgumentException if any validation fails
     */
    public SalesRecord(String orderId, String productName, String category, 
                      double price, int quantity, LocalDate orderDate, String customerRegion) {
        
        this.orderId = validateStringField(orderId, "Order ID");
        this.productName = validateStringField(productName, "Product Name");
        this.category = validateStringField(category, "Category");
        this.price = validatePrice(price);
        this.quantity = validateQuantity(quantity);
        this.orderDate = validateOrderDate(orderDate);
        this.customerRegion = validateStringField(customerRegion, "Customer Region");
    }
    
    /**
     * Alternative constructor for parsing from CSV string data.
     * 
     * @param orderId Order identifier
     * @param productName Product name
     * @param category Product category
     * @param price Price as string
     * @param quantity Quantity as string
     * @param orderDate Date as string (YYYY-MM-DD format)
     * @param customerRegion Customer region
     * @throws IllegalArgumentException if parsing or validation fails
     */
    public SalesRecord(String orderId, String productName, String category,
                      String price, String quantity, String orderDate, String customerRegion) {
        
        this.orderId = validateStringField(orderId, "Order ID");
        this.productName = validateStringField(productName, "Product Name");
        this.category = validateStringField(category, "Category");
        this.price = parseAndValidatePrice(price);
        this.quantity = parseAndValidateQuantity(quantity);
        this.orderDate = parseAndValidateDate(orderDate);
        this.customerRegion = validateStringField(customerRegion, "Customer Region");
    }
    
    /**
     * Calculates the total revenue for this sales record.
     * 
     * @return Total revenue (price * quantity)
     */
    public double getTotalRevenue() {
        return price * quantity;
    }
    
    /**
     * Gets the year from the order date for temporal analysis.
     * 
     * @return Year of the order
     */
    public int getOrderYear() {
        return orderDate.getYear();
    }
    
    /**
     * Gets the month from the order date for seasonal analysis.
     * 
     * @return Month of the order (1-12)
     */
    public int getOrderMonth() {
        return orderDate.getMonthValue();
    }
    
    /**
     * Gets the quarter from the order date for quarterly analysis.
     * 
     * @return Quarter of the order (1-4)
     */
    public int getOrderQuarter() {
        return (orderDate.getMonthValue() - 1) / 3 + 1;
    }
    
    // Validation methods
    private String validateStringField(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
        return value.trim();
    }
    
    private double validatePrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive, got: " + price);
        }
        if (Double.isNaN(price) || Double.isInfinite(price)) {
            throw new IllegalArgumentException("Price must be a valid number, got: " + price);
        }
        return price;
    }
    
    private int validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive, got: " + quantity);
        }
        return quantity;
    }
    
    private LocalDate validateOrderDate(LocalDate orderDate) {
        if (orderDate == null) {
            throw new IllegalArgumentException("Order date cannot be null");
        }
        return orderDate;
    }
    
    // Parsing methods for CSV construction
    private double parseAndValidatePrice(String priceStr) {
        try {
            double parsedPrice = Double.parseDouble(priceStr.trim());
            return validatePrice(parsedPrice);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid price format: " + priceStr, e);
        }
    }
    
    private int parseAndValidateQuantity(String quantityStr) {
        try {
            int parsedQuantity = Integer.parseInt(quantityStr.trim());
            return validateQuantity(parsedQuantity);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid quantity format: " + quantityStr, e);
        }
    }
    
    private LocalDate parseAndValidateDate(String dateStr) {
        try {
            LocalDate parsedDate = LocalDate.parse(dateStr.trim());
            return validateOrderDate(parsedDate);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + dateStr + 
                ". Expected format: YYYY-MM-DD", e);
        }
    }
    
    // Getters
    public String getOrderId() {
        return orderId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public String getCategory() {
        return category;
    }
    
    public double getPrice() {
        return price;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public LocalDate getOrderDate() {
        return orderDate;
    }
    
    public String getCustomerRegion() {
        return customerRegion;
    }
    
    // Object methods
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        SalesRecord that = (SalesRecord) obj;
        return Double.compare(that.price, price) == 0 &&
               quantity == that.quantity &&
               Objects.equals(orderId, that.orderId) &&
               Objects.equals(productName, that.productName) &&
               Objects.equals(category, that.category) &&
               Objects.equals(orderDate, that.orderDate) &&
               Objects.equals(customerRegion, that.customerRegion);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(orderId, productName, category, price, quantity, orderDate, customerRegion);
    }
    
    @Override
    public String toString() {
        return String.format(
            "SalesRecord{orderId='%s', productName='%s', category='%s', " +
            "price=%.2f, quantity=%d, orderDate=%s, customerRegion='%s', totalRevenue=%.2f}",
            orderId, productName, category, price, quantity, orderDate, customerRegion, getTotalRevenue()
        );
    }
}