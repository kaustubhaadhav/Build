package com.example.producerconsumer.repository;

import com.example.producerconsumer.model.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class ManualWaitNotifyBufferRepositoryTest {

    @Test
    void testAddAndPoll() throws InterruptedException {
        ManualWaitNotifyBufferRepository buffer = new ManualWaitNotifyBufferRepository(5);
        Item item = new Item(1, "Test");
        buffer.add(item);

        assertFalse(buffer.isEmpty());
        assertEquals(item, buffer.poll());
        assertTrue(buffer.isEmpty());
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void testBlockingWhenFull() throws InterruptedException {
        ManualWaitNotifyBufferRepository buffer = new ManualWaitNotifyBufferRepository(1);
        buffer.add(new Item(1, "1"));

        AtomicBoolean producerFinished = new AtomicBoolean(false);
        Thread producer = new Thread(() -> {
            try {
                buffer.add(new Item(2, "2"));
                producerFinished.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        producer.start();

        // Give it a moment to block
        Thread.sleep(100);
        assertFalse(producerFinished.get(), "Producer should be blocked because buffer is full");

        // Clear space
        buffer.poll();

        // Wait for producer to finish
        producer.join(1000);
        assertTrue(producerFinished.get(), "Producer should finish after space is available");
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void testBlockingWhenEmpty() throws InterruptedException {
        ManualWaitNotifyBufferRepository buffer = new ManualWaitNotifyBufferRepository(1);

        AtomicBoolean consumerFinished = new AtomicBoolean(false);
        Thread consumer = new Thread(() -> {
            try {
                buffer.poll();
                consumerFinished.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();

        // Give it a moment to block
        Thread.sleep(100);
        assertFalse(consumerFinished.get(), "Consumer should be blocked because buffer is empty");

        // Add item
        buffer.add(new Item(1, "1"));

        // Wait for consumer to finish
        consumer.join(1000);
        assertTrue(consumerFinished.get(), "Consumer should finish after item is added");
    }

    @Test
    void testInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new ManualWaitNotifyBufferRepository(0));
        assertThrows(IllegalArgumentException.class, () -> new ManualWaitNotifyBufferRepository(-1));
    }
}
