package com.example.eassyappointmentfe.DTO;

public class Category {
    private long id;
    private String name;

    public Category(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // This is important for ArrayAdapter to display the name in the dropdown correctly.
    @Override
    public String toString() {
        return name;
    }
}

