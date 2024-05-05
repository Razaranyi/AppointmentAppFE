package com.example.eassyappointmentfe.userActivity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eassyappointmentfe.R;

public class AppointmentActivity extends AppCompatActivity {

    private TextView appointmentDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

//        appointmentDetails = findViewById(R.id.appointmentDetails);

        // Fetch the appointment data and populate the views
        // This is just a placeholder, replace it with your actual data fetching and processing logic
        String appointmentData = "Appointment details go here";
        appointmentDetails.setText(appointmentData);
    }
}