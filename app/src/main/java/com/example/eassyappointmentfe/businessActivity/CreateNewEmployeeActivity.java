package com.example.eassyappointmentfe.businessActivity;

import static com.example.eassyappointmentfe.util.NetworkUtils.processResponse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eassyappointmentfe.R;
import com.example.eassyappointmentfe.commonActivity.CommonBusinessActivity;
import com.example.eassyappointmentfe.fragments.BreakTimeBottomSheetDialogFragment;
import com.example.eassyappointmentfe.fragments.DayOfWeekSelectorDialogFragment;
import com.example.eassyappointmentfe.util.ImageUtils;
import com.example.eassyappointmentfe.util.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CreateNewEmployeeActivity extends AppCompatActivity {
    private final Boolean[] daysOfTheWeek = new Boolean[7];
    private EditText employeeNameInput;
    private Spinner sessionDurationInput;
    private ImageView employeeImageView;
    private Button addMoreBreaksButton;
    private Button chooseDaysButton;
    private Button uploadEmployeeImageButton;
    private Button createEmployeeButton;
    private List<String> breakTimesList = new ArrayList<>();

    private String businessId;
    private String branchId;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            this::handleImageSelection
    );



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_employee);
        employeeNameInput = findViewById(R.id.employeeNameInput);
        employeeImageView = findViewById(R.id.employeeImageView);
        addMoreBreaksButton = findViewById(R.id.addMoreBreaksButton);
        chooseDaysButton = findViewById(R.id.chooseDaysButton);
        uploadEmployeeImageButton = findViewById(R.id.uploadEmployeeImageButton);
        createEmployeeButton = findViewById(R.id.createEmployeeButton);
        sessionDurationInput = findViewById(R.id.sessionDurationInput);
        employeeImageView = findViewById(R.id.employeeImageView);
        uploadEmployeeImageButton = findViewById(R.id.uploadEmployeeImageButton);

        businessId = intent.getStringExtra("businessId");
        branchId = intent.getStringExtra("branchId");

        System.out.println("Starting employee activity Business ID: " + businessId);
        System.out.println("Starting employee activity Branch ID: " + branchId);




        uploadEmployeeImageButton.setOnClickListener(v -> mGetContent.launch("image/*"));
        setupAddMoreBreaksButton();
        setupDaysOfTheWeekButton();
        setupCreateEmployeeButton();
        setSpinnerButton();
        System.out.println("Session Duration: " + sessionDurationInput.getSelectedItem().toString());

    }

    private void setupAddMoreBreaksButton() {
        addMoreBreaksButton.setOnClickListener(v -> {
            BreakTimeBottomSheetDialogFragment breakTimeBottomSheetDialogFragment = new BreakTimeBottomSheetDialogFragment();
            breakTimeBottomSheetDialogFragment.setListener(breaks -> breakTimesList.addAll(Arrays.asList(breaks)));
            breakTimeBottomSheetDialogFragment.show(getSupportFragmentManager(), breakTimeBottomSheetDialogFragment.getTag());
        });
    }

    private void setupDaysOfTheWeekButton(){
        chooseDaysButton.setOnClickListener(v -> {
            DayOfWeekSelectorDialogFragment dayOfWeekSelectorDialogFragment = new DayOfWeekSelectorDialogFragment();
            dayOfWeekSelectorDialogFragment.setDayOfWeekSelectorListener(days -> {
                for (int i = 0; i < days.length; i++) {
                    daysOfTheWeek[i] = days[i];
                }
            });
            dayOfWeekSelectorDialogFragment.show(getSupportFragmentManager(), "dayOfWeekSelector");
        });
    }

    private void setupCreateEmployeeButton() {
    createEmployeeButton.setOnClickListener(v -> {
        try {
            JSONObject employeeData = new JSONObject();
            employeeData.put("name", employeeNameInput.getText().toString());

            JSONArray daysArray = new JSONArray();
            for (Boolean selected : daysOfTheWeek) {
                daysArray.put(selected);
            }
            employeeData.put("workingDays", daysArray);

            JSONArray breaksArray = new JSONArray();
            for (String breakTime : breakTimesList) {
                breaksArray.put(breakTime);
            }
            employeeData.put("breakHour", breaksArray);

            // Check if a session duration has been selected
            String selectedDuration = sessionDurationInput.getSelectedItem().toString();
            if (!selectedDuration.equals("Session Duration")) {
                // Get the selected item's position
                int selectedPosition = sessionDurationInput.getSelectedItemPosition();
                // Get the corresponding value from the session_duration array
                String[] sessionDurations = getResources().getStringArray(R.array.session_duration);
                selectedDuration = sessionDurations[selectedPosition];
            } else {
                // If no duration has been selected, treat as null
                selectedDuration = "null";
            }
            // Use the selected duration
            employeeData.put("sessionDuration", selectedDuration);

            if (employeeImageView.getTag() != null) {
                byte[] imageBytes = (byte[]) employeeImageView.getTag();
                String encodedImage = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);
                employeeData.put("serviceProviderImage", encodedImage);
            }


            JSONObject rootObject = new JSONObject();
            rootObject.put("data", employeeData);

            sendEmployeeData(rootObject);
            System.out.println("Request: " + rootObject.toString());
            clearDialogSelections();
            breakTimesList.clear();

        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    });
}

    private void handleImageSelection(Uri uri) {
        if (uri == null) {
            return;
        }
        ImageUtils.handleImageSelection(this, uri, employeeImageView, Bitmap.CompressFormat.JPEG, 80);
    }


    private void sendEmployeeData(JSONObject employeeData) throws JSONException, ExecutionException, InterruptedException {

        Thread thread = new Thread(() -> {
            JSONObject response = NetworkUtils.performPostRequest(
                    this,
                    "business/" + businessId + "/" + branchId + "/service-provider/create",
                    employeeData,
                    true
            );
            runOnUiThread(() -> Toast.makeText(CreateNewEmployeeActivity.this, processResponse(response, "message"), Toast.LENGTH_LONG).show());
            try {
                if (response.getInt("status") == 200) {
                    Intent intent = new Intent(CreateNewEmployeeActivity.this, CommonBusinessActivity.class);
                    intent.putExtra("businessId", businessId);
                    intent.putExtra("branchId", branchId);
                    intent.putExtra("isCustomer",false);
                    intent.putExtra("businessName", "My Business");

                    startActivity(intent);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
    }

    private void clearDialogSelections() {
        System.out.println("Trying to clear selections in dialog");
        DayOfWeekSelectorDialogFragment dialogFragment = (DayOfWeekSelectorDialogFragment) getSupportFragmentManager().findFragmentByTag("dayOfWeekSelector");
        if (dialogFragment != null) {
            System.out.println("Fragment is added, now clearing selection");
            DayOfWeekSelectorDialogFragment fragment = (DayOfWeekSelectorDialogFragment) getSupportFragmentManager().findFragmentByTag("dayOfWeekSelector");
        } else {
            System.out.println("Fragment not found or not added");
        }
    }

   private void setSpinnerButton() {
    // Set the initial text of the Spinner
    List<String> initialList = new ArrayList<>();
    initialList.add("Session Duration");
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, initialList);
    sessionDurationInput.setAdapter(adapter);

    // Populate the Spinner with the actual durations when it is clicked
    sessionDurationInput.setOnTouchListener((v, event) -> {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            String[] sessionDurations = getResources().getStringArray(R.array.session_duration_display);
            ArrayAdapter<String> newAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sessionDurations);
            sessionDurationInput.setAdapter(newAdapter);
        }
        return false;
    });
}

}
