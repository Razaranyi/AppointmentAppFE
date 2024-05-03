package com.example.eassyappointmentfe.businessActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eassyappointmentfe.DTO.Category;
import com.example.eassyappointmentfe.R;
import com.example.eassyappointmentfe.util.ImageUtils;
import com.example.eassyappointmentfe.util.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Activity to create a new business.
 * It allows users to input business information and upload an image.
 */
public class CreateBusinessActivity extends AppCompatActivity {

    private static final String SHARED_PREFS_FILE = "com.example.eassyappointmentfe.SHARED_PREFS";
    private static final String BUSINESS_NAME = "com.example.eassyappointmentfe.BUSINESS_NAME";
    private EditText businessNameEditText;
    private ImageView businessLogoImageView;
    private AutoCompleteTextView category1;
    private AutoCompleteTextView category2;
    private Button uploadLogoButton;
    private Button createBusinessButton;

    // Activity result launcher for image picker
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            this::handleImageSelection
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_business);

        initializeViews();
        fetchCategories();
        setUpCreateBusinessButton();
    }

    /**
     * Initialize views and set up listeners.
     */
    private void initializeViews() {
        businessNameEditText = findViewById(R.id.businessNameEditText);
        businessLogoImageView = findViewById(R.id.businessLogoImageView);
        uploadLogoButton = findViewById(R.id.uploadLogoButton);
        createBusinessButton = findViewById(R.id.createBusinessButton);
        category1 = findViewById(R.id.businessCategory1);
        category2 = findViewById(R.id.businessCategory2);

        uploadLogoButton.setOnClickListener(v -> mGetContent.launch("image/*"));
    }

    /**
     * Handles the result of image selection from the image picker.
     * Sets the image to the ImageView and stores the image bytes for later use.
     * @param uri URI of the selected image.
     */
    private void handleImageSelection(Uri uri) {
        if (uri == null) { // No image selected
            return;
        }
        ImageUtils.handleImageSelection(this, uri, businessLogoImageView, Bitmap.CompressFormat.JPEG, 100);
    }

    /**
     * Fetches categories from the server and populates the category spinners.
     */
    private void fetchCategories() {
        new Thread(() -> {
            String response = null;
            try {
                response = NetworkUtils.performGetRequest(this,"http://10.0.2.2:8080/api/categories/all", true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            List<Category> categoryList = parseCategories(response);
            List<String> categoryNames = categoryList.stream()
                    .map(Category::getName)
                    .collect(Collectors.toList());
            runOnUiThread(() -> populateSpinner(categoryNames));
        }).start();
    }

    /**
     * Populates category spinners with fetched category names.
     * @param categoryNames List of category names.
     */
    private void populateSpinner(List<String> categoryNames) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categoryNames);

        category1.setAdapter(adapter);
        category2.setAdapter(adapter);
    }

    /**
     * Parses the JSON response to extract category information.
     * @param jsonResponse JSON response from the server.
     * @return List of categories.
     */
    private List<Category> parseCategories(String jsonResponse) {
        List<Category> categoryList = new ArrayList<>();
        try {
            JSONArray categoriesJsonArray = new JSONArray(jsonResponse);
            for (int i = 0; i < categoriesJsonArray.length(); i++) {
                JSONObject categoryJson = categoriesJsonArray.getJSONObject(i);
                Category category = new Category(
                        categoryJson.getLong("id"),
                        categoryJson.getString("name")
                );
                categoryList.add(category);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return categoryList;
    }

    /**
     * Sets up the click listener for the create business button.
     */
    private void setUpCreateBusinessButton() {
        createBusinessButton.setOnClickListener(v -> createBusiness());
    }

    /**
     * Collects data from the form and sends a request to create a new business.
     */
    private void createBusiness() {
        try {
            JSONObject dataObject = new JSONObject();
            dataObject.put("name", businessNameEditText.getText().toString().trim());
            dataObject.put("businessCategories", new JSONArray().put(category1.getText().toString()).put(category2.getText().toString()));

            if (businessLogoImageView.getTag() != null) {
                byte[] imageBytes = (byte[]) businessLogoImageView.getTag();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                dataObject.put("logoImage", encodedImage);
            }

            JSONObject rootObject = new JSONObject();
            rootObject.put("data", dataObject);

            new Thread(() -> {
                String response = String.valueOf(NetworkUtils.performPostRequest(
                        this,
                        "http://10.0.2.2:8080/api/business/create",
                        rootObject,
                        true
                ));

                try {
                    JSONObject responseJson = new JSONObject(response);
                    String message = NetworkUtils.processResponse(responseJson, "message");

                    int status = responseJson.getInt("status");
                    runOnUiThread(() -> {
                        Toast.makeText(CreateBusinessActivity.this, message, Toast.LENGTH_LONG).show();
                        if (status == HttpURLConnection.HTTP_OK) {
                            Intent intent = new Intent(CreateBusinessActivity.this, CreateBranchActivity.class);
                            String businessId = NetworkUtils.processResponse(responseJson, "id");

                            System.out.println("Business Activity businessId: " + businessId);
                            intent.putExtra("businessId", businessId);
                            Intent mangmentIntent = new Intent(CreateBusinessActivity.this, BusinessManagementActivity.class);
                            mangmentIntent.putExtra("businessId", businessId);
                            SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(BUSINESS_NAME, businessNameEditText.getText().toString().trim());



                            startActivity(intent);
                        }
                    });
                } catch (JSONException e) {
                    Log.e("CreateBusinessActivity", "JSON Exception: ", e);
                    runOnUiThread(() -> {
                        Toast.makeText(CreateBusinessActivity.this, "Failed to create business", Toast.LENGTH_LONG).show();
                    });
                }
            }).start();
        } catch (JSONException e) {
            Log.e("CreateBusinessActivity", "JSON Exception: ", e);
        }
    }
}
