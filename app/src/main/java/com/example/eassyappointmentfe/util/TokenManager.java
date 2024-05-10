package com.example.eassyappointmentfe.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * This class manages the token used for authentication.
 * It provides methods to save and retrieve the token from shared preferences.
 */
public class TokenManager {
    private static final String SHARED_PREFS_FILE = "com.example.eassyappointmentfe.SHARED_PREFS";
    private static final String TOKEN_KEY = "com.example.eassyappointmentfe.TOKEN_KEY";

    /**
     * Saves the authentication token to shared preferences.
     *
     * @param context The application context.
     * @param token   The authentication token to save.
     */
    public static void saveToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    /**
     * Retrieves the authentication token from shared preferences.
     *
     * @param context The application context.
     * @return The retrieved authentication token, or null if no token was found.
     */
    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        return prefs.getString(TOKEN_KEY, null);
    }
}