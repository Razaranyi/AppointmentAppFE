package com.example.eassyappointmentfe.businessActivity;

import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Locale;
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
        businessId = getBusinessId();

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
        openingTimeInput.setOnClickListener(v -> showTimePickerDialog(openingTimeInput));
        closingTimeInput.setOnClickListener(v -> showTimePickerDialog(closingTimeInput));
    }


        private void handleImageSelection (Uri uri){
            if (uri == null) { // No image selected
                return;
            }
            ImageUtils.handleImageSelection(this, uri, branchLogoImageView, Bitmap.CompressFormat.JPEG, 100);
        }

    private void showTimePickerDialog(final EditText timeInput) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    timeInput.setText(formattedTime);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void setUpCreateBranchButton() {
        createBranchButton.setOnClickListener(v -> createBranch());
        }

        private void createBranch(){
            try {
                JSONObject branchData = new JSONObject();
                branchData.put("name", branchNameInput.getText().toString());
                branchData.put("address", branchAddressInput.getText().toString());
                branchData.put("openingHours", toLocalTime(openingTimeInput.getText().toString()));
                branchData.put("closingHours", toLocalTime(closingTimeInput.getText().toString()));
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

                    processResponse(response);
                }).start();
            } catch (JSONException e) {
                Log.e("CreateBranchActivity", "JSON Exception: ", e);
            }

        }
        private void processResponse(String response) {
        try {
            JSONObject responseObject = new JSONObject(response);
            String message = responseObject.getJSONObject("response").getString("message");

            runOnUiThread(() -> {
                Toast.makeText(CreateBranchActivity.this, message, Toast.LENGTH_LONG).show();
            });
        } catch (JSONException e) {
            Log.e("CreateBranchActivity", "JSON Exception: ", e);
            runOnUiThread(() -> {
                Toast.makeText(CreateBranchActivity.this, "Failed to create branch", Toast.LENGTH_LONG).show();
            });
            e.printStackTrace();
        }
    }


    private LocalTime toLocalTime(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            return null; // or return a default LocalTime value
        }
        String[] parts = timeString.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalTime.of(hour, minute);
        }
        return null;
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
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.getJSONObject("data").getString("id");
        } catch (ExecutionException | InterruptedException | JSONException e) {
            Log.e("CreateBranchActivity", "Exception: ", e);
            return null;
        } finally {
            executor.shutdown();
        }
    }

}



