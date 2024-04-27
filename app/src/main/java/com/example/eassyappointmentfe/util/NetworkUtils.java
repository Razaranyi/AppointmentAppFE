package com.example.eassyappointmentfe.util;

import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

    private static final String TAG = "NetworkUtil";

    /**
     * Performs a POST request to the specified URL with the provided JSON data.
     *
     * @param urlString The URL to send the POST request to.
     * @param postData  The JSON object containing the data to be sent with the request.
     * @param isTokenRequired A boolean indicating whether the request requires a token.
     * @return A JSONObject containing the response code and the response body.
     */
    public static JSONObject performPostRequest(Context context, String urlString, JSONObject postData, boolean isTokenRequired) {
        System.out.println("perform post request method" +
                        "\n url: " + urlString );
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
        System.out.println(responseJson.toString());
        return responseJson;
    }

    /**
     * Performs a GET request to the specified URL.
     *
     * @param urlString The URL to send the GET request to.
     * @param isTokenRequired A boolean indicating whether the request requires a token.
     * @return A string containing the response body.
     */

    public static String performGetRequest(Context context, String urlString, boolean isTokenRequired) {
        HttpURLConnection urlConnection = null;
        StringBuilder result = new StringBuilder();
        System.out.println("perform get request method" +
                "\n url: " + urlString);
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            urlConnection.setRequestProperty("Accept", "application/json");
            if (isTokenRequired) {
                urlConnection.setRequestProperty("Authorization", "Bearer " + TokenManager.getToken(context));
                System.out.println("Bearer " + TokenManager.getToken(context));
            }
            urlConnection.connect();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        System.out.println("url: " + urlString + "\n" +
                "result" + result.toString());

        return result.toString();
    }

    public static String processResponse(JSONObject response, String key) {
        System.out.println("process response method");
        if (key.equals("message")) {
            JSONObject responseObject = response.optJSONObject("response");
            if (responseObject != null) {
                String message = responseObject.optString("message");
                if (message.contains("=")) {
                    // Extract the value after '='
                    return message.substring(message.indexOf('=') + 1, message.indexOf('}'));
                }
                return message;
            }
            return response.optString("message");
        }
        JSONObject dataObject = response.optJSONObject("data");
        if (dataObject != null) {
            return dataObject.optString(key);
        } else {
            JSONObject error = response.optJSONObject("response");
            if (error != null) {
                JSONObject errorObject = error.optJSONObject("error");
                System.out.println("error: " + errorObject.toString());
                return error.optString("message");
            }
            return null;
        }
    }


}

