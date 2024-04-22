package com.example.eassyappointmentfe.businessActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CreateBusinessActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText businessNameEditText;
    private ImageView businessLogoImageView;
    private AutoCompleteTextView categoty1;
    private AutoCompleteTextView categoty2;
    private Button uploadLogoButton;
    private Button createBusinessButton;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult( //set up image selection
            new ActivityResultContracts.GetContent(),
            uri -> {
                // Handle the returned Uri
                byte[] imageBytes = ImageUtils.convertImageToByteArray(this, uri, Bitmap.CompressFormat.JPEG, 100);
                if (imageBytes != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    businessLogoImageView.setImageBitmap(bitmap);
                    businessLogoImageView.setTag(imageBytes); // Store bytes in tag for later upload
                } else {
                    Toast.makeText(this, "Failed to process selected image", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_business);

        businessNameEditText = findViewById(R.id.businessNameEditText);
        businessLogoImageView = findViewById(R.id.businessLogoImageView);
        uploadLogoButton = findViewById(R.id.uploadLogoButton);
        createBusinessButton = findViewById(R.id.createBusinessButton);
        categoty1 = findViewById(R.id.businessCategory1);
        categoty2 = findViewById(R.id.businessCategory2);

        uploadLogoButton.setOnClickListener(v -> showFileChooser());
        fetchCategories();
        setUpCreateBusinessButton();
    }


    private void fetchCategories() {
        new Thread(() -> {
            String response = NetworkUtils.performGetRequest(this,"http://10.0.2.2:8080/api/categories/all", true);
            List<Category> categoryList = parseCategories(response);
            List<String> categoryNames = categoryList.stream()
                    .map(Category::getName)
                    .collect(Collectors.toList());
            runOnUiThread(() -> populateSpinner(categoryNames, categoryList));
        }).start();
    }

    private void populateSpinner(List<String> categoryNames, List<Category> fullCategories) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categoryNames);

        categoty1.setAdapter(adapter);
        categoty2.setAdapter(adapter);

        // Set listeners to handle selection, map back to Category objects
        categoty1.setOnItemClickListener((parent, view, position, id) -> {
        });
        categoty2.setOnItemClickListener((parent, view, position, id) -> {
        });
    }


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

    private void setUpCreateBusinessButton() {
        createBusinessButton.setOnClickListener(v -> {
            try {
                JSONArray categoriesArray = new JSONArray();
                if (categoty1.getText().toString().isEmpty() || categoty2.getText().toString().isEmpty()) { //make sure both categories are selected, might need to change this
                    Toast.makeText(this, "Please select two categories", Toast.LENGTH_SHORT).show();
                    return;
                }

                // add categories to an array

                categoriesArray.put(categoty1.getText().toString());
                categoriesArray.put(categoty2.getText().toString());

                // Wrapping data in another JSONObject as per to the structure of the request body
                JSONObject dataObject = new JSONObject();
                dataObject.put("name", businessNameEditText.getText().toString().trim());
                dataObject.put("businessCategories", categoriesArray);

                // include image data if available
                if (businessLogoImageView.getTag() != null) {
                    byte[] imageBytes = (byte[]) businessLogoImageView.getTag();
                    String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    System.out.println("encodedImage " + encodedImage);
                    dataObject.put("logoImage", encodedImage);
                }


                JSONObject rootObject = new JSONObject();
                rootObject.put("data", dataObject);

                System.out.println("JSON payload: " + rootObject.toString());


                new Thread(() -> {
                    String response = String.valueOf(NetworkUtils.performPostRequest(
                            this,
                            "http://10.0.2.2:8080/api/businesses/create",
                            rootObject,
                            true
                    ));

                    processResponse(response);
                }).start();
            } catch (JSONException e) {
                Log.e("CreateBusinessActivity", "JSON Exception: ", e);
            }
        });
    }

    private void processResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            int status = jsonResponse.getInt("status");
            String message = jsonResponse.getJSONObject("response").getString("message");

            runOnUiThread(() -> {
                Toast.makeText(CreateBusinessActivity.this, message, Toast.LENGTH_LONG).show();
            });
        } catch (JSONException e) {
            Log.e("CreateBusinessActivity", "JSON Parsing Exception: ", e);
            runOnUiThread(() -> {
                Toast.makeText(CreateBusinessActivity.this, "An error occurred while processing the response.", Toast.LENGTH_LONG).show();
            });
        }
    }


    public void showFileChooser() {
        mGetContent.launch("image/*");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            byte[] imageBytes = ImageUtils.convertImageToByteArray(this, selectedImageUri, Bitmap.CompressFormat.JPEG, 100);
            if (imageBytes != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                businessLogoImageView.setImageBitmap(bitmap);
                businessLogoImageView.setTag(imageBytes); // Store bytes in tag for later upload
            } else {
                Toast.makeText(this, "Failed to process selected image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
