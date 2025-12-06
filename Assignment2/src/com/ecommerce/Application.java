package com.ecommerce;

import com.ecommerce.model.SalesRecord;
import com.ecommerce.repository.InMemorySalesRepository;
import com.ecommerce.repository.SalesRepository;
import com.ecommerce.service.CsvDataLoader;
import com.ecommerce.service.SalesAnalysisService;
import com.ecommerce.util.ValidationUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Main application class providing a console interface for sales data analysis.
 * Demonstrates comprehensive Stream API usage for data processing and analysis.
 */
public class Application {
    
    private static final String DEFAULT_CSV_FILE = "sales_data.csv";
    private static final int MAX_DISPLAY_ITEMS = 20;
    
    private SalesRepository salesRepository;
    private SalesAnalysisService analysisService;
    private Scanner scanner;
    
    /**
     * Main entry point for the application.
     * 
     * @param args Command line arguments (optional CSV file path)
     */
    public static void main(String[] args) {
        String csvFile = args.length > 0 ? args[0] : DEFAULT_CSV_FILE;
        
        Application app = new Application();
        app.initialize(csvFile);
        app.run();
    }
    
    /**
     * Initializes the application by loading data and setting up services.
     * 
     * @param csvFile Path to the CSV data file
     */
    private void initialize(String csvFile) {
        scanner = new Scanner(System.in);
        
        System.out.println("E-Commerce Sales Data Analysis System");
        System.out.println("====================================");
        System.out.println();
        
        try {
            System.out.println("Loading sales data from: " + csvFile);
            
            // Load data using CSV loader
            List<SalesRecord> salesData = CsvDataLoader.loadSalesData(csvFile);
            System.out.println("Successfully loaded " + salesData.size() + " sales records");
            
            // Initialize repository with loaded data
            salesRepository = new InMemorySalesRepository(salesData);
            System.out.println("Repository initialized with data");
            
            // Initialize analysis service
            analysisService = new SalesAnalysisService(salesRepository);
            System.out.println("Analysis service ready");
            
            // Display data overview
            displayDataOverview();
            
        } catch (CsvDataLoader.CsvLoadingException e) {
            System.err.println("Failed to load CSV data: " + e.getMessage());
            System.err.println("Please ensure the CSV file exists and is properly formatted.");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Initialization failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Runs the main application loop with menu interface.
     */
    private void run() {
        boolean running = true;
        
        while (running) {
            try {
                displayMainMenu();
                int choice = getMenuChoice(1, 10);
                
                switch (choice) {
                    case 1:
                        showOverallSummary();
                        break;
                    case 2:
                        showRevenueByCategory();
                        break;
                    case 3:
                        showTopProducts();
                        break;
                    case 4:
                        showSeasonalTrends();
                        break;
                    case 5:
                        showRegionalPerformance();
                        break;
                    case 6:
                        showHighValueOrders();
                        break;
                    case 7:
                        showPriceDistribution();
                        break;
                    case 8:
                        showLowTurnoverProducts();
                        break;
                    case 9:
                        showDataStatistics();
                        break;
                    case 10:
                        System.out.println("Thank you for using the Sales Analysis System!");
                        running = false;
                        break;
                }
                
                if (running) {
                    promptContinue();
                }
                
            } catch (Exception e) {
                System.err.println("Error occurred: " + e.getMessage());
                promptContinue();
            }
        }
        
        scanner.close();
    }
    
    /**
     * Displays the main menu options.
     */
    private void displayMainMenu() {
        System.out.println();
        System.out.println("=== Sales Data Analysis Menu ===");
        System.out.println("1. Overall Summary Statistics");
        System.out.println("2. Revenue by Category");
        System.out.println("3. Top Products by Revenue");
        System.out.println("4. Seasonal Trends Analysis");
        System.out.println("5. Regional Performance");
        System.out.println("6. High Value Orders");
        System.out.println("7. Price Distribution");
        System.out.println("8. Low Turnover Products");
        System.out.println("9. Repository Statistics");
        System.out.println("10. Exit");
        System.out.println();
        System.out.print("Please select an option (1-10): ");
    }
    
    /**
     * Displays basic data overview information.
     */
    private void displayDataOverview() {
        System.out.println();
        System.out.println("=== Data Overview ===");
        
        long totalRecords = salesRepository.count();
        List<String> categories = salesRepository.findDistinctCategories();
        List<String> regions = salesRepository.findDistinctRegions();
        
        System.out.println("Total Records: " + totalRecords);
        System.out.println("Product Categories: " + categories.size() + " (" + String.join(", ", categories) + ")");
        System.out.println("Customer Regions: " + regions.size() + " (" + String.join(", ", regions) + ")");
        
        // Find date range
        List<SalesRecord> allRecords = salesRepository.findAll();
        if (!allRecords.isEmpty()) {
            LocalDate minDate = allRecords.stream()
                    .map(SalesRecord::getOrderDate)
                    .min(LocalDate::compareTo)
                    .orElse(null);
            
            LocalDate maxDate = allRecords.stream()
                    .map(SalesRecord::getOrderDate)
                    .max(LocalDate::compareTo)
                    .orElse(null);
            
            System.out.println("Date Range: " + minDate + " to " + maxDate);
        }
        
        System.out.println();
    }
    
    /**
     * Shows overall summary statistics.
     */
    private void showOverallSummary() {
        System.out.println();
        System.out.println("=== Overall Summary Statistics ===");
        
        SalesAnalysisService.SalesSummary summary = analysisService.getOverallSummary();
        
        System.out.printf("Total Orders: %,d%n", summary.getTotalOrders());
        System.out.printf("Total Revenue: $%,.2f%n", summary.getTotalRevenue());
        System.out.printf("Average Order Value: $%.2f%n", summary.getAverageOrderValue());
        System.out.printf("Average Product Price: $%.2f%n", summary.getAveragePrice());
        System.out.printf("Highest Order Value: $%.2f%n", summary.getMaxOrderValue());
        System.out.printf("Total Items Sold: %,d%n", summary.getTotalQuantity());
        System.out.printf("Unique Products: %d%n", summary.getUniqueProducts());
        System.out.printf("Product Categories: %d%n", summary.getUniqueCategories());
        System.out.printf("Customer Regions: %d%n", summary.getUniqueRegions());
    }
    
    /**
     * Shows revenue analysis by product category.
     */
    private void showRevenueByCategory() {
        System.out.println();
        System.out.println("=== Revenue by Category ===");
        
        Map<String, Double> revenueByCategory = analysisService.getRevenueByCategory();
        
        if (revenueByCategory.isEmpty()) {
            System.out.println("No data available for analysis.");
            return;
        }
        
        double totalRevenue = revenueByCategory.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        
        System.out.printf("%-20s %15s %10s%n", "Category", "Revenue", "% of Total");
        System.out.println("-".repeat(50));
        
        revenueByCategory.forEach((category, revenue) -> {
            double percentage = (revenue / totalRevenue) * 100;
            System.out.printf("%-20s $%,13.2f %8.1f%%%n", category, revenue, percentage);
        });
        
        System.out.println("-".repeat(50));
        System.out.printf("%-20s $%,13.2f %8.1f%%%n", "TOTAL", totalRevenue, 100.0);
    }
    
    /**
     * Shows top products by revenue.
     */
    private void showTopProducts() {
        System.out.print("Enter number of top products to display (default 10): ");
        String input = scanner.nextLine().trim();
        
        int limit;
        try {
            limit = input.isEmpty() ? 10 : ValidationUtils.validateLimit(Integer.parseInt(input), MAX_DISPLAY_ITEMS);
        } catch (Exception e) {
            System.out.println("Invalid input. Using default limit of 10.");
            limit = 10;
        }
        
        System.out.println();
        System.out.println("=== Top " + limit + " Products by Revenue ===");
        
        List<SalesAnalysisService.ProductSummary> topProducts = 
                analysisService.getTopProductsByRevenue(limit);
        
        if (topProducts.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        
        System.out.printf("%-30s %15s %8s %12s%n", "Product", "Total Revenue", "Orders", "Avg Order");
        System.out.println("-".repeat(70));
        
        for (int i = 0; i < topProducts.size(); i++) {
            SalesAnalysisService.ProductSummary product = topProducts.get(i);
            System.out.printf("%2d. %-27s $%,12.2f %7d $%9.2f%n",
                    i + 1,
                    truncateString(product.getProductName(), 27),
                    product.getTotalRevenue(),
                    product.getOrderCount(),
                    product.getAverageOrderValue());
        }
    }
    
    /**
     * Shows seasonal trends analysis.
     */
    private void showSeasonalTrends() {
        System.out.println();
        System.out.println("=== Seasonal Trends Analysis ===");
        
        Map<String, SalesAnalysisService.SeasonalStats> seasonalTrends = 
                analysisService.getSeasonalTrends();
        
        if (seasonalTrends.isEmpty()) {
            System.out.println("No seasonal data available.");
            return;
        }
        
        System.out.printf("%-10s %15s %8s %12s %10s%n", "Month", "Revenue", "Orders", "Avg Order", "Products");
        System.out.println("-".repeat(60));
        
        seasonalTrends.forEach((month, stats) -> {
            System.out.printf("%-10s $%,12.2f %7d $%9.2f %8d%n",
                    month.substring(0, 3), // Abbreviate month name
                    stats.getTotalRevenue(),
                    stats.getTotalOrders(),
                    stats.getAverageOrderValue(),
                    stats.getUniqueProducts());
        });
    }
    
    /**
     * Shows regional performance analysis.
     */
    private void showRegionalPerformance() {
        System.out.println();
        System.out.println("=== Regional Performance Analysis ===");
        
        Map<String, SalesAnalysisService.RegionalStats> regionalStats = 
                analysisService.getRegionalPerformance();
        
        if (regionalStats.isEmpty()) {
            System.out.println("No regional data available.");
            return;
        }
        
        System.out.printf("%-15s %15s %8s %12s %-15s%n", "Region", "Revenue", "Orders", "Avg Order", "Top Category");
        System.out.println("-".repeat(75));
        
        regionalStats.forEach((region, stats) -> {
            System.out.printf("%-15s $%,12.2f %7d $%9.2f %-15s%n",
                    truncateString(region, 15),
                    stats.getTotalRevenue(),
                    stats.getOrderCount(),
                    stats.getAverageRevenue(),
                    truncateString(stats.getMostPopularCategory(), 15));
        });
    }
    
    /**
     * Shows high value orders above a threshold.
     */
    private void showHighValueOrders() {
        System.out.print("Enter minimum order value threshold (default $500): ");
        String input = scanner.nextLine().trim();
        
        double threshold;
        try {
            threshold = input.isEmpty() ? 500.0 : ValidationUtils.validateNonNegativeDouble(Double.parseDouble(input), "Threshold");
        } catch (Exception e) {
            System.out.println("Invalid input. Using default threshold of $500.");
            threshold = 500.0;
        }
        
        System.out.println();
        System.out.println("=== High Value Orders (above $" + String.format("%.2f", threshold) + ") ===");
        
        List<SalesRecord> highValueOrders = analysisService.getHighValueOrders(threshold);
        
        if (highValueOrders.isEmpty()) {
            System.out.println("No high value orders found above the threshold.");
            return;
        }
        
        System.out.printf("%-10s %-25s %12s %8s %12s%n", "Order ID", "Product", "Price", "Qty", "Total");
        System.out.println("-".repeat(70));
        
        int displayed = 0;
        for (SalesRecord order : highValueOrders) {
            if (displayed >= MAX_DISPLAY_ITEMS) {
                System.out.println("... and " + (highValueOrders.size() - displayed) + " more orders");
                break;
            }
            
            System.out.printf("%-10s %-25s $%9.2f %7d $%9.2f%n",
                    order.getOrderId(),
                    truncateString(order.getProductName(), 25),
                    order.getPrice(),
                    order.getQuantity(),
                    order.getTotalRevenue());
            displayed++;
        }
        
        System.out.println();
        System.out.println("Total high value orders: " + highValueOrders.size());
    }
    
    /**
     * Shows price distribution analysis.
     */
    private void showPriceDistribution() {
        System.out.println();
        System.out.println("=== Price Distribution ===");
        
        Map<String, Long> priceDistribution = analysisService.getPriceDistribution();
        
        if (priceDistribution.isEmpty()) {
            System.out.println("No price data available.");
            return;
        }
        
        long totalProducts = priceDistribution.values().stream()
                .mapToLong(Long::longValue)
                .sum();
        
        System.out.printf("%-15s %10s %12s%n", "Price Range", "Count", "Percentage");
        System.out.println("-".repeat(40));
        
        priceDistribution.forEach((range, count) -> {
            double percentage = (count * 100.0) / totalProducts;
            System.out.printf("%-15s %,9d %10.1f%%%n", range, count, percentage);
        });
        
        System.out.println("-".repeat(40));
        System.out.printf("%-15s %,9d %10.1f%%%n", "TOTAL", totalProducts, 100.0);
    }
    
    /**
     * Shows low turnover products analysis.
     */
    private void showLowTurnoverProducts() {
        System.out.print("Enter maximum quantity threshold for low turnover (default 3): ");
        String input = scanner.nextLine().trim();
        
        int threshold;
        try {
            threshold = input.isEmpty() ? 3 : ValidationUtils.validatePositiveInteger(Integer.parseInt(input), "Threshold");
        } catch (Exception e) {
            System.out.println("Invalid input. Using default threshold of 3.");
            threshold = 3;
        }
        
        System.out.println();
        System.out.println("=== Low Turnover Products (≤" + threshold + " units sold) ===");
        
        List<SalesAnalysisService.ProductTurnover> lowTurnoverProducts = 
                analysisService.getLowTurnoverProducts(threshold);
        
        if (lowTurnoverProducts.isEmpty()) {
            System.out.println("No low turnover products found with the specified threshold.");
            return;
        }
        
        System.out.printf("%-25s %-15s %8s %12s %8s%n", "Product", "Category", "Qty Sold", "Revenue", "Orders");
        System.out.println("-".repeat(75));
        
        for (SalesAnalysisService.ProductTurnover product : lowTurnoverProducts) {
            System.out.printf("%-25s %-15s %7d $%9.2f %7d%n",
                    truncateString(product.getProductName(), 25),
                    truncateString(product.getCategory(), 15),
                    product.getTotalQuantitySold(),
                    product.getTotalRevenue(),
                    product.getOrderCount());
        }
        
        System.out.println();
        System.out.println("Total low turnover products: " + lowTurnoverProducts.size());
    }
    
    /**
     * Shows repository statistics for debugging.
     */
    private void showDataStatistics() {
        System.out.println();
        System.out.println("=== Repository Statistics ===");
        
        if (salesRepository instanceof InMemorySalesRepository) {
            InMemorySalesRepository inMemoryRepo = (InMemorySalesRepository) salesRepository;
            System.out.println(inMemoryRepo.getStatistics());
        } else {
            System.out.println("Statistics not available for this repository type.");
        }
        
        System.out.println();
        System.out.println("Available Categories: " + 
                String.join(", ", salesRepository.findDistinctCategories()));
        System.out.println("Available Regions: " + 
                String.join(", ", salesRepository.findDistinctRegions()));
    }
    
    // Utility methods
    
    /**
     * Gets a menu choice from the user within the specified range.
     */
    private int getMenuChoice(int min, int max) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    continue;
                }
                
                int choice = Integer.parseInt(input);
                if (choice >= min && choice <= max) {
                    return choice;
                }
                
                System.out.printf("Please enter a number between %d and %d: ", min, max);
            } catch (NumberFormatException e) {
                System.out.printf("Please enter a valid number between %d and %d: ", min, max);
            }
        }
    }
    
    /**
     * Prompts the user to continue.
     */
    private void promptContinue() {
        System.out.println();
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Truncates a string to the specified length with ellipsis if needed.
     */
    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
}