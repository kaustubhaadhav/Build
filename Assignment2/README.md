# E-Commerce Sales Data Analysis System

A comprehensive Java application demonstrating advanced Stream API usage, functional programming paradigms, and data analysis capabilities on e-commerce sales data.

## Project Overview

This application analyzes e-commerce sales data using Java 11+ Stream operations, lambda expressions, and functional programming techniques. It implements a clean Service-Repository architecture with comprehensive data analysis capabilities.

### Key Features

- **Stream API Mastery**: Extensive use of grouping, filtering, mapping, reducing, and statistical operations
- **Functional Programming**: Lambda expressions, method references, and functional interfaces
- **Clean Architecture**: Service-Repository pattern with clear separation of concerns
- **Data Analysis**: Revenue analysis, seasonal trends, regional performance, and product insights
- **Robust Validation**: Comprehensive input validation and error handling
- **Test Coverage**: Unit tests demonstrating TDD principles
- **CSV Processing**: Flexible CSV data loading with error recovery

## Architecture

### Project Structure

```
Assign2/
├── src/com/ecommerce/           # Source code
│   ├── model/                   # Data models
│   │   └── SalesRecord.java     # Immutable sales record with validation
│   ├── repository/              # Data access layer
│   │   ├── SalesRepository.java # Repository interface
│   │   └── InMemorySalesRepository.java # In-memory implementation
│   ├── service/                 # Business logic
│   │   ├── SalesAnalysisService.java # Stream-based analysis methods
│   │   └── CsvDataLoader.java   # CSV processing utility
│   ├── util/                    # Utilities
│   │   └── ValidationUtils.java # Input validation helpers
│   └── Application.java         # Main console application
├── test/com/ecommerce/         # Test code
│   ├── model/                  # Model tests
│   └── service/                # Service tests
├── bin/                        # Compiled classes (generated)
├── sales_data.csv             # Sample dataset (120 records)
├── Makefile                   # Build automation
└── README.md                  # This file
```

### Component Design

#### 1. Model Layer (`SalesRecord`)
- **Immutable Design**: Thread-safe with defensive copying
- **Validation**: Comprehensive input validation with meaningful error messages
- **Calculated Methods**: `getTotalRevenue()`, `getOrderQuarter()`, temporal accessors
- **Two Constructors**: Direct instantiation and CSV parsing

#### 2. Repository Layer
- **Interface**: `SalesRepository` - Contract for data access
- **Implementation**: `InMemorySalesRepository` - Thread-safe, stream-optimized
- **Query Methods**: Category, region, date range, price range filtering
- **Swappable Design**: Easy to replace with database implementation

#### 3. Service Layer (`SalesAnalysisService`)
- **Stream Operations**: Demonstrates all major Stream API patterns
- **Analysis Methods**: Revenue analysis, trending, performance metrics
- **Functional Programming**: Heavy use of collectors, lambda expressions
- **Statistical Operations**: Summary statistics, aggregations, transformations

#### 4. Application Layer
- **Console Interface**: Interactive menu system
- **Error Handling**: Graceful degradation and user-friendly messages
- **Data Loading**: Flexible CSV file processing
- **Results Display**: Formatted output with statistics

## Stream Operations Demonstrated

### Grouping Operations
```java
// Revenue by category
records.stream()
    .collect(Collectors.groupingBy(
        SalesRecord::getCategory,
        Collectors.summingDouble(SalesRecord::getTotalRevenue)
    ))
```

### Statistical Operations  
```java
// Regional performance with statistics
records.stream()
    .collect(Collectors.groupingBy(
        SalesRecord::getCustomerRegion,
        Collectors.summarizingDouble(SalesRecord::getTotalRevenue)
    ))
```

### Complex Aggregations
```java
// Seasonal trends with multiple metrics
records.stream()
    .collect(Collectors.groupingBy(
        record -> Month.of(record.getOrderMonth()).name(),
        Collectors.collectingAndThen(
            Collectors.toList(),
            monthRecords -> new SeasonalStats(...)
        )
    ))
```

### Filtering and Sorting
```java
// Top products by revenue
records.stream()
    .collect(Collectors.groupingBy(...))
    .entrySet().stream()
    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
    .limit(limit)
    .collect(Collectors.toList())
```

## Setup Instructions

### Prerequisites

- **Java 11+** (OpenJDK or Oracle JDK)
- **Make** (for build automation)
- **Git** (for version control)

### Quick Start

1. **Clone and Navigate**
   ```bash
   cd Assign2
   ```

2. **Build the Project**
   ```bash
   make all
   ```

3. **Run the Application**
   ```bash
   make run
   ```

### Build Commands

| Command | Description |
|---------|-------------|
| `make all` | Complete build and test (default) |
| `make build` | Compile all source files |
| `make test` | Run comprehensive tests |
| `make run` | Execute the main application |
| `make clean` | Remove build artifacts |
| `make help` | Show all available commands |

### Advanced Setup

#### JUnit 5 Integration (Optional)
```bash
# Download JUnit 5 dependencies
make setup-junit

# Run enhanced tests
make test-junit
```

#### Custom Data Files
```bash
# Run with your own CSV file
make run-with-file FILE=path/to/your/data.csv
```

#### Documentation Generation
```bash
# Generate Javadoc
make docs
```

## Sample Data

The included `sales_data.csv` contains 120 realistic e-commerce records with:

- **Products**: Electronics, Apparel, Home & Kitchen, Sports, Beauty, Accessories
- **Regions**: North America, Europe, Asia
- **Date Range**: Full year 2023 data
- **Price Range**: $9.99 to $999.99
- **Variety**: Different quantities, seasonal patterns, regional preferences

### CSV Format
```csv
orderId,productName,category,price,quantity,orderDate,customerRegion
ORD001,Wireless Bluetooth Headphones,Electronics,99.99,2,2023-01-15,North America
ORD002,Running Shoes,Apparel,129.99,1,2023-01-16,Europe
...
```

## Testing

### Test Strategy

The project demonstrates **Test-Driven Development (TDD)** principles with comprehensive **JUnit 5** test coverage:

#### Unit Tests (`SalesRecordTest`)
- **Validation Logic**: All input validation scenarios with edge cases
- **Object Methods**: equals(), hashCode(), toString() contract verification
- **Calculated Methods**: Revenue calculation, date extraction, quarter logic
- **Boundary Testing**: Extreme values, null inputs, whitespace handling
- **String Parsing**: Date/number parsing with error validation

#### Service Tests (`SalesAnalysisServiceTest`)
- **Analysis Methods**: All Stream-based analysis operations with real data
- **Edge Cases**: Empty datasets, single records, null inputs, parameter validation
- **Stream Operations**: Complex grouping, filtering, mapping, and reducing
- **Functional Programming**: Lambda expressions and method reference usage
- **Data Aggregation**: Multi-level grouping and statistical calculations

#### CSV Loading Tests (`CsvDataLoaderTest`)
- **Valid CSV Processing**: Correct parsing of well-formed CSV files
- **Error Handling**: Invalid paths, malformed data, missing files
- **Edge Cases**: Empty files, header-only files, wrong column counts
- **Data Validation**: Field trimming, line skipping, format validation
- **Stream Implementation**: Alternative stream-based loading approach

#### Validation Tests (`ValidationUtilsTest`)
- **String Validation**: Null, empty, whitespace-only input handling
- **Numeric Validation**: Positive, non-negative, boundary value testing
- **Date/Range Validation**: Date parsing, range validation logic
- **Collection Validation**: Empty collection detection and null safety
- **Error Message Sanitization**: Sensitive information filtering

#### Integration Tests (`SimpleIntegrationTest`)
- **End-to-End Workflows**: Complete CSV loading and analysis chains
- **Component Integration**: Service-Repository-Model interaction testing
- **Real Data Processing**: Actual CSV file processing verification
- **Cross-Component Validation**: Data flow between layers

### Running Tests

```bash
# JUnit 5 test suite (recommended)
make test-junit

# Basic test suite (legacy manual tests)
make test

# Setup JUnit 5 dependencies
make setup-junit

# Performance testing
make test-performance

# Code structure validation
make validate
```

### Test Coverage Summary

- **Total Tests**: 115 comprehensive JUnit 5 tests
- **Test Classes**: 5 dedicated test classes covering all components
- **Coverage Areas**:
  - Model validation and business logic
  - Service layer with complex Stream operations
  - CSV data loading and error handling
  - Utility functions and edge cases
  - Integration workflows and data flow

### Test Results Interpretation

The JUnit 5 test output provides:
- **Hierarchical Display**: Organized by test class and method
- **Descriptive Names**: Clear test descriptions using `@DisplayName`
- **Detailed Failures**: Precise error messages and stack traces
- **Execution Metrics**: Test timing and performance indicators
- **Coverage Statistics**: Pass/fail ratios and test counts

## Usage Examples

### Interactive Menu System

When you run `make run`, you'll see:

```
E-Commerce Sales Data Analysis System
====================================

Loading sales data from: sales_data.csv
Successfully loaded 120 sales records

=== Sales Data Analysis Menu ===
1. Overall Summary Statistics
2. Revenue by Category  
3. Top Products by Revenue
4. Seasonal Trends Analysis
5. Regional Performance
6. High Value Orders
7. Price Distribution
8. Low Turnover Products
9. Repository Statistics
10. Exit
```

### Analysis Outputs

#### Overall Summary
```
Total Orders: 120
Total Revenue: $25,847.60
Average Order Value: $215.40
Unique Products: 120
Product Categories: 6
Customer Regions: 3
```

#### Revenue by Category
```
Category             Revenue    % of Total
Electronics        $8,249.89        31.9%
Apparel           $6,127.42        23.7%
Home & Kitchen    $5,891.23        22.8%
...
```

#### Regional Performance  
```
Region          Revenue   Orders  Avg Order  Top Category
North America  $9,248.91      42   $220.21   Electronics
Europe         $8,129.45      38   $213.93   Apparel
Asia           $8,469.24      40   $211.73   Home & Kitchen
```

## Customization

### Adding New Analysis Methods

1. **Extend SalesAnalysisService**:
   ```java
   public Map<String, Double> getCustomAnalysis() {
       return repository.findAll().stream()
           .collect(/* your custom collector */);
   }
   ```

2. **Add Menu Option**: Update `Application.java` menu system

3. **Add Tests**: Create corresponding test methods

### Custom Data Sources

1. **Implement SalesRepository**: Create new repository implementation
2. **Update Application**: Inject your repository implementation
3. **Maintain Interface**: All existing functionality continues to work

### CSV Format Extensions

1. **Extend SalesRecord**: Add new fields with validation
2. **Update CsvDataLoader**: Handle additional columns
3. **Update Analysis**: Incorporate new fields in Stream operations

## Assignment Requirements Compliance

### Code Structure
- [x] **Clear MVC Pattern**: Model-View-Controller with Service-Repository layers
- [x] **Separation of Concerns**: Each class has single responsibility
- [x] **Not Everything in One File**: 10+ classes across 4 packages

### Functionality
- [x] **Compiles Successfully**: `make build` works without errors
- [x] **Main API Works**: `make run` executes the application
- [x] **Happy Path Priority**: Core functionality works reliably

### Testing
- [x] **Critical Testing**: Comprehensive unit test suite
- [x] **TDD Demonstrated**: Tests for all analysis methods
- [x] **1-2 Unit Tests**: Multiple test classes with extensive coverage

### Data Handling
- [x] **In-Memory Database**: InMemorySalesRepository with Collections
- [x] **Repository Pattern**: Swappable data source design
- [x] **Data Source Abstraction**: Interface-based design

### Input Validation
- [x] **Edge Cases Handled**: Null inputs, negative values, empty collections
- [x] **Comprehensive Validation**: ValidationUtils with meaningful errors
- [x] **Graceful Degradation**: Application continues with invalid inputs

### Functional Programming
- [x] **Stream Operations**: Extensive use throughout analysis methods  
- [x] **Data Aggregation**: groupingBy, summingDouble, reducing operations
- [x] **Lambda Expressions**: Used extensively in Stream operations
- [x] **Method References**: Constructor and static method references

## Performance Characteristics

### Stream Operation Efficiency
- **Lazy Evaluation**: Streams process data only when terminal operations execute
- **Parallel Processing**: Can be enhanced with `.parallelStream()` for large datasets
- **Memory Efficient**: Stream operations don't create intermediate collections

### Scalability Considerations
- **Memory Usage**: Current implementation holds all data in memory
- **Database Ready**: Repository pattern allows easy database integration
- **Concurrent Safe**: InMemorySalesRepository uses ConcurrentHashMap

### Optimization Opportunities
- **Caching**: Results can be cached for repeated analysis
- **Streaming CSV**: Large files can be processed with streaming
- **Parallel Streams**: Multi-core processing for complex aggregations

## Troubleshooting

### Common Issues

#### Java Version
```bash
# Check Java version
java -version

# Should be Java 11+
```

#### Build Failures
```bash
# Clean rebuild
make clean && make all

# Check for compilation errors
make build
```

#### Test Failures  
```bash
# Run basic validation
make test-basic

# Check test output for specific failures
make test
```

#### Data File Issues
```bash
# Verify CSV file exists
ls -la sales_data.csv

# Check CSV format
head -3 sales_data.csv
```

### Getting Help

1. **Run Diagnostics**: `make validate`
2. **Check Documentation**: `make docs` (if javadoc available)
3. **Review Build Output**: Look for specific error messages
4. **Test Individual Components**: Use menu options to isolate issues

## Learning Outcomes

This project demonstrates mastery of:

### Java Stream API
- **Collectors**: groupingBy, summingDouble, toList, toMap
- **Statistical Operations**: summarizingDouble, counting, averaging  
- **Complex Pipelines**: Multi-stage stream processing
- **Performance Optimization**: Efficient stream usage patterns

### Functional Programming
- **Lambda Expressions**: Concise function definitions
- **Method References**: Constructor and static method references
- **Function Composition**: Building complex operations from simple functions
- **Immutable Design**: Thread-safe, predictable object behavior

### Software Architecture
- **Design Patterns**: Repository, Service Layer, Strategy
- **SOLID Principles**: Single responsibility, Open/closed, Dependency inversion
- **Clean Code**: Readable, maintainable, well-documented code
- **Test-Driven Development**: Tests drive design and implementation

---

## License

This project is created for educational purposes as part of a Java programming assignment demonstrating Stream API proficiency and functional programming paradigms.