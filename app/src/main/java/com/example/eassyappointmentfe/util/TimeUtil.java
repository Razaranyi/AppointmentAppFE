package com.example.eassyappointmentfe.util;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Locale;


/**
 * This class contains utility methods for handling time-related operations.
 */
public class TimeUtil {

    /**
     * Displays a time picker dialog and sets the selected time to the provided EditText.
     *
     * @param context   The application context.
     * @param timeInput The EditText where the selected time will be set.
     */
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

    /**
     * Displays a date picker dialog and sets the selected date to the provided EditText.
     * Executes the provided Runnable after the date is set.
     *
     * @param context      The application context.
     * @param editText     The EditText where the selected date will be set.
     * @param afterDateSet The Runnable to be executed after the date is set.
     */
    public static void showDatePickerDialog(Context context, EditText editText, Runnable afterDateSet) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year1, monthOfYear, dayOfMonth) -> {
            calendar.set(year1, monthOfYear, dayOfMonth);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            editText.setText(format.format(calendar.getTime()));
            afterDateSet.run();
        }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    /**
     * Converts a time string to a LocalTime object.
     *
     * @param timeString The time string to convert.
     * @return The converted LocalTime object, or null if the time string is null or empty.
     */
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