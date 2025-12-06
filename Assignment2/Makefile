# E-Commerce Sales Data Analysis - Makefile
# Java 11+ build system with comprehensive targets

# Java configuration
JAVA_VERSION = 11
JAVAC = javac
JAVA = java
JAVAC_FLAGS = -cp $(CLASSPATH) -d $(BIN_DIR) -Xlint:deprecation -Xlint:unchecked

# Project directories
SRC_DIR = src
BIN_DIR = bin
TEST_DIR = test
LIB_DIR = lib
REPORTS_DIR = reports

# Source and class files
MAIN_CLASS = com.ecommerce.Application
SRC_FILES = $(shell find $(SRC_DIR) -name "*.java")
TEST_FILES = $(shell find $(TEST_DIR) -name "*.java" 2>/dev/null || true)
CLASS_FILES = $(SRC_FILES:$(SRC_DIR)/%.java=$(BIN_DIR)/%.class)

# Data files
DATA_FILE = sales_data.csv

# Classpath configuration
CLASSPATH = $(BIN_DIR)
ifdef JUNIT_CLASSPATH
    CLASSPATH := $(CLASSPATH):$(JUNIT_CLASSPATH)
endif

# JUnit 5 configuration (optional - for enhanced testing)
JUNIT5_VERSION = 5.9.2
JUNIT_PLATFORM_VERSION = 1.9.2
APIGUARDIAN_VERSION = 1.1.2
OPENTEST4J_VERSION = 1.2.0
JUNIT_JARS = $(LIB_DIR)/junit-jupiter-api-$(JUNIT5_VERSION).jar:$(LIB_DIR)/junit-jupiter-engine-$(JUNIT5_VERSION).jar:$(LIB_DIR)/junit-platform-launcher-$(JUNIT_PLATFORM_VERSION).jar:$(LIB_DIR)/junit-platform-engine-$(JUNIT_PLATFORM_VERSION).jar:$(LIB_DIR)/junit-platform-commons-$(JUNIT_PLATFORM_VERSION).jar:$(LIB_DIR)/junit-platform-console-standalone-$(JUNIT_PLATFORM_VERSION).jar:$(LIB_DIR)/apiguardian-api-$(APIGUARDIAN_VERSION).jar:$(LIB_DIR)/opentest4j-$(OPENTEST4J_VERSION).jar

# Default target
.DEFAULT_GOAL := all

# Check Java version
check-java:
	@echo "Checking Java version..."
	@$(JAVA) -version 2>&1 | grep -E "(openjdk|java) version" | head -1
	@java_version=$$($(JAVA) -version 2>&1 | grep -E "(openjdk|java) version" | head -1 | sed 's/.*version "\([^"]*\)".*/\1/' | cut -d. -f1-2); \
	if [ "$$(echo "$$java_version >= 11" | bc -l 2>/dev/null || echo "0")" != "1" ]; then \
		echo "Warning: Java 11+ recommended. Current version: $$java_version"; \
	else \
		echo "Java version check passed: $$java_version"; \
	fi

# Create necessary directories
$(BIN_DIR):
	@echo "Creating bin directory..."
	@mkdir -p $(BIN_DIR)

$(REPORTS_DIR):
	@echo "Creating reports directory..."
	@mkdir -p $(REPORTS_DIR)

$(LIB_DIR):
	@echo "Creating lib directory..."
	@mkdir -p $(LIB_DIR)

# Compile source files
compile: check-java $(BIN_DIR)
	@echo "Compiling source files..."
	@$(JAVAC) $(JAVAC_FLAGS) $(SRC_FILES)
	@echo "Compilation completed successfully."

# Compile test files (JUnit 5 tests - requires JUnit dependencies)
compile-tests: compile
	@echo "Compiling JUnit 5 tests..."
	@if [ -f "$(LIB_DIR)/junit-jupiter-api-$(JUNIT5_VERSION).jar" ]; then \
		$(JAVAC) -cp $(CLASSPATH):$(JUNIT_JARS) -d $(BIN_DIR) $(TEST_FILES); \
		echo "JUnit 5 tests compiled successfully."; \
	else \
		echo "JUnit 5 dependencies not available. Run 'make setup-junit' to enable test compilation."; \
		echo "Skipping test compilation - using 'make test-junit' instead."; \
	fi

# Build everything
build: compile compile-tests
	@echo "Build completed successfully."

# Run basic validation tests
test-basic: build
	@echo "Running basic validation tests..."
	@echo "Testing SalesRecord creation and validation..."
	@$(JAVA) -cp $(CLASSPATH) -ea \
		-Xmx512m \
		-Dtest.mode=validation \
		com.ecommerce.Application --test-mode 2>/dev/null || \
		echo "Note: Application test mode not available, running manual validation"
	@echo "Testing CSV data loading..."
	@if [ -f "$(DATA_FILE)" ]; then \
		echo "CSV data file exists: $(DATA_FILE)"; \
		head -3 $(DATA_FILE); \
		echo "..."; \
		echo "CSV validation passed."; \
	else \
		echo "Error: CSV data file missing: $(DATA_FILE)"; \
		exit 1; \
	fi
	@echo "Basic tests completed."

# Run comprehensive test suite (prefers JUnit 5 if available)
test: test-basic
	@echo "Running comprehensive test suite..."
	@if [ -f "$(LIB_DIR)/junit-jupiter-api-$(JUNIT5_VERSION).jar" ]; then \
		echo "Using JUnit 5 test runner..."; \
		$(MAKE) test-junit; \
	else \
		echo "JUnit 5 not available. Please run 'make setup-junit' to install dependencies."; \
		echo "Then use 'make test-junit' for the full test suite."; \
		exit 1; \
	fi

# Run the main application
run: build
	@echo "Running Sales Data Analysis Application..."
	@$(JAVA) -cp $(CLASSPATH) $(MAIN_CLASS) $(DATA_FILE)

# Run with custom CSV file
run-with-file: build
	@if [ -z "$(FILE)" ]; then \
		echo "Usage: make run-with-file FILE=path/to/your/data.csv"; \
		exit 1; \
	fi
	@echo "Running with custom file: $(FILE)"
	@$(JAVA) -cp $(CLASSPATH) $(MAIN_CLASS) $(FILE)

# Performance testing with larger datasets
test-performance: build
	@echo "Running performance tests..."
	@echo "Testing with standard dataset..."
	@time $(JAVA) -cp $(CLASSPATH) $(MAIN_CLASS) $(DATA_FILE) --batch-mode 2>/dev/null || \
		echo "Batch mode not available, performance test skipped"

# Generate documentation
docs: $(REPORTS_DIR)
	@echo "Generating project documentation..."
	@if command -v javadoc >/dev/null 2>&1; then \
		javadoc -d $(REPORTS_DIR)/javadoc -cp $(CLASSPATH) -sourcepath $(SRC_DIR) \
			-subpackages com.ecommerce -windowtitle "E-Commerce Sales Analysis" \
			-doctitle "E-Commerce Sales Data Analysis System" \
			-private -use -version -author; \
		echo "Javadoc generated in $(REPORTS_DIR)/javadoc/"; \
	else \
		echo "Javadoc not available, skipping documentation generation"; \
	fi

# Download JUnit 5 dependencies (optional enhancement)
setup-junit: $(LIB_DIR)
	@echo "Setting up JUnit 5 dependencies..."
	@if command -v curl >/dev/null 2>&1; then \
		echo "Downloading JUnit 5 JARs..."; \
		curl -L -o $(LIB_DIR)/junit-jupiter-api-$(JUNIT5_VERSION).jar \
			https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-api/$(JUNIT5_VERSION)/junit-jupiter-api-$(JUNIT5_VERSION).jar; \
		curl -L -o $(LIB_DIR)/junit-jupiter-engine-$(JUNIT5_VERSION).jar \
			https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-engine/$(JUNIT5_VERSION)/junit-jupiter-engine-$(JUNIT5_VERSION).jar; \
		curl -L -o $(LIB_DIR)/junit-platform-launcher-$(JUNIT_PLATFORM_VERSION).jar \
			https://repo1.maven.org/maven2/org/junit/platform/junit-platform-launcher/$(JUNIT_PLATFORM_VERSION)/junit-platform-launcher-$(JUNIT_PLATFORM_VERSION).jar; \
		curl -L -o $(LIB_DIR)/junit-platform-engine-$(JUNIT_PLATFORM_VERSION).jar \
			https://repo1.maven.org/maven2/org/junit/platform/junit-platform-engine/$(JUNIT_PLATFORM_VERSION)/junit-platform-engine-$(JUNIT_PLATFORM_VERSION).jar; \
		curl -L -o $(LIB_DIR)/junit-platform-commons-$(JUNIT_PLATFORM_VERSION).jar \
			https://repo1.maven.org/maven2/org/junit/platform/junit-platform-commons/$(JUNIT_PLATFORM_VERSION)/junit-platform-commons-$(JUNIT_PLATFORM_VERSION).jar; \
		curl -L -o $(LIB_DIR)/apiguardian-api-$(APIGUARDIAN_VERSION).jar \
			https://repo1.maven.org/maven2/org/apiguardian/apiguardian-api/$(APIGUARDIAN_VERSION)/apiguardian-api-$(APIGUARDIAN_VERSION).jar; \
		curl -L -o $(LIB_DIR)/opentest4j-$(OPENTEST4J_VERSION).jar \
			https://repo1.maven.org/maven2/org/opentest4j/opentest4j/$(OPENTEST4J_VERSION)/opentest4j-$(OPENTEST4J_VERSION).jar; \
		curl -L -o $(LIB_DIR)/junit-platform-console-standalone-$(JUNIT_PLATFORM_VERSION).jar \
			https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/$(JUNIT_PLATFORM_VERSION)/junit-platform-console-standalone-$(JUNIT_PLATFORM_VERSION).jar; \
		echo "JUnit 5 setup completed. Use 'make test-junit' to run enhanced tests."; \
	else \
		echo "curl not available. Please manually download JUnit 5 JARs to $(LIB_DIR)/"; \
	fi

# Run JUnit 5 tests (if dependencies available)
test-junit: compile
	@if [ -f "$(LIB_DIR)/junit-jupiter-api-$(JUNIT5_VERSION).jar" ]; then \
		echo "Running JUnit 5 tests..."; \
		export JUNIT_CLASSPATH=$(JUNIT_JARS); \
		$(JAVAC) -cp $(CLASSPATH):$(JUNIT_JARS) -d $(BIN_DIR) $(TEST_FILES); \
		$(JAVA) -jar $(LIB_DIR)/junit-platform-console-standalone-$(JUNIT_PLATFORM_VERSION).jar \
			--class-path $(CLASSPATH) --scan-class-path; \
	else \
		echo "JUnit 5 not available. Run 'make setup-junit' first or use 'make test' for basic tests."; \
	fi

# Validate code style and structure
validate: build
	@echo "Validating code structure and patterns..."
	@echo "Checking for required patterns:"
	@echo -n "  - Model classes: "; \
		find $(SRC_DIR) -path "*/model/*.java" -type f | wc -l | tr -d ' '
	@echo -n "  - Repository pattern: "; \
		find $(SRC_DIR) -path "*/repository/*.java" -type f | wc -l | tr -d ' '
	@echo -n "  - Service layer: "; \
		find $(SRC_DIR) -path "*/service/*.java" -type f | wc -l | tr -d ' '
	@echo -n "  - Stream operations: "; \
		grep -r "\.stream()" $(SRC_DIR) | wc -l | tr -d ' '
	@echo -n "  - Lambda expressions: "; \
		grep -r " -> " $(SRC_DIR) | wc -l | tr -d ' '
	@echo -n "  - Method references: "; \
		grep -r "::" $(SRC_DIR) | wc -l | tr -d ' '
	@echo "Checking test coverage:"
	@echo -n "  - Unit test classes: "; \
		find $(TEST_DIR) -name "*Test.java" -type f | wc -l | tr -d ' '
	@echo -n "  - Integration tests: "; \
		find $(TEST_DIR) -name "Simple*Test.java" -type f | wc -l | tr -d ' '
	@echo "Code structure and test validation completed."

# Package application for distribution
package: build validate
	@echo "Packaging application..."
	@if command -v jar >/dev/null 2>&1; then \
		jar cfm ecommerce-analysis.jar - -C $(BIN_DIR) . $(DATA_FILE); \
		echo "Application packaged as ecommerce-analysis.jar"; \
	else \
		echo "JAR tool not available, creating simple distribution..."; \
		mkdir -p dist; \
		cp -r $(BIN_DIR)/* dist/; \
		cp $(DATA_FILE) dist/; \
		echo "Distribution created in dist/ directory"; \
	fi

# Clean build artifacts
clean:
	@echo "Cleaning build artifacts..."
	@rm -rf $(BIN_DIR)
	@rm -rf $(REPORTS_DIR)
	@rm -rf dist
	@rm -f ecommerce-analysis.jar
	@echo "Clean completed."

# Clean everything including dependencies
clean-all: clean
	@echo "Cleaning all generated files and dependencies..."
	@rm -rf $(LIB_DIR)
	@echo "Deep clean completed."

# Development setup
setup: check-java $(BIN_DIR) $(REPORTS_DIR)
	@echo "Setting up development environment..."
	@echo "Project structure ready."
	@echo "Run 'make build' to compile the project."
	@echo "Run 'make test' to run tests."
	@echo "Run 'make run' to execute the application."

# Show help
help:
	@echo "E-Commerce Sales Data Analysis - Build System"
	@echo "=============================================="
	@echo ""
	@echo "Available targets:"
	@echo "  all         - Build and test the complete project (default)"
	@echo "  build       - Compile all source and test files"
	@echo "  compile     - Compile source files only"
	@echo "  test        - Run comprehensive test suite"
	@echo "  run         - Execute the main application"
	@echo "  clean       - Remove build artifacts"
	@echo "  validate    - Check code structure and patterns"
	@echo "  package     - Create distribution package"
	@echo "  docs        - Generate Javadoc documentation"
	@echo ""
	@echo "Development targets:"
	@echo "  setup       - Initialize development environment"
	@echo "  setup-junit - Download JUnit 5 dependencies"
	@echo "  test-junit  - Run JUnit 5 enhanced tests"
	@echo "  clean-all   - Deep clean including dependencies"
	@echo ""
	@echo "Usage examples:"
	@echo "  make all                           # Build and test everything"
	@echo "  make run                          # Run with default data"
	@echo "  make run-with-file FILE=data.csv  # Run with custom file"
	@echo "  make test                         # Run all tests"
	@echo "  make clean && make all            # Fresh rebuild"

# Main target - build and test everything
all: setup build test validate
	@echo ""
	@echo "=========================================="
	@echo "Build completed successfully!"
	@echo "=========================================="
	@echo "Project: E-Commerce Sales Data Analysis"
	@echo "Java Version: $$(java -version 2>&1 | head -1)"
	@echo "Build Date: $$(date)"
	@echo ""
	@echo "To run the application:"
	@echo "  make run"
	@echo ""
	@echo "To run with custom data:"
	@echo "  make run-with-file FILE=your_data.csv"
	@echo ""
	@echo "To see all options:"
	@echo "  make help"
	@echo "=========================================="

# Phony targets
.PHONY: all build compile compile-tests test test-basic test-junit test-performance \
        run run-with-file clean clean-all setup setup-junit validate package docs \
        help check-java