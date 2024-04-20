package com.example.eassyappointmentfe.businessActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eassyappointmentfe.R;

public class CreateBusinessActivity extends AppCompatActivity {

    private EditText businessNameEditText;
    private ImageView businessLogoImageView;
    private Button uploadLogoButton;
    private Button createBusinessButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_business);

        businessNameEditText = findViewById(R.id.businessNameEditText);
//        businessLogoImageView = findViewById(R.id.businessLogoImageView);
        uploadLogoButton = findViewById(R.id.uploadLogoButton);
        createBusinessButton = findViewById(R.id.createBusinessButton);

        // TODO: Set up button click listeners, image selection, and business creation logic.

    }
}
