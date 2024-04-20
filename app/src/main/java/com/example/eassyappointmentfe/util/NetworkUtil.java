package com.example.eassyappointmentfe.util;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtil {

    private static final String TAG = "NetworkUtil";

    /**
     * Performs a POST request to the specified URL with the provided JSON data.
     *
     * @param urlString The URL to send the POST request to.
     * @param postData  The JSON object containing the data to be sent with the request.
     * @return A JSONObject containing the response code and the response body.
     */
    public static JSONObject performPostRequest(String urlString, JSONObject postData) {
        HttpURLConnection connection = null;
        JSONObject responseJson = new JSONObject();
        try {
            URL url = new URL(urlString);

            // Open the connection and set the request method to POST.
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            // Set the headers to accept and send JSON.
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

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
            StringBuffer response = new StringBuffer();
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
}