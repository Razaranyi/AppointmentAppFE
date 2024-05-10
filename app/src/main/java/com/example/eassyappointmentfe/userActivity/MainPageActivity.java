package com.example.eassyappointmentfe.userActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eassyappointmentfe.DTO.Business;
import com.example.eassyappointmentfe.DTO.Category;
import com.example.eassyappointmentfe.R;
import com.example.eassyappointmentfe.adapters.CategoriesAdapter;
import com.example.eassyappointmentfe.businessActivity.CreateBusinessActivity;
import com.example.eassyappointmentfe.commonActivity.CommonBusinessActivity;
import com.example.eassyappointmentfe.util.ImageUtils;
import com.example.eassyappointmentfe.util.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the main page of the application.
 * It displays the categories of businesses and allows users to search for businesses.
 */
public class MainPageActivity extends AppCompatActivity {

    private List<Category> categories = new ArrayList<>();
    private CategoriesAdapter adapter;
    private EditText searchInput;
    private TextView customerStatus;

    private Button myAppointmentButton;


    /**
     * Initializes the activity.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_main_page);
        RecyclerView rvCategories = findViewById(R.id.rvCategories);
        searchInput = findViewById(R.id.searchInput);
        customerStatus = findViewById(R.id.customerStatus);
        myAppointmentButton = findViewById(R.id.btnAppointments);
        setUpCustomerStatus();
        setUpAppointmentsButton();



        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        populatePage();

        adapter = new CategoriesAdapter(this, categories);
        rvCategories.setAdapter(adapter);


        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });


    }

    /**
     * Sets up the appointments button to navigate to the appointments activity.
     */

    private void setUpAppointmentsButton() {
        myAppointmentButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainPageActivity.this, AppointmentActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Fetches the businesses and categories to populate the main page.
     */
    private void populatePage() {
        new Thread(() -> {
            try {
                // Fetch favorites first
                String favoritesResponse = NetworkUtils.performGetRequest(
                        this,
                        "favorites/my-favorites",
                        true);
                JSONObject favoritesJson = new JSONObject(favoritesResponse);
                JSONArray favoriteBusinessesJsonArray = favoritesJson.getJSONArray("data");

                List<Business> favoriteBusinesses = new ArrayList<>();
                for (int i = 0; i < favoriteBusinessesJsonArray.length(); i++) {
                    long businessId = favoriteBusinessesJsonArray.getJSONObject(i).getLong("businessId");
                    favoriteBusinesses.add(fetchBusinessById(businessId, true));
                }

                runOnUiThread(() -> {
                    if (!favoriteBusinesses.isEmpty()) {
                        //add the favorite as a category
                        Category favoritesCategory = new Category(Long.MAX_VALUE, "Favorites", favoriteBusinesses);
                        categories.add(0, favoritesCategory);
                    }
                    fetchOtherCategories();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Fetches a business by its ID.
     *
     * @param businessId The ID of the business to fetch.
     * @param isFavorite Whether the business is a favorite.
     * @return The fetched business.
     * @throws JSONException If there is an error parsing the JSON response.
     * @throws IOException   If there is an error fetching the business.
     */
    private Business fetchBusinessById(long businessId, boolean isFavorite) throws JSONException, IOException {
        String response = NetworkUtils.performGetRequest(
                this,
                "business/get-business-by-id/" + businessId,
                true);
        JSONObject jsonObject = new JSONObject(response);
        return parseBusiness(jsonObject.getJSONObject("data"), isFavorite);
    }

    /**
     * Fetches the other categories of businesses.
     */
    private void fetchOtherCategories() {
        new Thread(() -> {
            try {
                String response = NetworkUtils.performGetRequest(this,
                        "categories/get-all",
                        true);
                List<Category> additionalCategories = parseCategoriesAndBusinesses(response);
                runOnUiThread(() -> {
                    categories.addAll(additionalCategories);
                    adapter.setCategories(categories); // Update the adapter's list
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Parses the JSON response to extract category and business information.
     *
     * @param response The JSON response from the server.
     * @return List of categories.
     */
    private List<Category> parseCategoriesAndBusinesses(String response) {
        List<Category> categories = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject categoryJson = jsonArray.getJSONObject(i);
                long id = categoryJson.getLong("id");
                String name = categoryJson.getString("name");
                List<Business> businesses = getBusinessesForCategory(categoryJson.getJSONArray("businessIds"));
                categories.add(new Category(id, name, businesses));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return categories;
    }

    /**
     * Fetches the businesses for a category.
     *
     * @param businessIdsJsonArray The JSON array of business IDs.
     * @return List of businesses.
     */
    private List<Business> getBusinessesForCategory(JSONArray businessIdsJsonArray) {
        List<Business> businesses = new ArrayList<>();
        try {
            for (int i = 0; i < businessIdsJsonArray.length(); i++) {
                long businessId = businessIdsJsonArray.getLong(i);
                businesses.add(fetchBusinessById(businessId, false));
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing business details: " + e.getMessage(), e);
        }
        return businesses;
    }

    /**
     * Parses the JSON response to extract business information.
     *
     * @param businessJson The JSON object containing business information.
     * @param isFavorite   Whether the business is a favorite.
     * @return The parsed business.
     */
    private Business parseBusiness(JSONObject businessJson, boolean isFavorite) {
        try {
            long id = businessJson.getLong("id");
            String name = businessJson.getString("name");
            byte[] decodedImage = Base64.decode(businessJson.optString("logoImage"), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
            Uri image = ImageUtils.getImageUri(this, decodedByte, name);
            return new Business(id, name, null, image, isFavorite);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing business details: " + e.getMessage(), e);
        }
    }

    /**
     * Fetches categories from the server and populates the category spinners.
     */
    private void setUpCustomerStatus() {
        customerStatus.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    String response = NetworkUtils.performGetRequest(
                            this,
                            "business/my-business",
                            true
                    );
                    JSONObject jsonObject = new JSONObject(response);
                    System.out.println("Response: " + jsonObject.toString());

                    //TODO: handle this annoying bug
                    int status = 0;
                    boolean success = false;
                    if (jsonObject.has("status")){
                        status = jsonObject.getInt("status"); // success or status?!?!?!
                    }
                    else{
                        success = jsonObject.getBoolean("success");
                    }

                    if (status == HttpURLConnection.HTTP_OK || success) {
                        Intent intent = new Intent(MainPageActivity.this, CommonBusinessActivity.class);
                        intent.putExtra("businessId", NetworkUtils.processResponse(jsonObject, "id"));
                        intent.putExtra("businessName", "My Business");
                        intent.putExtra("isCustomer", false);
                        startActivity(intent);
                    }else {
                        String errorMessage = jsonObject.getString("message");
                        if (errorMessage.contains("Resource not found: Business not found")) {
                        Intent intent = new Intent(MainPageActivity.this, CreateBusinessActivity.class);
                        startActivity(intent);
                    } else {
                        throw new RuntimeException("Error fetching business details: " + errorMessage);
                    }
                }
                } catch (JSONException | IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        });
    }
}
