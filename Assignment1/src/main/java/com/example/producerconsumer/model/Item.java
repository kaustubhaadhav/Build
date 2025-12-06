package com.example.producerconsumer.model;

/**
 * Represents a unit of work or data to be processed.
 */
public class Item {
    private final int id;
    private final String payload;

    public Item(int id, String payload) {
        if (id < 0) {
            throw new IllegalArgumentException("ID cannot be negative");
        }
        if (payload == null) {
            throw new IllegalArgumentException("Payload cannot be null");
        }
        this.id = id;
        this.payload = payload;
    }

    public int getId() {
        return id;
    }

    public String getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "Item{id=" + id + ", payload='" + payload + "'}";
    }
}
