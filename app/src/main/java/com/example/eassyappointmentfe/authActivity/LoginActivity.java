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
import com.example.eassyappointmentfe.util.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class LoginActivity extends AppCompatActivity {
    private EditText userEmail;
    private EditText userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);
        TextView textViewSignUpPrompt = findViewById(R.id.textViewSignUpPrompt);

        setUpClickableSignUpPrompt(textViewSignUpPrompt);
        setUpLoginButton();
    }

    private void setUpClickableSignUpPrompt(TextView textViewSignUpPrompt) {
        String signUpPromptText = getString(R.string.sign_up_prompt);
        SpannableString spannableString = new SpannableString(signUpPromptText);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        };

        String signUpKeyword = getString(R.string.sign_up_keyword);
        int startIndexOfLink = signUpPromptText.indexOf(signUpKeyword);
        spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + signUpKeyword.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewSignUpPrompt.setText(spannableString);
        textViewSignUpPrompt.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setUpLoginButton() {
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            JSONObject postData = new JSONObject();
            try {
                postData.put("email", userEmail.getText().toString().trim());
                postData.put("password", userPassword.getText().toString().trim());

                new Thread(() -> {
                    JSONObject response = NetworkUtils.performPostRequest(
                            this,
                            "http://10.0.2.2:8080/api/auth/authenticate",
                            postData,
                            false
                    );
                    processResponse(response);
                }).start();
            } catch (JSONException e) {
                Log.e("LoginActivity", "JSON Exception: ", e);
            }
        });
    }

    private void processResponse(JSONObject response) {
        try {
            int status = response.getInt("status");
            JSONObject responseBody = response.getJSONObject("response");
            if (status == HttpURLConnection.HTTP_OK) {
                String token = responseBody.getString("token");
                saveToken(this, token);
                showToast(getString(R.string.login_success));
            } else {
                String message = responseBody.getString("message");
                showToast(message);
            }
        } catch (JSONException e) {
            Log.e("LoginActivity", "JSON Parsing Exception: ", e);
            showToast("An error occurred while processing the response.");
        }
    }

    private void showToast(final String message) {
        runOnUiThread(() -> Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show());
    }
}

