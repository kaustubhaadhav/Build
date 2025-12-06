package com.example.producerconsumer.service;

import com.example.producerconsumer.model.Item;
import com.example.producerconsumer.repository.BufferRepository;
import com.example.producerconsumer.repository.DestinationRepository;
import java.util.Random;

/**
 * Service responsible for consuming items.
 * <p>
 * Takes items from a {@link BufferRepository} and saves them to a
 * {@link DestinationRepository}.
 * Stops execution upon receiving a "Poison Pill".
 * </p>
 */
public class ConsumerService implements Runnable {
    private final BufferRepository buffer;
    private final DestinationRepository destination;

    public ConsumerService(BufferRepository buffer, DestinationRepository destination) {
        this.buffer = buffer;
        this.destination = destination;
    }

    @Override
    public void run() {
        Random random = new Random();
        try {
            while (true) {
                Item item = buffer.poll();
                if (item.getId() == 0 && "POISON_PILL".equals(item.getPayload())) {
                    System.out.println("Consumer received stop signal.");
                    break;
                }

                // Simulate processing
                Thread.sleep(random.nextInt(150));
                destination.save(item);
                System.out.println("Consumed: " + item);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Consumer interrupted");
        }
    }
}
