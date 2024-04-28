package com.example.eassyappointmentfe.util;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.widget.EditText;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Locale;

public class TimeUtil {

    public static void showTimePickerDialog(Context context, final EditText timeInput) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                (view, hourOfDay, minuteOfHour) -> {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    timeInput.setText(formattedTime);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    public static LocalTime toLocalTime(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            return null; // or return a default LocalTime value
        }
        String[] parts = timeString.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalTime.of(hour, minute);
        }
        return null;
    }
}