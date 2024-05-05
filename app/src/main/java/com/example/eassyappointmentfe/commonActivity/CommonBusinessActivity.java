//TODO:1 Add functionality of cancel button then remove appointment button
//TODO:2 Add a button to add business owner

package com.example.eassyappointmentfe.commonActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eassyappointmentfe.DTO.Appointment;
import com.example.eassyappointmentfe.DTO.Branch;
import com.example.eassyappointmentfe.DTO.ServiceProvider;
import com.example.eassyappointmentfe.R;
import com.example.eassyappointmentfe.adapters.AppointmentsAdapter;
import com.example.eassyappointmentfe.adapters.BranchAdapter;
import com.example.eassyappointmentfe.adapters.ServiceProviderAdapter;
import com.example.eassyappointmentfe.businessActivity.CreateBranchActivity;
import com.example.eassyappointmentfe.businessActivity.CreateNewEmployeeActivity;
import com.example.eassyappointmentfe.userActivity.MainPageActivity;
import com.example.eassyappointmentfe.util.ImageUtils;
import com.example.eassyappointmentfe.util.NetworkUtils;
import com.example.eassyappointmentfe.util.TimeUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommonBusinessActivity extends AppCompatActivity implements BranchAdapter.OnBranchClickListener, ServiceProviderAdapter.OnServiceProviderClickListener {

    private String businessId;
    private String branchId;
    private String serviceProviderId;
    private RecyclerView branchRecyclerView;
    private RecyclerView serviceProviderRecyclerView;
    private BranchAdapter branchAdapter;
    private ServiceProviderAdapter serviceProviderAdapter;
    private RecyclerView appointmentsRecyclerView;


    private TextView businessName;
    private TextView customerStatus;
    private Button addBranchText;
    private Button addServiceProviderText;
    private Button appointmentButton;
    private List<Branch> branches = new ArrayList<>();
    private List<ServiceProvider> serviceProviders = new ArrayList<>();
    private EditText tvDate;

    private boolean isCustomer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_business);
        Intent intent = getIntent();

        initialize(intent);

        setUpBranchRecyclerView();
        fetchBranches();


        branchAdapter = new BranchAdapter(this, branches, this);
        branchRecyclerView.setAdapter(branchAdapter);


        setUpServiceProvidersRecyclerView();
        fetchServiceProviders();

        serviceProviderAdapter = new ServiceProviderAdapter(this, serviceProviders, this);
        serviceProviderRecyclerView.setAdapter(serviceProviderAdapter);

        appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        setAddBranchText();
        setUpAppointmentsButton(); //to be removed?
        setAddServiceProviderText();
        setUpCustomerStatus();
        setUpDatePicker();

    }

    private void initialize(Intent intent) {
        SharedPreferences preferences = getSharedPreferences("com.example.eassyappointmentfe.SHARED_PREFS", MODE_PRIVATE);
        businessId = intent.getStringExtra("businessId");
        isCustomer = intent.getBooleanExtra("isCustomer", true);

        businessName = findViewById(R.id.businessName);
        businessName.setText(intent.getStringExtra("businessName"));

        addBranchText = findViewById(R.id.addBranchText);
        addServiceProviderText = findViewById(R.id.addServiceProviderText);
        appointmentButton = findViewById(R.id.appointmentsButton);
        branchRecyclerView = findViewById(R.id.branchesRecyclerView);
        serviceProviderRecyclerView = findViewById(R.id.serviceProviderRecyclerView);
        customerStatus = findViewById(R.id.customerStatus);

        tvDate = findViewById(R.id.tvDate);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        tvDate.setText(currentDate);
    }

    private void fetchBranches() {
        if (businessId == null && !isCustomer) {
            businessId = NetworkUtils.getBusinessId(this);
        }
        new Thread(() -> {
            String response = null;
            try {
                response = NetworkUtils.performGetRequest(
                        this,
                        "business/" + businessId + "/branch/get-all",
                        true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            branches = parseBranches(response);
            if (branches.isEmpty()) {
                return;
            }
            runOnUiThread(() -> {
                updateBranches(branches);
            });
        }).start();
    }

    private void fetchServiceProviders() { //called only when branch is clicked or on start
        System.out.println("Fetching service providers. Branches: " + branches.size() + ",Business ID: " + businessId + " Branch ID: " + branchId);
        if (branches.isEmpty()) {
            return;
        }

        new Thread(() -> {
            if (branchId == null) {
                branchId = String.valueOf(branches.get(0).getId());
            }
            String response = null;
            try {
                response = NetworkUtils.performGetRequest(
                        this,
                        "business/" + businessId + "/" + branchId + "/service-provider/get-all",
                        true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            serviceProviders = parseServiceProviders(response);

            if (serviceProviders.isEmpty()) {
                return;
            }
            runOnUiThread(() -> {
                updateServiceProviders(serviceProviders);
                // Fetch appointments for the first service provider by default
                long firstServiceProviderId = serviceProviders.get(0).getId();
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                fetchAppointments(firstServiceProviderId, currentDate);
            });
        }).start();
    }

    private List<ServiceProvider> parseServiceProviders(String response) {
        List<ServiceProvider> serviceProviders = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray serviceProvidersJson = jsonObject.getJSONArray("data");

            for (int i = 0; i < serviceProvidersJson.length(); i++) {
                JSONObject serviceProviderJson = serviceProvidersJson.getJSONObject(i);

                // Decode the Base64 string to a byte array
                byte[] decodedString = Base64.decode(serviceProviderJson.getString("serviceProviderImage"), Base64.DEFAULT);
                String name = serviceProviderJson.getString("name");

                // Convert the byte array to a Bitmap
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                // Convert the Bitmap to a Uri
                Uri imageUri = ImageUtils.getImageUri(this, decodedByte, name);

                ServiceProvider serviceProvider = new ServiceProvider(
                        serviceProviderJson.getLong("id"),
                        serviceProviderJson.getString("name"),
                        NetworkUtils.convertJsonArrayToBooleanArray(serviceProviderJson.getJSONArray("workingDays")),
                        serviceProviderJson.getString("breakHour"),
                        serviceProviderJson.getInt("sessionDuration"),
                        NetworkUtils.convertJsonArrayToSet(serviceProviderJson.getJSONArray("appointmentsIds")),
                        serviceProviderJson.getLong("branchId"),
                        imageUri
                );

                serviceProviders.add(serviceProvider);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceProviders;
    }

    private List<Branch> parseBranches(String response) {
        List<Branch> branches = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray branchesJson = jsonObject.getJSONArray("data");

            for (int i = 0; i < branchesJson.length(); i++) {
                JSONObject branchJson = branchesJson.getJSONObject(i);

                // Decode the Base64 string to a byte array
                byte[] decodedString = Base64.decode(branchJson.getString("branchImage"), Base64.DEFAULT);
                String name = branchJson.getString("name");

                // Convert the byte array to a Bitmap
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                // Convert the Bitmap to a Uri
                Uri imageUri = ImageUtils.getImageUri(this, decodedByte, name);

                Branch branch = new Branch(
                        branchJson.getLong("branchId"),
                        branchJson.getString("name"),
                        branchJson.getString("address"),
                        branchJson.getString("openingHours"),
                        branchJson.getString("closingHours"),
                        imageUri,
                        null
                );

                branches.add(branch);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return branches;
    }

    private void updateBranches(List<Branch> newBranches) {
        branchAdapter = new BranchAdapter(this, newBranches, this);
        branchRecyclerView.setAdapter(branchAdapter);

        fetchServiceProviders();

    }

    private void updateServiceProviders(List<ServiceProvider> newServiceProviders) {
        serviceProviderAdapter = new ServiceProviderAdapter(this, newServiceProviders, this);
        serviceProviderRecyclerView.setAdapter(serviceProviderAdapter);
    }

    private void setUpBranchRecyclerView() {
        branchRecyclerView = findViewById(R.id.branchesRecyclerView);
        branchRecyclerView.setLayoutManager(new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false));
    }

    private void setUpServiceProvidersRecyclerView() {
        serviceProviderRecyclerView = findViewById(R.id.serviceProviderRecyclerView);
        serviceProviderRecyclerView.setLayoutManager(new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false));
    }

    private void setAddBranchText() {
        if (isCustomer) {
            addBranchText.setVisibility(Button.GONE);
            return;
        }
        addBranchText.setOnClickListener(v -> {
            Intent intent = new Intent(CommonBusinessActivity.this, CreateBranchActivity.class);
            intent.putExtra("businessId", businessId);
            startActivity(intent);
        });
    }

    private void setAddServiceProviderText() {
        if (isCustomer) {
            addServiceProviderText.setVisibility(Button.GONE);
            return;
        }
        addServiceProviderText.setOnClickListener(v -> {
            Intent intent = new Intent(CommonBusinessActivity.this, CreateNewEmployeeActivity.class);
            intent.putExtra("branchId", branchId);
            intent.putExtra("businessId", businessId);
            startActivity(intent);
        });
    }

    private void setUpAppointmentsButton() {
        appointmentButton.setOnClickListener(v -> {
//            Intent intent = new Intent(BusinessManagementActivity.this, AppointmentsActivity.class);
//            intent.putExtra("businessId", businessId);
//            startActivity(intent);
        });
    }

    private void setUpCustomerStatus() {

        if (isCustomer) {
            customerStatus.setVisibility(TextView.GONE);
        }
        customerStatus.setOnClickListener(v -> {
            Intent intent = new Intent(CommonBusinessActivity.this, MainPageActivity.class);
            startActivity(intent);
        });
    }


    @Override
    public void onBranchClick(Branch branch) {
        branchId = String.valueOf(branch.getId());
        fetchServiceProviders();
    }


    @Override
    public void onServiceProviderClick(long serviceProviderId) {
        fetchAppointments(serviceProviderId, tvDate.getText().toString());
    }

    private void fetchAppointments(long serviceProviderId, String date) { // called service provider clicked or when asked to get default or when date is changed
        new Thread(() -> {

            try {
                String response = NetworkUtils.performGetRequest(
                        this,
                        "business/"
                                + businessId + "/branch/" + branchId + "/service-provider/"
                                + serviceProviderId + "/appointment/get/date/" + date,
                        true
                );
                System.out.println("Appointments response: " + response);
                List<Appointment> appointments = Appointment.parseAppointments(response);

                System.out.println("Appointments: " + appointments.toString());
                runOnUiThread(() -> updateAppointmentsRecyclerView(appointments));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void updateAppointmentsRecyclerView(List<Appointment> appointments) {
        TextView noAppointmentsText = findViewById(R.id.noAppointmentsText);
        if (appointments.isEmpty()) {
            noAppointmentsText.setVisibility(View.VISIBLE);
        } else {
            noAppointmentsText.setVisibility(View.GONE);
        }
        if (appointmentsRecyclerView.getAdapter() == null) {
            appointmentsRecyclerView.setAdapter(new AppointmentsAdapter(this,appointments, isCustomer));
        } else {
            ((AppointmentsAdapter) appointmentsRecyclerView.getAdapter()).updateData(appointments);
        }
    }

    private void setUpDatePicker() {
        tvDate.setOnClickListener(v -> {
            TimeUtil.showDatePickerDialog(this, tvDate, () -> {
                if (serviceProviderAdapter.getSelectedServiceProviderId() != 0) {
                    fetchAppointments(serviceProviderAdapter.getSelectedServiceProviderId(), tvDate.getText().toString());
                }
            });
        });
    }
}
