package com.example.eassyappointmentfe.businessActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eassyappointmentfe.R;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

public class CreateNewEmployeeActivity extends AppCompatActivity {
    // You can define interfaces to communicate with the activity if needed
    public interface OnBreakAddedListener {
        void onBreakAdded(Time breakStart, Time breakEnd);
    }

    private TextInputEditText employeeNameInput;
    private TextInputEditText employeeAddressInput;
    private ImageView employeeImageView;
    private Button addMoreBreaksButton;
    private Button chooseDaysButton;
    private Button uploadEmployeeImageButton;
    private Button createEmployeeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_employee);

        employeeImageView = findViewById(R.id.employeeImageView);
        addMoreBreaksButton = findViewById(R.id.addMoreBreaksButton);
        chooseDaysButton = findViewById(R.id.chooseDaysButton);
        uploadEmployeeImageButton = findViewById(R.id.uploadEmployeeImageButton);
        createEmployeeButton = findViewById(R.id.createEmployeeButton);

        // Initialize button actions
        setupAddMoreBreaksButton();
        setupChooseDaysButton();
        setupUploadEmployeeImageButton();
        setupCreateEmployeeButton();
    }

    private void setupAddMoreBreaksButton() {
        addMoreBreaksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new instance of BreakTimeBottomSheetDialogFragment
                BreakTimeBottomSheetDialogFragment breakTimeBottomSheetDialogFragment = new BreakTimeBottomSheetDialogFragment();

                // Set the listener to handle the actions from the dialog
                breakTimeBottomSheetDialogFragment.setListener(new BreakTimeBottomSheetDialogFragment.BottomSheetListener() {
                    @Override
                    public void onBreaksAdded(List<String> breaks) {
                        // Handle the breaks added by the user
                    }

                    @Override
                    public void onDaysSelected(Set<DayOfWeek> days) {
                        // Handle the days selected by the user
                    }
                });

                // Show the bottom sheet dialog fragment
                breakTimeBottomSheetDialogFragment.show(getSupportFragmentManager(), breakTimeBottomSheetDialogFragment.getTag());
            }
        });
    }

    private void setupChooseDaysButton() {
        chooseDaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement your code to choose days of the week here
            }
        });
    }

    private void setupUploadEmployeeImageButton() {
        uploadEmployeeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement your code to upload an employee image here
            }
        });
    }

    private void setupCreateEmployeeButton() {
        createEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement your code to create a new employee here
            }
        });
    }

}