package com.example.producerconsumer.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {

    @Test
    void testValidItem() {
        Item item = new Item(1, "Valid");
        assertEquals(1, item.getId());
        assertEquals("Valid", item.getPayload());
    }

    @Test
    void testNegativeId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Item(-1, "Negative");
        });
        assertEquals("ID cannot be negative", exception.getMessage());
    }

    @Test
    void testNullPayload() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Item(1, null);
        });
        assertEquals("Payload cannot be null", exception.getMessage());
    }
}
