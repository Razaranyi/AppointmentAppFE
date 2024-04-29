package com.example.eassyappointmentfe.DTO;

import android.net.Uri;

import java.util.Set;

public class Branch {
    private long id;
    private String name;
    private String address;
    private String openingTime;
    private String closingTime;
    private Uri branchImage;

    private Set<Long> serviceProvidersIds;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public Uri getBranchImage() {
        return branchImage;
    }

    public Set<Long> getServiceProvidersIds() {
        return serviceProvidersIds;
    }

    public Branch(long id, String name, String address, String openingTime, String closingTime, Uri logoUri, Set<Long> serviceProviders) {
        this.id = id;
        this.name = name;
        if (address != null ){
            this.address = address;
        }
        this.openingTime = openingTime;
        this.closingTime = closingTime;

        if (logoUri != null) {
            this.branchImage = logoUri;
        }
        if (serviceProviders != null) {
            this.serviceProvidersIds = serviceProviders;
        }


    }



}
