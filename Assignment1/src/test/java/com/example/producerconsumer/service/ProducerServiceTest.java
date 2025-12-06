package com.example.producerconsumer.service;

import com.example.producerconsumer.model.Item;
import com.example.producerconsumer.repository.BufferRepository;
import com.example.producerconsumer.repository.SourceRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProducerServiceTest {

    @Test
    void testProducerMovesItemsToBuffer() {
        // Mock Source
        SourceRepository headerSource = new SourceRepository() {
            private final List<Item> items = new ArrayList<>(Arrays.asList(
                    new Item(1, "A"), new Item(2, "B")));

            @Override
            public synchronized Item read() {
                if (items.isEmpty())
                    return null;
                return items.remove(0);
            }

            @Override
            public synchronized boolean hasNext() {
                return !items.isEmpty();
            }
        };

        // Mock Buffer
        List<Item> bufferedItems = new ArrayList<>();
        BufferRepository spyBuffer = new BufferRepository() {
            @Override
            public void add(Item item) {
                bufferedItems.add(item);
            }

            @Override
            public Item poll() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return true;
            }
        };

        ProducerService producer = new ProducerService(spyBuffer, headerSource);
        producer.run();

        // Assert items + poison pill
        assertEquals(3, bufferedItems.size());
        assertEquals("A", bufferedItems.get(0).getPayload());
        assertEquals("B", bufferedItems.get(1).getPayload());
        assertEquals("POISON_PILL", bufferedItems.get(2).getPayload());
    }

    @Test
    void testProducerHandlesEmptySource() {
        // Mock Empty Source
        SourceRepository emptySource = new SourceRepository() {
            @Override
            public Item read() {
                return null;
            }

            @Override
            public boolean hasNext() {
                return false;
            }
        };

        // Mock Buffer
        List<Item> bufferedItems = new ArrayList<>();
        BufferRepository spyBuffer = new BufferRepository() {
            @Override
            public void add(Item item) {
                bufferedItems.add(item);
            }

            @Override
            public Item poll() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return true;
            }
        };

        ProducerService producer = new ProducerService(spyBuffer, emptySource);
        producer.run();

        // Should only contain Poison Pill
        assertEquals(1, bufferedItems.size());
        assertEquals("POISON_PILL", bufferedItems.get(0).getPayload());
    }
}
