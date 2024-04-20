package com.example.eassyappointmentfe.businessActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

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
        fetchCategories();

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



}
