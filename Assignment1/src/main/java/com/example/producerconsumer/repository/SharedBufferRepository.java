package com.example.producerconsumer.repository;

import com.example.producerconsumer.model.Item;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Implementation of BufferRepository using
 * {@link java.util.concurrent.BlockingQueue}.
 * <p>
 * This implementation delegates thread safety and blocking behavior to
 * {@link java.util.concurrent.ArrayBlockingQueue}, which uses internal locks
 * and conditions.
 * </p>
 */
public class SharedBufferRepository implements BufferRepository {
    private final BlockingQueue<Item> queue;

    public SharedBufferRepository(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.queue = new ArrayBlockingQueue<>(capacity);
    }

    /**
     * Adds an item to the buffer. Blocks if the buffer is full.
     *
     * @param item the item to add
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    @Override
    public void add(Item item) throws InterruptedException {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add null item to buffer");
        }
        queue.put(item);
    }

    /**
     * Retrieves and removes an item from the buffer. Blocks if the buffer is empty.
     *
     * @return the removed item
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    @Override
    public Item poll() throws InterruptedException {
        return queue.take();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
