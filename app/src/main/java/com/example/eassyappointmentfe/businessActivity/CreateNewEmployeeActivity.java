package com.example.eassyappointmentfe.businessActivity;

import static com.example.eassyappointmentfe.util.NetworkUtils.processResponse;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eassyappointmentfe.R;
import com.example.eassyappointmentfe.util.NetworkUtils;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_employee);
        employeeNameInput = findViewById(R.id.employeeNameInput);
        employeeImageView = findViewById(R.id.employeeImageView);
        addMoreBreaksButton = findViewById(R.id.addMoreBreaksButton);
        chooseDaysButton = findViewById(R.id.chooseDaysButton);
        uploadEmployeeImageButton = findViewById(R.id.uploadEmployeeImageButton);
        createEmployeeButton = findViewById(R.id.createEmployeeButton);
        sessionDurationInput = findViewById(R.id.sessionDurationInput);

        setupAddMoreBreaksButton();
        setupDaysOfTheWeekButton();
        setupCreateEmployeeButton();
        setSpinnerButton();
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
                selectedDuration = null;
            }
            // Use the selected duration
            employeeData.put("sessionDuration", selectedDuration);


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


    private void sendEmployeeData(JSONObject employeeData) throws JSONException, ExecutionException, InterruptedException {
        String businessId = getBusinessId();
        String branchId = getBranchId(businessId, "Branch");

        Thread thread = new Thread(() -> {
            JSONObject response = NetworkUtils.performPostRequest(
                    this,
                    "http://10.0.2.2:8080/api/business/" + businessId + "/" + branchId + "/service-provider/create",
                    employeeData,
                    true
            );
            runOnUiThread(() -> Toast.makeText(CreateNewEmployeeActivity.this, processResponse(response, "message"), Toast.LENGTH_LONG).show());
        });
        thread.start();
    }

    private String getBusinessId() throws JSONException, ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> result = executor.submit(() -> {
            return NetworkUtils.performGetRequest(
                    this, "http://10.0.2.2:8080/api/business/get-id", true
            );
        });
// Ensure the task has completed and get the result
        String jsonResult = result.get();

        JSONObject json = new JSONObject(jsonResult);
        String businessId = processResponse(json, "id");
        return businessId;
    }

    private String getBranchId(String businessId, String branchName) throws JSONException, ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> result = executor.submit(() -> {
            return NetworkUtils.performGetRequest(
                    this,
                    "http://10.0.2.2:8080/api/business/" + businessId + "/branch/" + branchName + "/get-id",
                    true
            );
        });

        String jsonResult = result.get();
        JSONObject json = new JSONObject(jsonResult);
        String branchId = processResponse(json, "branchId");
        System.out.println(branchId);
        return branchId;
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
            ArrayAdapter<String> newAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sessionDurations);
            sessionDurationInput.setAdapter(newAdapter);
        }
        return false;
    });
}

}
