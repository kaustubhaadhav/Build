package com.example.producerconsumer.service;

import com.example.producerconsumer.model.Item;
import com.example.producerconsumer.repository.BufferRepository;
import com.example.producerconsumer.repository.SourceRepository;
import java.util.Random;

/**
 * Service responsible for producing items.
 * <p>
 * Reads items from a {@link SourceRepository} and puts them into a
 * {@link BufferRepository}.
 * Stops when the source is exhausted and sends a "Poison Pill" to signal
 * completion.
 * </p>
 */
public class ProducerService implements Runnable {
    private final BufferRepository buffer;
    private final SourceRepository source;

    public ProducerService(BufferRepository buffer, SourceRepository source) {
        this.buffer = buffer;
        this.source = source;
    }

    @Override
    public void run() {
        Random random = new Random();
        try {
            while (source.hasNext()) {
                Item item = source.read();
                if (item != null) {
                    // Simulate some work
                    Thread.sleep(random.nextInt(100));

                    buffer.add(item);
                    System.out.println("Produced: " + item);
                }
            }
            // Poison pill logic
            buffer.add(new Item(0, "POISON_PILL"));
            System.out.println("Producer finished.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Producer interrupted");
        }
    }
}
