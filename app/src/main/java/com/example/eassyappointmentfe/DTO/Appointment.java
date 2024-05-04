package com.example.eassyappointmentfe.DTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Appointment {

    private Long id;
    private Date startTime;
    private Date endTime;
    private Boolean isAvailable;
    private Long serviceProviderId;
    private Long bookingId;
    private String bookingUserName;

    public Appointment(Long id, Date startTime, Date endTime, Boolean isAvailable, Long serviceProviderId, Long bookingId,String bookingUserName) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAvailable = isAvailable;
        this.serviceProviderId = serviceProviderId;
        if (bookingId != null) {
            this.bookingId = bookingId;
        }
        if (bookingUserName != null) {
            this.bookingUserName = bookingUserName;
        }

    }

    public static List<Appointment> parseAppointments(String jsonResponse) {
        List<Appointment> appointments = new ArrayList<>();
        String bookingUserName = null;
        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                System.out.println("parsing appointment: " + jsonObject.toString());
                if (jsonObject.getBoolean("isAvailable")) {
                    jsonObject.put("bookingId", 0);
                }
                if (jsonObject.has("bookingUserName")) {
                  bookingUserName =   jsonObject.getString("bookingUserName");
                }
                Appointment appointment = new Appointment(
                        jsonObject.getLong("id"),
                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(jsonObject.getString("startTime")),
                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(jsonObject.getString("endTime")),
                        jsonObject.getBoolean("isAvailable"),
                        jsonObject.getLong("serviceProviderId"),
                        jsonObject.getLong("bookingId"),
                        bookingUserName


                );

                System.out.println("parsed appointment: " + appointment.toString());
                appointments.add(appointment);
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace(); // Handle error appropriately
        }
        return appointments;
    }

    public Long getId() {
        return id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Boolean isAvailable() {
        return isAvailable;
    }

    public Long getServiceProviderId() {
        return serviceProviderId;
    }


    public String getBookingUserName() {
        return bookingUserName;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", isAvailable=" + isAvailable +
                ", serviceProviderId=" + serviceProviderId +
                ", bookingId=" + bookingId +
                '}';
    }
}