package com.example.eassyappointmentfe.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

public class TokenManager {
    private static final String SHARED_PREFS_FILE = "com.example.eassyappointmentfe.SHARED_PREFS";
    private static final String TOKEN_KEY = "com.example.eassyappointmentfe.TOKEN_KEY";

    public static void saveToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        return prefs.getString(TOKEN_KEY, null);
    }


}