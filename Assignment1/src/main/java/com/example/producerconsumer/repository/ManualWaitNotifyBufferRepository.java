package com.example.producerconsumer.repository;

import com.example.producerconsumer.model.Item;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Implementation of BufferRepository using manual synchronized/wait/notify
 * logic.
 * <p>
 * This repository manages a fixed-size queue of items. It uses standard Java
 * object monitors
 * to block producers when full and consumers when empty.
 * </p>
 */
public class ManualWaitNotifyBufferRepository implements BufferRepository {
    final Queue<Item> queue; // Package-private for testing
    private final int capacity;

    public ManualWaitNotifyBufferRepository(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.queue = new LinkedList<>();
    }

    /**
     * Adds an item to the buffer. Blocks if the buffer is full.
     *
     * @param item the item to add
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    @Override
    public synchronized void add(Item item) throws InterruptedException {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add null item to buffer");
        }
        while (queue.size() == capacity) {
            wait();
        }
        queue.add(item);
        notifyAll();
    }

    /**
     * Retrieves and removes an item from the buffer. Blocks if the buffer is empty.
     *
     * @return the removed item
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    @Override
    public synchronized Item poll() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        Item item = queue.poll();
        notifyAll();
        return item;
    }

    @Override
    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }
}
