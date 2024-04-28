package com.example.eassyappointmentfe.businessActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eassyappointmentfe.R;
import com.example.eassyappointmentfe.util.ImageUtils;
import com.example.eassyappointmentfe.util.NetworkUtils;
import com.example.eassyappointmentfe.util.TimeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CreateBranchActivity extends AppCompatActivity {
    private EditText branchNameInput;
    private EditText branchAddressInput;

    private EditText openingTimeInput;
    private EditText closingTimeInput;

    private Button uploadLogoButton;
    private Button createBranchButton;

    private ImageView branchLogoImageView;

    private  String businessId;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            this::handleImageSelection
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_branch);
        Intent intent = getIntent();

        if (intent == null) {
            businessId = getBusinessId();
        }
        else {
            businessId = intent.getStringExtra("businessId");
        }

        initializeViews();
        setUpCreateBranchButton();

    }

    private void initializeViews() {
        branchNameInput = findViewById(R.id.branchNameInput);
        branchAddressInput = findViewById(R.id.branchAddressInput);
        openingTimeInput = findViewById(R.id.openingHourInput);
        closingTimeInput = findViewById(R.id.closingHourInput);
        uploadLogoButton = findViewById(R.id.uploadLogoButton);
        createBranchButton = findViewById(R.id.createBranchButton);
        branchLogoImageView = findViewById(R.id.branchLogoImageView);

        uploadLogoButton.setOnClickListener(v -> mGetContent.launch("image/*"));
        openingTimeInput.setOnClickListener(v -> TimeUtil.showTimePickerDialog(this,openingTimeInput));
        closingTimeInput.setOnClickListener(v -> TimeUtil.showTimePickerDialog(this,closingTimeInput));
    }


        private void handleImageSelection (Uri uri){
            if (uri == null) { // No image selected
                return;
            }
            ImageUtils.handleImageSelection(this, uri, branchLogoImageView, Bitmap.CompressFormat.JPEG, 100);
        }

    private void setUpCreateBranchButton() {
        createBranchButton.setOnClickListener(v -> createBranch());
        }

        private void createBranch(){
            try {
                JSONObject branchData = new JSONObject();
                branchData.put("name", branchNameInput.getText().toString());
                branchData.put("address", branchAddressInput.getText().toString());
                branchData.put("openingHours", TimeUtil.toLocalTime(openingTimeInput.getText().toString()));
                branchData.put("closingHours", TimeUtil.toLocalTime(closingTimeInput.getText().toString()));
                if (branchLogoImageView.getTag() != null) {
                    byte[] imageBytes = (byte[]) branchLogoImageView.getTag();
                    String encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    branchData.put("branchImage", encodedImage);
                }
                JSONObject rootObject = new JSONObject();
                rootObject.put("data", branchData);

                new Thread(() -> {
                    String response = String.valueOf(NetworkUtils.performPostRequest(
                            this,
                            "http://10.0.2.2:8080/api/business/" + businessId + "/branch/create",
                            rootObject,
                            true
                    ));

                    try {
                        JSONObject responseJson = new JSONObject(response);
                        String message = NetworkUtils.processResponse(responseJson, "message");
                        int status = new JSONObject(response).getInt("status");
                        runOnUiThread(() -> {
                            Toast.makeText(CreateBranchActivity.this, message, Toast.LENGTH_LONG).show();

                            if (status == 200) {
                                Intent intent = new Intent(CreateBranchActivity.this, CreateNewEmployeeActivity.class);
                                String branchId = NetworkUtils.processResponse(responseJson, "branchId");
                                intent.putExtra("branchId", branchId);
                                intent.putExtra("businessId", businessId);
                                startActivity(intent);
                            }
                        });
                    } catch (JSONException e) {
                        Log.e("CreateBranchActivity", "JSON Exception: ", e);
                        runOnUiThread(() -> {
                            Toast.makeText(CreateBranchActivity.this, "Failed to create branch", Toast.LENGTH_LONG).show();
                        });
                    }
                }).start();
            } catch (JSONException e) {
                Log.e("CreateBranchActivity", "JSON Exception: ", e);
            }
        }

    private String getBusinessId() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> result = executor.submit(() -> {
            return NetworkUtils.performGetRequest(
                    this,
                    "http://10.0.2.2:8080/api/business/get-id",
                    true
            );
        });

        try {
    String response = result.get();  // Blocks until the response is available
    return NetworkUtils.processResponse(new JSONObject(response), "id");
} catch (ExecutionException | InterruptedException | JSONException e) {
    Log.e("CreateBranchActivity", "Exception: ", e);
    return null;
} finally {
    executor.shutdown();
}
    }



}



