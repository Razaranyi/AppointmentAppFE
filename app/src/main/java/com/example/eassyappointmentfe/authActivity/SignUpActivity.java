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
import com.example.eassyappointmentfe.userActivity.MainPageActivity;
import com.example.eassyappointmentfe.util.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;


/**
 * This class represents the sign up activity of the application.
 * It handles user registration and navigation to the login activity.
 */
public class SignUpActivity extends AppCompatActivity {
    private EditText userEmailAddress, userPassword, userName;

    /**
     * Initializes the activity.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
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

    /**
     * Sets up the login prompt with a clickable span that navigates to the login activity.
     *
     * @param textViewLoginPrompt The TextView that contains the login prompt.
     */
    private void setUpClickableLoginPrompt(TextView textViewLoginPrompt) {
        String loginPromptText = getString(R.string.login_prompt);
        SpannableString spannableString = new SpannableString(loginPromptText);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        };

        int startIndexOfLink = loginPromptText.indexOf(getString(R.string.login_keyword));
        spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + getString(R.string.login_keyword).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewLoginPrompt.setText(spannableString);
        textViewLoginPrompt.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Sets up the sign up button with a click listener that sends a sign up request to the server.
     */
    private void setUpSignUpButton() {
        Button signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(v -> {
            JSONObject postData = new JSONObject();
            try {
                postData.put("email", userEmailAddress.getText().toString().trim());
                postData.put("password", userPassword.getText().toString().trim());
                postData.put("fullName", userName.getText().toString().trim());

                new Thread(() -> {
                    JSONObject response = NetworkUtils.performPostRequest(
                            this,
                            "auth/sign-up",
                            postData,
                            false
                    );
                    processResponse(response);
                }).start();
            } catch (JSONException e) {
                Log.e("SignUpActivity", "JSON Exception: ", e);
            }
        });
    }

    /**
     * Processes the response from the server and navigates to the main page if the sign up is successful.
     *
     * @param response The JSON response from the server.
     */
    private void processResponse(JSONObject response) {
        try {
            int status = response.getInt("status");
            String message = response.getJSONObject("response").getString("message");

            if (status == HttpURLConnection.HTTP_OK) {
                String token = response.getJSONObject("response").getString("token");
                saveToken(this, token);
                showToast(message);
                Intent intent = new Intent(SignUpActivity.this, MainPageActivity.class);
                startActivity(intent);
            } else {
                showToast(message);
            }
        } catch (JSONException e) {
            Log.e("SignUpActivity", "JSON Parsing Exception: ", e);
            showToast("An error occurred while processing the response.");
        }
    }


    /**
     * Displays a toast message on the UI thread.
     *
     * @param message The message to display.
     */
    private void showToast(final String message) {
        runOnUiThread(() -> Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_LONG).show());
    }
}
