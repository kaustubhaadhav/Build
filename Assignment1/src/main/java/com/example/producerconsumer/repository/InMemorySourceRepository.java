package com.example.producerconsumer.repository;

import com.example.producerconsumer.model.Item;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * In-memory implementation of SourceRepository using a pre-filled queue.
 */
public class InMemorySourceRepository implements SourceRepository {
    private final Queue<Item> items;

    public InMemorySourceRepository(List<Item> initialItems) {
        this.items = new LinkedList<>(initialItems); // Copy to ensure independence
    }

    @Override
    public synchronized Item read() {
        return items.poll();
    }

    @Override
    public synchronized boolean hasNext() {
        return !items.isEmpty();
    }
}
