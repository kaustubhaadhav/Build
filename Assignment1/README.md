# Producer-Consumer Verification Project

This project demonstrates a robust implementation of the Producer-Consumer pattern in Java, focusing on explicit data movement, thread safety, and zero data loss. It showcases two distinct strategies for shared buffer management.

## Key Features

1.  **Explicit Data Flow**:
    *   **SourceRepository**: Where data originates (pre-filled in-memory list).
    *   **BufferRepository**: The thread-safe conduit.
    *   **DestinationRepository**: Where data is finally stored.
    *   **Services**: `ProducerService` and `ConsumerService` are stateless movers of data.

2.  **Dual Buffer Strategies**:
    *   **Manual Wait/Notify**: `ManualWaitNotifyBufferRepository` implements strict blocking using `synchronized` blocks, `wait()`, and `notifyAll()`.
    *   **Standard BlockingQueue**: `SharedBufferRepository` uses `java.util.concurrent.ArrayBlockingQueue` for production-grade reliability.

3.  **Reliability**:
    *   Guaranteed delivery from Source to Destination.
    *   No data loss or duplication.
    *   Poison Pill mechanism for graceful shutdown.

## Prerequisites

Please refer to `requirement.txt` for the list of necessary software and versions.

## How to Run

### 1. Run the Main Application
The main program executes both buffer strategies sequentially for demonstration.

```bash
mvn exec:java
```

Alternatively, if built with `package`, you can run the jar (assuming main class is set in manifest, though exec:java is easier for development):
```bash
java -cp target/classes:target/dependency/* com.example.producerconsumer.Main
```

**Note for JDK 21+ Users:**
If you see warnings about `sun.misc.Unsafe` or `HiddenClassDefiner`, they are from Maven's internal dependencies and can be ignored. To suppress them in the output, you can run:
```bash
mvn -q exec:java 2>&1 | grep -vE "sun.misc.Unsafe|HiddenClassDefiner|Please consider reporting"
```

### 2. Build the Project
To compile the project and clear any previous builds:
```bash
mvn clean package
```

### 3. Run Tests
The project includes comprehensive unit and integration tests using JUnit 5. to run them:

```bash
mvn test
```

## Sample Output

When running `mvn exec:java`, you will see output similar to this:

```text
=== Starting Producer-Consumer Dual Demo ===

>>> Running: Manual Wait/Notify Buffer
Produced: Item{id=1, payload='Data-1'}
Consumed: Item{id=1, payload='Data-1'}
Produced: Item{id=2, payload='Data-2'}
...
Finished: Manual Wait/Notify Buffer
Destination count: 10

>>> Running: Standard BlockingQueue Buffer
Produced: Item{id=1, payload='Data-1'}
Consumed: Item{id=1, payload='Data-1'}
...
Finished: Standard BlockingQueue Buffer
Destination count: 10

=== Dual Demo Complete ===
```

## Project Structure

*   `src/main/java/com/example/producerconsumer/repository`: Interface definitions and implementations for Source, Buffer, and Destination.
*   `src/main/java/com/example/producerconsumer/service`: Producer and Consumer logic.
*   `src/test/java`: Extensive test suite covering unit logic and full system integration.
