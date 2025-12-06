package com.example.producerconsumer;

import com.example.producerconsumer.model.Item;
import com.example.producerconsumer.repository.BufferRepository;
import com.example.producerconsumer.repository.DestinationRepository;
import com.example.producerconsumer.repository.InMemoryDestinationRepository;
import com.example.producerconsumer.repository.InMemorySourceRepository;
import com.example.producerconsumer.repository.ManualWaitNotifyBufferRepository;
import com.example.producerconsumer.repository.SharedBufferRepository;
import com.example.producerconsumer.repository.SourceRepository;
import com.example.producerconsumer.service.ConsumerService;
import com.example.producerconsumer.service.ProducerService;
import java.util.ArrayList;
import java.util.List;

/**
 * Entry point for the Producer-Consumer demonstration.
 * 
 * This project fulfills requirements by implementing:
 * 1. Explicit Source -> Buffer -> Destination data flow logic using
 * Repositories.
 * 2. Two Buffer strategies:
 * - ManualWaitNotifyBufferRepository (using synchronized/wait/notify)
 * - SharedBufferRepository (using ArrayBlockingQueue)
 * 3. Zero data loss guarantee verified by integration tests.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Starting Producer-Consumer Dual Demo ===");
        System.out.println();

        // Demo 1: Manual Wait/Notify Buffer
        runDemo("Manual Wait/Notify Buffer", new ManualWaitNotifyBufferRepository(5), 10);
        System.out.println();

        // Demo 2: Standard BlockingQueue Buffer
        runDemo("Standard BlockingQueue Buffer", new SharedBufferRepository(5), 10);

        System.out.println();
        System.out.println("=== Dual Demo Complete ===");
    }

    private static void runDemo(String demoName, BufferRepository buffer, int itemCount) {
        System.out.println(">>> Running: " + demoName);

        // Setup Source
        List<Item> initialItems = new ArrayList<>();
        for (int i = 1; i <= itemCount; i++) {
            initialItems.add(new Item(i, "Data-" + i));
        }
        SourceRepository source = new InMemorySourceRepository(initialItems);

        // Setup Destination
        DestinationRepository destination = new InMemoryDestinationRepository();

        // Setup Services
        ProducerService producer = new ProducerService(buffer, source);
        ConsumerService consumer = new ConsumerService(buffer, destination);

        // Start Threads
        Thread producerThread = new Thread(producer, "Producer");
        Thread consumerThread = new Thread(consumer, "Consumer");

        producerThread.start();
        consumerThread.start();

        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Finished: " + demoName);
        List<Item> result = destination.getAll();
        System.out.println("Destination count: " + result.size());
    }
}
