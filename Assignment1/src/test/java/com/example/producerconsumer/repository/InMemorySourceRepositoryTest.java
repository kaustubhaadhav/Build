package com.example.producerconsumer.repository;

import com.example.producerconsumer.model.Item;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemorySourceRepositoryTest {

    @Test
    void testReadReturnsItemsInOrder() {
        List<Item> input = Arrays.asList(new Item(1, "A"), new Item(2, "B"));
        InMemorySourceRepository repo = new InMemorySourceRepository(input);

        Item item1 = repo.read();
        assertEquals("A", item1.getPayload());

        Item item2 = repo.read();
        assertEquals("B", item2.getPayload());
    }

    @Test
    void testHasNextReturnsCorrectly() {
        List<Item> input = Collections.singletonList(new Item(1, "A"));
        InMemorySourceRepository repo = new InMemorySourceRepository(input);

        assertTrue(repo.hasNext());
        repo.read();
        assertFalse(repo.hasNext());
    }

    @Test
    void testReadReturnsNullWhenEmpty() {
        InMemorySourceRepository repo = new InMemorySourceRepository(Collections.emptyList());
        assertNull(repo.read());
    }
}
