package com.example.producerconsumer.repository;

import com.example.producerconsumer.model.Item;

/**
 * Interface for the shared buffer repository.
 * Allows identifying implementation strategies (in-memory, database, etc.).
 * Interface for a shared buffer between Producer and Consumer.
 * Defines methods for blocking addition and removal of items.
 */
public interface BufferRepository {
    /**
     * Adds an item to the buffer, blocking if necessary until space is available.
     *
     * @param item the item to store
     * @throws InterruptedException if interrupted while waiting
     */
    void add(Item item) throws InterruptedException;

    /**
     * Retrieves and removes an item from the buffer, blocking if necessary until an
     * item is available.
     *
     * @return the item
     * @throws InterruptedException if interrupted while waiting
     */
    Item poll() throws InterruptedException;

    /**
     * Checks if the buffer is empty.
     */
    boolean isEmpty();
}
