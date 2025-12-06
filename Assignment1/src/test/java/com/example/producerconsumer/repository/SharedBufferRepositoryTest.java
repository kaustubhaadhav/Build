package com.example.producerconsumer.repository;

import com.example.producerconsumer.model.Item;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

public class SharedBufferRepositoryTest {

    @Test
    void testAddAndPoll() throws InterruptedException {
        BufferRepository repo = new SharedBufferRepository(10);
        Item item = new Item(1, "test");
        repo.add(item);
        Item result = repo.poll();
        assertEquals(item.getId(), result.getId());
    }

    @Test
    void testBlockingBehavior() throws InterruptedException {
        BufferRepository repo = new SharedBufferRepository(1); // Capacity 1
        CountDownLatch latch = new CountDownLatch(1);

        Thread t = new Thread(() -> {
            try {
                repo.add(new Item(1, "A"));
                repo.add(new Item(2, "B")); // Should block here
                latch.countDown(); // Should not reach here until polled
            } catch (InterruptedException e) {
                // Ignore
            }
        });

        t.start();

        // Wait a bit to ensure thread is blocked
        boolean finishedEarly = latch.await(500, TimeUnit.MILLISECONDS);
        assertFalse(finishedEarly, "Thread should be blocked on full buffer");

        // Now consume one
        repo.poll();

        boolean finishedLater = latch.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(finishedLater, "Thread should unblock after poll");
    }

    @Test
    void testNullHandling() {
        BufferRepository repo = new SharedBufferRepository(10);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            repo.add(null);
        });
        assertEquals("Cannot add null item to buffer", exception.getMessage());
    }

    @Test
    void testInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new SharedBufferRepository(0));
        assertThrows(IllegalArgumentException.class, () -> new SharedBufferRepository(-5));
    }

    @Test
    void testConsumerBlocksWhenEmpty() throws InterruptedException {
        BufferRepository repo = new SharedBufferRepository(10);
        CountDownLatch latch = new CountDownLatch(1);

        Thread consumerThread = new Thread(() -> {
            try {
                repo.poll(); // Should block
                latch.countDown();
            } catch (InterruptedException e) {
                // Ignore
            }
        });

        consumerThread.start();

        // Ensure it blocks
        boolean finishedEarly = latch.await(200, TimeUnit.MILLISECONDS);
        assertFalse(finishedEarly, "Consumer should block when buffer is empty");

        // Unblock
        repo.add(new Item(1, "WakeUp"));
        boolean finishedLater = latch.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(finishedLater, "Consumer should unblock when item is added");
    }

    @Test
    void testFIFOOrder() throws InterruptedException {
        BufferRepository repo = new SharedBufferRepository(10);
        repo.add(new Item(1, "1"));
        repo.add(new Item(2, "2"));
        repo.add(new Item(3, "3"));

        assertEquals(1, repo.poll().getId());
        assertEquals(2, repo.poll().getId());
        assertEquals(3, repo.poll().getId());
    }

    @Test
    void testProducerUnblocksAfterPoll() throws InterruptedException {
        // Capacity 1 for easy filling
        BufferRepository repo = new SharedBufferRepository(1);
        CountDownLatch producerLatch = new CountDownLatch(1);

        // Fill buffer
        repo.add(new Item(1, "Filler"));

        Thread producerThread = new Thread(() -> {
            try {
                repo.add(new Item(2, "Blocked")); // Should block
                producerLatch.countDown();
            } catch (InterruptedException e) {
                // Ignore
            }
        });

        producerThread.start();

        // Ensure blocked
        boolean finishedEarly = producerLatch.await(200, TimeUnit.MILLISECONDS);
        assertFalse(finishedEarly, "Producer should block when buffer is full");

        // Consume to unblock
        repo.poll();

        boolean finishedLater = producerLatch.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(finishedLater, "Producer should unblock after consumer polls");
    }

    @Test
    void testBoundaryCapacity() throws InterruptedException {
        BufferRepository repo = new SharedBufferRepository(1);
        CountDownLatch latch = new CountDownLatch(1);

        // 1. Add item - should succeed
        repo.add(new Item(1, "A"));

        // 2. Try to add another - should block
        Thread t = new Thread(() -> {
            try {
                repo.add(new Item(2, "B"));
                latch.countDown();
            } catch (InterruptedException e) {
            }
        });
        t.start();

        assertFalse(latch.await(200, TimeUnit.MILLISECONDS));

        // 3. Remove item
        assertEquals(1, repo.poll().getId());

        // 4. Thread should finish now
        assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));

        // 5. Verify queue has the second item
        assertEquals(2, repo.poll().getId());
    }
}
