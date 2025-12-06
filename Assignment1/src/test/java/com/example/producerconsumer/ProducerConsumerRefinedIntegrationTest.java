package com.example.producerconsumer;

import com.example.producerconsumer.model.Item;
import com.example.producerconsumer.repository.BufferRepository;
import com.example.producerconsumer.repository.DestinationRepository;
import com.example.producerconsumer.repository.InMemoryDestinationRepository;
import com.example.producerconsumer.repository.InMemorySourceRepository;
import com.example.producerconsumer.repository.ManualWaitNotifyBufferRepository;
import com.example.producerconsumer.repository.SourceRepository;
import com.example.producerconsumer.service.ConsumerService;
import com.example.producerconsumer.service.ProducerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProducerConsumerRefinedIntegrationTest {

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testFullFlowNoLoss() throws InterruptedException {
        // 1. Setup Data
        List<Item> inputItems = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            inputItems.add(new Item(i, "Load-" + i));
        }

        // 2. Setup Components
        SourceRepository source = new InMemorySourceRepository(inputItems);
        DestinationRepository destination = new InMemoryDestinationRepository();
        BufferRepository buffer = new ManualWaitNotifyBufferRepository(10);

        ProducerService producer = new ProducerService(buffer, source);
        ConsumerService consumer = new ConsumerService(buffer, destination);

        // 3. Execution
        Thread pThread = new Thread(producer);
        Thread cThread = new Thread(consumer);

        pThread.start();
        cThread.start();

        pThread.join();
        cThread.join();

        // 4. Verification
        List<Item> results = destination.getAll();
        assertEquals(50, results.size(), "Should consume exactly 50 items");

        // Optionally verify order
        for (int i = 0; i < 50; i++) {
            assertEquals("Load-" + i, results.get(i).getPayload());
        }
    }
}
