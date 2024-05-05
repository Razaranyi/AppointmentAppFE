package com.example.eassyappointmentfe.userActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eassyappointmentfe.DTO.Appointment;
import com.example.eassyappointmentfe.R;
import com.example.eassyappointmentfe.util.NetworkUtils;
import com.example.eassyappointmentfe.util.TimeUtil;
import com.example.eassyappointmentfe.adapters.AppointmentsAdapter;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentActivity extends AppCompatActivity {

    private EditText tvDate1;
    private EditText tvDate2;
    private RecyclerView appointmentsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        tvDate1 = findViewById(R.id.tvDate1);
        tvDate2 = findViewById(R.id.tvDate2);
        appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        setUpDatePicker();
        fetchAppointments(tvDate1.getText().toString(), tvDate2.getText().toString());
    }

    private void setUpDatePicker() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String nextWeekDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000));
        tvDate1.setText(currentDate);
        tvDate2.setText(nextWeekDate);

        tvDate1.setOnClickListener(v -> {
            TimeUtil.showDatePickerDialog(this, tvDate1, () -> {
                if (tvDate2.getText().toString().compareTo(tvDate1.getText().toString()) > 0) {
                    fetchAppointments(tvDate1.getText().toString(), tvDate2.getText().toString());
                }
            });
        });

        tvDate2.setOnClickListener(v -> {
            TimeUtil.showDatePickerDialog(this, tvDate2, () -> {
                if (tvDate2.getText().toString().compareTo(tvDate1.getText().toString()) > 0) {
                    fetchAppointments(tvDate1.getText().toString(), tvDate2.getText().toString());
                }
            });
        });
    }

    private void fetchAppointments(String startDate, String endDate) {
    new Thread(() -> {
        try {
            String response = NetworkUtils.performGetRequest(this,
                    "bookings/get-my-bookings?startDate=" + startDate + "&endDate=" + endDate,
                    true
            );
            System.out.println("my booking response: " + response);
            JSONObject jsonResponse = new JSONObject(response);
            List<Appointment> appointments = Appointment.parseAppointments(jsonResponse.getString("data"));
            runOnUiThread(() -> updateAppointmentsRecyclerView(appointments));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }).start();
}

    private void updateAppointmentsRecyclerView(List<Appointment> appointments) {
        TextView noAppointmentsTextView = findViewById(R.id.noAppointmentsTextView);
        if (appointments.isEmpty()) {
            noAppointmentsTextView.setText("No appointments found");
            noAppointmentsTextView.setVisibility(TextView.VISIBLE);
            appointmentsRecyclerView.setVisibility(RecyclerView.GONE);
        } else {
            noAppointmentsTextView.setVisibility(TextView.GONE);
            appointmentsRecyclerView.setVisibility(RecyclerView.VISIBLE);
        }

        if (appointmentsRecyclerView.getAdapter() == null) {
            appointmentsRecyclerView.setAdapter(new AppointmentsAdapter(this, appointments, true));
        } else {
            ((AppointmentsAdapter) appointmentsRecyclerView.getAdapter()).updateData(appointments);
        }
    }

    public void refreshAppointments() {
        fetchAppointments(tvDate1.getText().toString(), tvDate2.getText().toString());
    }
}