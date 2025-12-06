package com.example.producerconsumer.repository;

import com.example.producerconsumer.model.Item;
import java.util.List;

/**
 * Interface for the destination where items are stored.
 */
public interface DestinationRepository {
    /**
     * Saves an item to the destination.
     *
     * @param item the item to save
     */
    void save(Item item);

    /**
     * Retrieves all saved items.
     * 
     * @return List of all items.
     */
    List<Item> getAll();
}
