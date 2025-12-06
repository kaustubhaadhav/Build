package com.example.producerconsumer.service;

import com.example.producerconsumer.model.Item;
import com.example.producerconsumer.repository.BufferRepository;
import com.example.producerconsumer.repository.DestinationRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConsumerServiceTest {

    @Test
    void testConsumerMovesItemsToDestination() {
        // Mock Buffer with items + Poison Pill
        Queue<Item> bufferQueue = new LinkedList<>();
        bufferQueue.add(new Item(1, "A"));
        bufferQueue.add(new Item(2, "B"));
        bufferQueue.add(new Item(0, "POISON_PILL"));

        BufferRepository stubBuffer = new BufferRepository() {
            @Override
            public void add(Item item) {
            }

            @Override
            public Item poll() {
                return bufferQueue.poll();
            }

            @Override
            public boolean isEmpty() {
                return bufferQueue.isEmpty();
            }
        };

        // Mock Destination
        List<Item> destinationList = new ArrayList<>();
        DestinationRepository spyDestination = new DestinationRepository() {
            @Override
            public void save(Item item) {
                destinationList.add(item);
            }

            @Override
            public List<Item> getAll() {
                return destinationList;
            }
        };

        ConsumerService consumer = new ConsumerService(stubBuffer, spyDestination);
        consumer.run();

        // Assert that consumer processed all items until poison pill, but did NOT save
        // poison pill (logic check)
        // Re-reading logic: Consumer logic: if (poison) break; ... dest.save(item);
        // So poison pill is NOT saved to destination.

        assertEquals(2, destinationList.size());
        assertEquals("A", destinationList.get(0).getPayload());
        assertEquals("B", destinationList.get(1).getPayload());
    }

    @Test
    void testConsumerExitsOnImmediatePoisonPill() {
        // Mock Buffer with ONLY Poison Pill
        Queue<Item> bufferQueue = new LinkedList<>();
        bufferQueue.add(new Item(0, "POISON_PILL"));

        BufferRepository stubBuffer = new BufferRepository() {
            @Override
            public void add(Item item) {
            }

            @Override
            public Item poll() {
                return bufferQueue.poll();
            }

            @Override
            public boolean isEmpty() {
                return bufferQueue.isEmpty();
            }
        };

        // Mock Destination
        List<Item> destinationList = new ArrayList<>();
        DestinationRepository spyDestination = new DestinationRepository() {
            @Override
            public void save(Item item) {
                destinationList.add(item);
            }

            @Override
            public List<Item> getAll() {
                return destinationList;
            }
        };

        ConsumerService consumer = new ConsumerService(stubBuffer, spyDestination);
        consumer.run();

        // Should be empty because poison pill is not saved
        assertEquals(0, destinationList.size());
    }
}
