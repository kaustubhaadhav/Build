package com.example.producerconsumer.repository;

import com.example.producerconsumer.model.Item;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * In-memory implementation of DestinationRepository using a synchronized list.
 */
public class InMemoryDestinationRepository implements DestinationRepository {
    private final List<Item> storedItems = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void save(Item item) {
        storedItems.add(item);
    }

    @Override
    public List<Item> getAll() {
        // Return a copy to avoid concurrent modification issues if iterated externally
        // while still writing
        synchronized (storedItems) {
            return new ArrayList<>(storedItems);
        }
    }
}
