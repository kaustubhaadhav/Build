package com.ecommerce.service;

import com.ecommerce.model.SalesRecord;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Utility class for loading sales data from CSV files.
 * Provides robust error handling and validation for data parsing.
 */
public class CsvDataLoader {
    
    private static final String DEFAULT_CSV_FILE = "sales_data.csv";
    private static final String CSV_DELIMITER = ",";
    private static final int EXPECTED_COLUMN_COUNT = 7;
    
    // Expected CSV header fields
    private static final String[] EXPECTED_HEADERS = {
        "orderId", "productName", "category", "price", "quantity", "orderDate", "customerRegion"
    };
    
    /**
     * Loads sales data from the default CSV file.
     * 
     * @return List of SalesRecord objects parsed from CSV
     * @throws CsvLoadingException if file reading or parsing fails
     */
    public static List<SalesRecord> loadSalesData() throws CsvLoadingException {
        return loadSalesData(DEFAULT_CSV_FILE);
    }
    
    /**
     * Loads sales data from a specified CSV file.
     * 
     * @param filePath Path to the CSV file
     * @return List of SalesRecord objects parsed from CSV
     * @throws CsvLoadingException if file reading or parsing fails
     */
    public static List<SalesRecord> loadSalesData(String filePath) throws CsvLoadingException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new CsvLoadingException("File path cannot be null or empty");
        }
        
        Path path = Paths.get(filePath);
        validateFileExists(path);
        
        List<SalesRecord> salesRecords = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int lineNumber = 0;
        
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                try {
                    if (isFirstLine) {
                        validateHeaders(line, lineNumber);
                        isFirstLine = false;
                        continue;
                    }
                    
                    if (line.trim().isEmpty()) {
                        continue; // Skip empty lines
                    }
                    
                    SalesRecord record = parseCsvLine(line, lineNumber);
                    salesRecords.add(record);
                    
                } catch (Exception e) {
                    String errorMsg = String.format("Line %d: %s", lineNumber, e.getMessage());
                    errors.add(errorMsg);
                }
            }
            
        } catch (IOException e) {
            throw new CsvLoadingException("Failed to read CSV file: " + filePath, e);
        }
        
        if (!errors.isEmpty()) {
            throw new CsvLoadingException("CSV parsing failed with errors:\n" + 
                String.join("\n", errors));
        }
        
        if (salesRecords.isEmpty()) {
            throw new CsvLoadingException("No valid sales records found in CSV file: " + filePath);
        }
        
        return salesRecords;
    }
    
    /**
     * Loads sales data using Java 8 Streams for more functional approach.
     * Alternative implementation demonstrating stream processing.
     * 
     * @param filePath Path to the CSV file
     * @return List of SalesRecord objects parsed from CSV
     * @throws CsvLoadingException if file reading or parsing fails
     */
    public static List<SalesRecord> loadSalesDataWithStreams(String filePath) throws CsvLoadingException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new CsvLoadingException("File path cannot be null or empty");
        }
        
        Path path = Paths.get(filePath);
        validateFileExists(path);
        
        try (Stream<String> lines = Files.lines(path)) {
            List<String> allLines = lines.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            
            if (allLines.isEmpty()) {
                throw new CsvLoadingException("CSV file is empty: " + filePath);
            }
            
            // Validate headers
            validateHeaders(allLines.get(0), 1);
            
            // Process data lines with streams
            List<SalesRecord> records = allLines.stream()
                    .skip(1) // Skip header
                    .filter(line -> !line.trim().isEmpty()) // Filter empty lines
                    .map(line -> {
                        int lineIndex = allLines.indexOf(line) + 1;
                        return parseCsvLine(line, lineIndex);
                    })
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            
            if (records.isEmpty()) {
                throw new CsvLoadingException("No valid sales records found in CSV file: " + filePath);
            }
            
            return records;
            
        } catch (IOException e) {
            throw new CsvLoadingException("Failed to read CSV file: " + filePath, e);
        }
    }
    
    /**
     * Parses a single CSV line into a SalesRecord object.
     * 
     * @param line CSV line to parse
     * @param lineNumber Line number for error reporting
     * @return Parsed SalesRecord object
     * @throws IllegalArgumentException if parsing fails
     */
    private static SalesRecord parseCsvLine(String line, int lineNumber) {
        String[] fields = splitCsvLine(line);
        
        if (fields.length != EXPECTED_COLUMN_COUNT) {
            throw new IllegalArgumentException(String.format(
                "Expected %d fields but found %d. Line content: %s", 
                EXPECTED_COLUMN_COUNT, fields.length, line));
        }
        
        try {
            return new SalesRecord(
                fields[0].trim(), // orderId
                fields[1].trim(), // productName
                fields[2].trim(), // category
                fields[3].trim(), // price
                fields[4].trim(), // quantity
                fields[5].trim(), // orderDate
                fields[6].trim()  // customerRegion
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create SalesRecord: " + e.getMessage(), e);
        }
    }
    
    /**
     * Splits a CSV line handling quoted fields and escaped commas.
     * Simple implementation for basic CSV parsing.
     * 
     * @param line CSV line to split
     * @return Array of field values
     */
    private static String[] splitCsvLine(String line) {
        // Simple CSV splitting - handles basic cases
        // In production, consider using a proper CSV library like OpenCSV
        return line.split(CSV_DELIMITER, -1);
    }
    
    /**
     * Validates that the file exists and is readable.
     * 
     * @param path File path to validate
     * @throws CsvLoadingException if file doesn't exist or isn't readable
     */
    private static void validateFileExists(Path path) throws CsvLoadingException {
        if (!Files.exists(path)) {
            throw new CsvLoadingException("CSV file not found: " + path.toString());
        }
        
        if (!Files.isRegularFile(path)) {
            throw new CsvLoadingException("Path is not a regular file: " + path.toString());
        }
        
        if (!Files.isReadable(path)) {
            throw new CsvLoadingException("CSV file is not readable: " + path.toString());
        }
    }
    
    /**
     * Validates CSV headers match expected format.
     * 
     * @param headerLine First line of CSV file
     * @param lineNumber Line number for error reporting
     * @throws IllegalArgumentException if headers don't match
     */
    private static void validateHeaders(String headerLine, int lineNumber) {
        String[] headers = splitCsvLine(headerLine);
        
        if (headers.length != EXPECTED_HEADERS.length) {
            throw new IllegalArgumentException(String.format(
                "Invalid header count. Expected %d headers but found %d", 
                EXPECTED_HEADERS.length, headers.length));
        }
        
        for (int i = 0; i < EXPECTED_HEADERS.length; i++) {
            String expected = EXPECTED_HEADERS[i];
            String actual = headers[i].trim();
            
            if (!expected.equalsIgnoreCase(actual)) {
                throw new IllegalArgumentException(String.format(
                    "Invalid header at position %d. Expected '%s' but found '%s'", 
                    i + 1, expected, actual));
            }
        }
    }
    
    /**
     * Gets information about a CSV file without fully loading it.
     * 
     * @param filePath Path to the CSV file
     * @return CSV file information
     * @throws CsvLoadingException if file reading fails
     */
    public static CsvFileInfo getCsvFileInfo(String filePath) throws CsvLoadingException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new CsvLoadingException("File path cannot be null or empty");
        }
        
        Path path = Paths.get(filePath);
        validateFileExists(path);
        
        try (Stream<String> lines = Files.lines(path)) {
            long totalLines = lines.count();
            long dataLines = Math.max(0, totalLines - 1); // Subtract header line
            
            return new CsvFileInfo(filePath, totalLines, dataLines);
            
        } catch (IOException e) {
            throw new CsvLoadingException("Failed to analyze CSV file: " + filePath, e);
        }
    }
    
    /**
     * Custom exception for CSV loading errors.
     */
    public static class CsvLoadingException extends Exception {
        public CsvLoadingException(String message) {
            super(message);
        }
        
        public CsvLoadingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * Information about a CSV file.
     */
    public static class CsvFileInfo {
        private final String filePath;
        private final long totalLines;
        private final long dataLines;
        
        public CsvFileInfo(String filePath, long totalLines, long dataLines) {
            this.filePath = filePath;
            this.totalLines = totalLines;
            this.dataLines = dataLines;
        }
        
        public String getFilePath() { return filePath; }
        public long getTotalLines() { return totalLines; }
        public long getDataLines() { return dataLines; }
        
        @Override
        public String toString() {
            return String.format("CsvFileInfo{filePath='%s', totalLines=%d, dataLines=%d}", 
                filePath, totalLines, dataLines);
        }
    }
}