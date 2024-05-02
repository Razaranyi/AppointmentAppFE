package com.example.eassyappointmentfe.DTO;

import java.util.List;
import java.util.Set;

public class Category {
    private long id;
    private String name;

    private List<Business> businesses;

    public Category(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category(long id, String name, List<Business> businesses) {
        this.id = id;
        this.name = name;
        this.businesses = businesses;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Business> getBusinesses() {
        return businesses;
    }

    // This is important for ArrayAdapter to display the name in the dropdown correctly.
    @Override
    public String toString() {
        return name;
    }
}

