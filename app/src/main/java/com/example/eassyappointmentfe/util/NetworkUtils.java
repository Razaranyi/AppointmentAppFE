package com.example.eassyappointmentfe.util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.UiThread;

import com.example.eassyappointmentfe.JWTException;
import com.example.eassyappointmentfe.authActivity.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NetworkUtils {

    private static final String TAG = "NetworkUtil";
    private static final String BASE_URL = "http://10.0.2.2:8080/api/";


    /**
     * Performs a POST request to the specified URL with the provided JSON data.
     *
     * @param uri             The URL to send the POST request to.
     * @param postData        The JSON object containing the data to be sent with the request.
     * @param isTokenRequired A boolean indicating whether the request requires a token.
     * @return A JSONObject containing the response code and the response body.
     */
    public static JSONObject performPostRequest(Context context, String uri, JSONObject postData, boolean isTokenRequired) {
        System.out.println("perform post request method" +
                "\n url: " + uri);
        HttpURLConnection connection = null;
        JSONObject responseJson = new JSONObject();
        String finalUrl = BASE_URL + uri;
        try {
            URL url = new URL(finalUrl);

            // Open the connection and set the request method to POST.
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            // Set the headers to accept and send JSON.
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            if (isTokenRequired) {
                connection.setRequestProperty("Authorization", "Bearer " + TokenManager.getToken(context));
            }

            // Enable output for the connection to send data.
            connection.setDoOutput(true);

            // Write the JSON data to the connection output stream.
            try (DataOutputStream os = new DataOutputStream(connection.getOutputStream())) {
                os.writeBytes(postData.toString());
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            // Choose the appropriate stream based on the response code.
            InputStream inputStream = responseCode <= HttpURLConnection.HTTP_OK ?
                    connection.getInputStream() : connection.getErrorStream();

            // Read the input stream into a string buffer.
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();

            // Place the response code and body into a JSON object.
            responseJson.put("status", responseCode);
            responseJson.put("response", new JSONObject(response.toString()));

        } catch (Exception e) {
            // Handle any exceptions and put an error message into the response JSON.
            try {
                responseJson.put("status", 500);
                responseJson.put("response", new JSONObject().put("message", "Error connecting to server."));
            } catch (JSONException jsonException) {
                Log.e(TAG, "JSON Exception: ", jsonException);
            }
            Log.e(TAG, "Network Exception: ", e);
        } finally {
            // Disconnect the connection to free up resources.
            if (connection != null) {
                connection.disconnect();
            }
        }
        System.out.println(responseJson.toString());
        return responseJson;
    }

    // had to change configuration in spring boot to allow it not across HTTPS but HTTP, therefore its not in used but cancelling booking can be done here theoretically
    public static JSONObject performDeleteRequest(Context context, String uri) {
        System.out.println("perform delete request method" +
                "\n url: " + uri);
        String URL = BASE_URL + uri;

        HttpURLConnection connection = null;
        JSONObject responseJson = new JSONObject();
        try {
            URL url = new URL(URL);

            // Open the connection and set the request method to DELETE.
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            // Set the headers to accept and send JSON.
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + TokenManager.getToken(context));

            int responseCode = connection.getResponseCode();
            // Choose the appropriate stream based on the response code.
            InputStream inputStream = responseCode <= HttpURLConnection.HTTP_OK ?
                    connection.getInputStream() : connection.getErrorStream();

            // Read the input stream into a string buffer.
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();

            // Place the response code and body into a JSON object.
            responseJson.put("status", responseCode);
            responseJson.put("response", new JSONObject(response.toString()));

        } catch (Exception e) {
            // Handle any exceptions and put an error message into the response JSON.
            try {
                responseJson.put("status", 500);
                responseJson.put("response", new JSONObject().put("message", "Error connecting to server."));
            } catch (JSONException jsonException) {
                Log.e(TAG, "JSON Exception: ", jsonException);
            }
            Log.e(TAG, "Network Exception: ", e);
        } finally {
            // Disconnect the connection to free up resources.
            if (connection != null) {
                connection.disconnect();
            }
        }
        return responseJson;
    }

    /**
     * Performs a GET request to the specified URL.
     *
     * @param uri             The URL to send the GET request to.
     * @param isTokenRequired A boolean indicating whether the request requires a token.
     * @return A string containing the response body.
     */

    public static String performGetRequest(Context context, String uri, boolean isTokenRequired) throws IOException {
        HttpURLConnection urlConnection = null;
        StringBuilder result = new StringBuilder();
        System.out.println("perform get request method" +
                "\n url: " + uri);
        String finalUrl = BASE_URL + uri;
        try {
            URL url = new URL(finalUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            urlConnection.setRequestProperty("Accept", "application/json");
            if (isTokenRequired) {
                urlConnection.setRequestProperty("Authorization", "Bearer " + TokenManager.getToken(context));
            }
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                throw new JWTException("Token has expired. Please log in again.");
            }

            InputStream in;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                in = new BufferedInputStream(urlConnection.getInputStream());
            } else {
                in = new BufferedInputStream(urlConnection.getErrorStream());
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            System.out.println("Exception caught: " + e.toString());
            if (e instanceof JWTException) {
                handleJWTException(context);
            }
            throw e;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return result.toString();
    }

    public static String getBusinessId(Context context) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> result = executor.submit(() -> {
            return NetworkUtils.performGetRequest(
                    context,
                    "business/get-id",
                    true
            );
        });

        try {
            String response = result.get();  // Blocks until the response is available
            return NetworkUtils.processResponse(new JSONObject(response), "id");
        } catch (ExecutionException | InterruptedException | JSONException e) {
            Log.e("CreateBranchActivity", "Exception: ", e);
            return null;
        } finally {
            executor.shutdown();
        }
    }

    public static String processResponse(JSONObject response, String key) {
        System.out.println("process response method: " + response.toString() + " key: " + key);

        // Check if the response is nested inside a "response" key, typical for some API responses
        JSONObject innerResponse = response.optJSONObject("response");
        if (innerResponse != null) {
            response = innerResponse;  // Use the inner response for further processing
        }

        // For extracting message regardless of its location
        if (key.equals("message")) {
            String message = response.optString("message");
            if (message.isEmpty()) {
                // This covers the case where the message might be deeper in another object within 'response'
                JSONObject errorObject = response.optJSONObject("error");
                message = (errorObject != null) ? errorObject.optString("message") : null;
            }
            return message;
        }

        // Handle the data extraction uniformly
        JSONObject dataObject = response.optJSONObject("data");
        if (dataObject != null) {
            System.out.println("Data object: " + dataObject.toString());
            return dataObject.optString(key);
        } else {
            // If there's no "data" object and the key is not "message", try getting the key directly from the response
            return response.optString(key);
        }
    }

    public static boolean[] convertJsonArrayToBooleanArray(JSONArray jsonArray) throws JSONException {
        boolean[] workingDays = new boolean[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            workingDays[i] = jsonArray.getBoolean(i); // Assumes the JSON array contains boolean values
        }
        return workingDays;
    }

    public static Set<Long> convertJsonArrayToSet(JSONArray jsonArray) throws JSONException {
        Set<Long> resultSet = new HashSet<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            resultSet.add(jsonArray.getLong(i)); // Assumes the JSON array contains long values
        }
        return resultSet;
    }

    private static void handleJWTException(Context context) {
        // Create a new Handler to run code on the UI thread
        new Handler(Looper.getMainLooper()).post(() -> {
            // Display a toast message
            Toast.makeText(context, "Token has expired. Please log in again.", Toast.LENGTH_LONG).show();

            // Redirect to LoginActivity
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        });
    }
}


