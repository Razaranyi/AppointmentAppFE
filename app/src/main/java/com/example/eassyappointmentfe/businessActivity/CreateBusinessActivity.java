package com.example.eassyappointmentfe.businessActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eassyappointmentfe.DTO.Category;
import com.example.eassyappointmentfe.R;
import com.example.eassyappointmentfe.util.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CreateBusinessActivity extends AppCompatActivity {

    private EditText businessNameEditText;
    private ImageView businessLogoImageView;
    private AutoCompleteTextView categoty1;
    private AutoCompleteTextView categoty2;
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
        categoty1 = findViewById(R.id.businessCategory1);
        categoty2 = findViewById(R.id.businessCategory2);
        fetchCategories();
        setUpCreateBusinessButton();



        // TODO: Set up button click listeners, image selection, and business creation logic.

    }

    private void fetchCategories() {
        new Thread(() -> {
            String response = NetworkUtil.performGetRequest(this,"http://10.0.2.2:8080/api/categories/all", true);
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
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.businessCategory1);
        autoCompleteTextView.setAdapter(adapter);
        AutoCompleteTextView autoCompleteTextView2 = findViewById(R.id.businessCategory2);
        autoCompleteTextView2.setAdapter(adapter);

        // Set listeners to handle selection, map back to Category objects
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            Category selectedCategory = fullCategories.get(position);
            // You can now use selectedCategory.getId() and selectedCategory.getName()
        });
        autoCompleteTextView2.setOnItemClickListener((parent, view, position, id) -> {
            Category selectedCategory = fullCategories.get(position);
            // Similarly, use selectedCategory details as needed
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
        Button createBusinessButton = findViewById(R.id.createBusinessButton);
        createBusinessButton.setOnClickListener(v -> {
            JSONObject postData = new JSONObject();
            try {
                postData.put("name", businessNameEditText.getText().toString().trim());

                // add categories to an array
                JSONArray categoriesArray = new JSONArray();
                categoriesArray.put(categoty1.getText().toString());
                categoriesArray.put(categoty2.getText().toString());

                // Wrapping data in another JSONObject as per to the structure of the request body
                JSONObject dataObject = new JSONObject();
                dataObject.put("name", businessNameEditText.getText().toString().trim());
                dataObject.put("businessCategories", categoriesArray);

                JSONObject rootObject = new JSONObject();
                rootObject.put("data", dataObject);
                System.out.println(rootObject.toString());

                new Thread(() -> {
                    String response = String.valueOf(NetworkUtil.performPostRequest(
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



}
