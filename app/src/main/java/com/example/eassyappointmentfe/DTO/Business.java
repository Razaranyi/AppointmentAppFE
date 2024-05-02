package com.example.eassyappointmentfe.DTO;

import android.net.Uri;

import java.util.Set;

public class Business {
    private long id;
    private String name;
    private Set<Branch> branches;

    private Uri businessImage;

    private boolean isFavorite;


    public Business(long id, String name, Set<Branch> branches, Uri businessImage,boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.branches = branches;
        this.businessImage = businessImage;
        this.isFavorite = isFavorite;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Branch> getBranches() {
        return branches;
    }

    public Uri getBusinessImage() {
        return businessImage;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
