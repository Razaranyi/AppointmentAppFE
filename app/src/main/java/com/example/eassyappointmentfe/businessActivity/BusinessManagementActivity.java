package com.example.eassyappointmentfe.businessActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eassyappointmentfe.DTO.Branch;
import com.example.eassyappointmentfe.DTO.ServiceProvider;
import com.example.eassyappointmentfe.R;
import com.example.eassyappointmentfe.adapters.BranchAdapter;
import com.example.eassyappointmentfe.adapters.ServiceProviderAdapter;
import com.example.eassyappointmentfe.util.ImageUtils;
import com.example.eassyappointmentfe.util.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BusinessManagementActivity extends AppCompatActivity implements BranchAdapter.OnBranchClickListener {

    private String businessId;
    private String branchId;
    private RecyclerView branchRecyclerView;
    private RecyclerView serviceProviderRecyclerView;
    private BranchAdapter branchAdapter;
    private ServiceProviderAdapter serviceProviderAdapter;

    private TextView businessName;
    private Button addBranchText;
    private Button addServiceProviderText;
    private Button appointmentButton;
    private List<Branch> branches = new ArrayList<>();
    private List<ServiceProvider> serviceProviders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_management);
        Intent intent = getIntent();
        SharedPreferences preferences = getSharedPreferences("com.example.eassyappointmentfe.SHARED_PREFS", MODE_PRIVATE);

        businessId = intent.getStringExtra("businessId");
        businessName = findViewById(R.id.businessName);
        businessName.setText(preferences.getString("BUSINESS_NAME", "My Business"));
        addBranchText = findViewById(R.id.addBranchText);
        addServiceProviderText = findViewById(R.id.addServiceProviderText);
        appointmentButton = findViewById(R.id.appointmentsButton);
        branchRecyclerView = findViewById(R.id.branchesRecyclerView);
        serviceProviderRecyclerView = findViewById(R.id.serviceProviderRecyclerView);

        setUpBranchRecyclerView();
        fetchBranches();

        branchAdapter = new BranchAdapter(this, branches,this);
        branchRecyclerView.setAdapter(branchAdapter);

        setUpServiceProvidersRecyclerView();
        fetchServiceProviders();

        serviceProviderAdapter = new ServiceProviderAdapter(this, serviceProviders);
        serviceProviderRecyclerView.setAdapter(serviceProviderAdapter);


        setAddBranchText();
        setUpAppointmentsButton();
        setAddServiceProviderText();

    }

    private void fetchBranches() {
        if (businessId == null) {
           businessId = NetworkUtils.getBusinessId(this);
        }
        new Thread(() -> {
            String response = NetworkUtils.performGetRequest(
                    this,
                    "http://10.0.2.2:8080/api/business/" + businessId + "/branch/get-all",
                    true);
            branches = parseBranches(response);
            if (branches.isEmpty()) {
                return;
            }
            runOnUiThread(() -> {
                updateBranches(branches);
            });
        }).start();
    }

    private void fetchServiceProviders() {
        System.out.println("Fetching service providers. Branches: " + branches.size() + ",Business ID: " + businessId + " Branch ID: " + branchId);
        if (branches.isEmpty()) {
            return;
        }

        new Thread(() -> {
            if (branchId == null){
                branchId = String.valueOf(branches.get(0).getId());
            }
            String response = NetworkUtils.performGetRequest(
                    this,
                    "http://10.0.2.2:8080/api/business/" + businessId + "/" + branchId + "/service-provider/get-all",
                    true);
            serviceProviders = parseServiceProviders(response);

            if (serviceProviders.isEmpty()) {
                return;
            }
            runOnUiThread(() -> {
                updateServiceProviders(serviceProviders);
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
        serviceProviderAdapter = new ServiceProviderAdapter(this, newServiceProviders);
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
        addBranchText.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessManagementActivity.this, CreateBranchActivity.class);
            intent.putExtra("businessId", businessId);
            startActivity(intent);
        });
    }

    private void setAddServiceProviderText() {
        addServiceProviderText.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessManagementActivity.this, CreateNewEmployeeActivity.class);
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

    @Override
    public void onBranchClick(Branch branch) {
        branchId = String.valueOf(branch.getId());
        fetchServiceProviders();
    }
}