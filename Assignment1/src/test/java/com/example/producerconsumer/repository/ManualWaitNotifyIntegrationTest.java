package com.example.producerconsumer.repository;

import com.example.producerconsumer.model.Item;
import com.example.producerconsumer.service.ConsumerService;
import com.example.producerconsumer.service.ProducerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManualWaitNotifyIntegrationTest {

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void testProducerWithEmptySourceEmitsPoisonPill() throws InterruptedException {
        // Setup
        ManualWaitNotifyBufferRepository buffer = new ManualWaitNotifyBufferRepository(5);
        // Empty source
        InMemorySourceRepository source = new InMemorySourceRepository(Collections.emptyList());

        ProducerService producer = new ProducerService(buffer, source);

        // Action
        Thread producerThread = new Thread(producer);
        producerThread.start();
        producerThread.join();

        // Assertion
        // Buffer should contain exactly one item: the poison pill
        assertEquals(1, buffer.queue.size(), "Buffer should contain exactly one item (Poison Pill)");
        Item item = buffer.poll();
        assertEquals("POISON_PILL", item.getPayload());
        assertEquals(0, item.getId());
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void testConsumerReceivingOnlyPoisonPillExitsWithoutSaving() throws InterruptedException {
        // Setup
        ManualWaitNotifyBufferRepository buffer = new ManualWaitNotifyBufferRepository(5);
        // Pre-fill buffer with ONLY poison pill
        buffer.add(new Item(0, "POISON_PILL"));

        InMemoryDestinationRepository destination = new InMemoryDestinationRepository();
        ConsumerService consumer = new ConsumerService(buffer, destination);

        // Action
        Thread consumerThread = new Thread(consumer);
        consumerThread.start();
        consumerThread.join();

        // Assertion
        // Destination should be empty because poison pill is not saved
        assertTrue(destination.getAll().isEmpty(), "Destination should be empty when only poison pill is consumed");
    }
}
