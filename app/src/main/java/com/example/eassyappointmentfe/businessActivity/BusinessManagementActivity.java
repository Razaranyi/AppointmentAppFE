package com.example.eassyappointmentfe.businessActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eassyappointmentfe.DTO.Branch;
import com.example.eassyappointmentfe.DTO.ServiceProvider;
import com.example.eassyappointmentfe.R;
import com.example.eassyappointmentfe.adapters.BranchAdapter;
import com.example.eassyappointmentfe.util.ImageUtils;
import com.example.eassyappointmentfe.util.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BusinessManagementActivity extends AppCompatActivity {

    private String businessId;
    private RecyclerView branchRecyclerView;
    private RecyclerView serviceProviderRecyclerView;
    private BranchAdapter branchAdapter;
    private BranchAdapter serviceProviderAdapter;
    private List<Branch> branches = new ArrayList<>();

    private List<ServiceProvider> serviceProviders = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_management);
        Intent intent = getIntent();
        businessId = intent.getStringExtra("businessId");

        branchRecyclerView = findViewById(R.id.branchesRecyclerView);
        serviceProviderRecyclerView = findViewById(R.id.serviceProviderRecyclerView);
        branchRecyclerView.setLayoutManager(new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false));

        serviceProviderRecyclerView.setLayoutManager(new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false));

        fetchBranches();
        fetchServiceProviders();

        branchAdapter = new BranchAdapter(this, branches);
        serviceProviderAdapter = new ServiceProviderAdapter(this, serviceProviders);
        branchRecyclerView.setAdapter(branchAdapter);


    }

    private void fetchBranches() {
        new Thread(() -> {
            String response = NetworkUtils.performGetRequest(
                    this,
                    "http://10.0.2.2:8080/api/business/" + businessId + "/branch/get-all",
                    true);
            branches = parseBranches(response);
            runOnUiThread(() -> {
                updateBranches(branches);
            });
        }).start();
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
            Uri imageUri = ImageUtils.getImageUri(this, decodedByte,name);

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
        branchAdapter = new BranchAdapter(this, newBranches);
        branchRecyclerView.setAdapter(branchAdapter);
    }

}

