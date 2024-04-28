package com.example.eassyappointmentfe.DTO;

import android.net.Uri;

import java.util.Set;

public class ServiceProvider {
    private long id;
    private String name;
    private boolean[] workingDays;
    private String breakHour;
    private int sessionDuration;
    private Set<Long> appointments;
    private long branchId;

    private Uri serviceProviderImage;

    public ServiceProvider() {
    }

    public ServiceProvider(long id, String name, boolean[] workingDays, String breakHour, int sessionDuration, Set<Long> appointments, long branchId, Uri serviceProviderImage) {
        this.id = id;
        this.name = name;
        this.workingDays = workingDays;
        this.breakHour = breakHour;
        this.sessionDuration = sessionDuration;

        if (!appointments.isEmpty()) {
            this.appointments = appointments;
        }
        this.branchId = branchId;
        if (serviceProviderImage != null) {
            this.serviceProviderImage = serviceProviderImage;
        }
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean[] getWorkingDays() {
        return workingDays;
    }

    public String getBreakHour() {
        return breakHour;
    }

    public int getSessionDuration() {
        return sessionDuration;
    }

    public Set<Long> getAppointments() {
        return appointments;
    }

    public long getBranchId() {
        return branchId;
    }

    public Uri getServiceProviderImage() {
        return serviceProviderImage;
    }
}
