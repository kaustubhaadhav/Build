package com.example.producerconsumer.repository;

import com.example.producerconsumer.model.Item;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryDestinationRepositoryTest {

    @Test
    void testSaveStoresItems() {
        InMemoryDestinationRepository repo = new InMemoryDestinationRepository();
        repo.save(new Item(1, "A"));
        repo.save(new Item(2, "B"));

        List<Item> saved = repo.getAll();
        assertEquals(2, saved.size());
        assertEquals("A", saved.get(0).getPayload());
        assertEquals("B", saved.get(1).getPayload());
    }

    @Test
    void testGetAllReturnsCopy() {
        InMemoryDestinationRepository repo = new InMemoryDestinationRepository();
        repo.save(new Item(1, "A"));

        List<Item> returnedList = repo.getAll();
        returnedList.clear(); // Modify the returned list

        // Original repo should still have the item
        assertEquals(1, repo.getAll().size());
        assertEquals("A", repo.getAll().get(0).getPayload());
    }
}
