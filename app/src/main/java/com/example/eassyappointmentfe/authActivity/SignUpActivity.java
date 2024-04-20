package com.example.eassyappointmentfe.authActivity;

import static com.example.eassyappointmentfe.util.TokenManager.saveToken;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eassyappointmentfe.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class SignUpActivity extends AppCompatActivity {
    private EditText userEmailAddress;
    private EditText userPassword;
    private EditText userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userEmailAddress = findViewById(R.id.userEmailAddress);
        userPassword = findViewById(R.id.userPassword);
        userName = findViewById(R.id.userName);
        TextView textViewLoginPrompt = findViewById(R.id.textViewLoginPrompt);

        setUpClickableLoginPrompt(textViewLoginPrompt);
        setUpSignUpButton();
    }

    private void setUpClickableLoginPrompt(TextView textViewLoginPrompt) {  // set up already have an account prompt as clickable
        String loginPromptText = getString(R.string.login_prompt);
        SpannableString spannableString = new SpannableString(loginPromptText);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        };

        String loginKeyword = getString(R.string.login_keyword);
        int startIndexOfLink = loginPromptText.indexOf(loginKeyword);
        spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + loginKeyword.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewLoginPrompt.setText(spannableString);
        textViewLoginPrompt.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setUpSignUpButton() {
        Button signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(v -> {
            String email = userEmailAddress.getText().toString().trim();
            String password = userPassword.getText().toString().trim();
            String name = userName.getText().toString().trim();
            makePostRequest(email, password, name);
        });
    }

    private void makePostRequest(String email, String password, String name) { // call sign up endpoint
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                connection = getHttpURLConnection(email, password, name);
                int responseCode = connection.getResponseCode();
                String responseMessage = getResponseMessage(connection, responseCode);

                String finalResponseMessage;
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    finalResponseMessage = getString(R.string.signup_success);

                    String token = new JSONObject(responseMessage).getString("token"); // extract token & save token for later use
                    saveToken(SignUpActivity.this, token);

                } else { // error occurred
                    finalResponseMessage = extractErrorMessageFromResponse(responseMessage);
                }

                runOnUiThread(() -> Toast.makeText(SignUpActivity.this, finalResponseMessage, Toast.LENGTH_LONG).show());

            } catch (IOException | JSONException e) {
                Log.e("SignUpActivity", "Error in makePostRequest", e);
                runOnUiThread(() ->
                        Toast.makeText(SignUpActivity.this, getString(R.string.error_message, e.getMessage()), Toast.LENGTH_LONG).show()
                );
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    private String extractErrorMessageFromResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.getString("message");
        } catch (JSONException e) {
            Log.e("SignUpActivity", "JSON parsing error", e);
            return "An error occurred.";
        }
    }


    private String getResponseMessage(HttpURLConnection connection, int responseCode) throws IOException {
        InputStream responseStream = (responseCode >= 400) ? connection.getErrorStream() : connection.getInputStream();
        Scanner scanner = new Scanner(responseStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }


    private static HttpURLConnection getHttpURLConnection(String email, String password, String name) throws IOException, JSONException {
        URL url = new URL("http://10.0.2.2:8080/api/auth/sign-up");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        connection.setDoInput(true);

        JSONObject jsonParam = new JSONObject();
        jsonParam.put("email", email);
        jsonParam.put("password", password);
        jsonParam.put("fullName", name);

        try (DataOutputStream os = new DataOutputStream(connection.getOutputStream())) {
            os.writeBytes(jsonParam.toString());
            os.flush();
        }
        return connection;
    }
}
