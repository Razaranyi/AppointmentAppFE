package com.example.eassyappointmentfe;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.eassyappointmentfe.authActivity.LoginActivity;

import java.io.IOException;

public class EasyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            if (throwable instanceof JWTException ||
                    (throwable instanceof IOException && throwable.getMessage().contains("Token has expired"))) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                // Display a Toast message
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(this, "You're logged out. Please log in.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}