package com.example.producerconsumer.repository;

import com.example.producerconsumer.model.Item;

/**
 * Interface for the source of data items.
 */
public interface SourceRepository {
    /**
     * Reads the next item from the source.
     *
     * @return the item, or null if exhausted
     */
    Item read();

    /**
     * Checks if there are more items to read.
     * 
     * @return true if more items are available.
     */
    boolean hasNext();
}
