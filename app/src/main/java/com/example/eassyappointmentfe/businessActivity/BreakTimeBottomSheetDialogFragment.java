package com.example.eassyappointmentfe.businessActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eassyappointmentfe.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BreakTimeBottomSheetDialogFragment extends DialogFragment {

    public static final String SHARED_PREFS_NAME = "BreakPrefs";
    public static final String BREAKS_KEY = "breaks";

    private EditText breakStartTimeInput;
    private EditText breakEndTimeInput;

    private LinearLayout dialogLayout;

    private Button confirmButton;

    public interface BottomSheetListener {
        void onBreaksAdded(String[] breaks);
    }

    private BottomSheetListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_breaks, container, false);

        Button addMoreBreaksButton = v.findViewById(R.id.addBreakButton);
        breakStartTimeInput = v.findViewById(R.id.breakStartTimeInput);
        breakEndTimeInput = v.findViewById(R.id.breakEndTimeInput);
        breakStartTimeInput.setOnClickListener(view -> showTimePickerDialog(breakStartTimeInput));
        breakEndTimeInput.setOnClickListener(view -> showTimePickerDialog(breakEndTimeInput));
        dialogLayout = v.findViewById(R.id.breaksContainer);



        // Declare confirmButton outside of the click listener
        confirmButton = v.findViewById(R.id.confirmBreaksButton);
        confirmButton.setOnClickListener(view1 -> {
            List<String> breaks = new ArrayList<>();
            for (int i = 0; i < dialogLayout.getChildCount(); i++) {
                View child = dialogLayout.getChildAt(i);
                // Only proceed if the child is a LinearLayout containing the EditText pairs
                if (child instanceof LinearLayout) {
                    LinearLayout breakTimePair = (LinearLayout) child;
                    // Check each child within the LinearLayout
                    EditText breakStartTimeInput = null;
                    EditText breakEndTimeInput = null;

                    for (int j = 0; j < breakTimePair.getChildCount(); j++) {
                        View innerChild = breakTimePair.getChildAt(j);
                        // Ensure the innerChild is an instance of EditText
                        if (innerChild instanceof EditText) {
                            EditText editText = (EditText) innerChild;
                            // Determine whether this is the start or end time based on its position
                            if (editText.getHint() != null && editText.getHint().toString().contains("Starting")) {
                                breakStartTimeInput = editText;
                            } else if (editText.getHint() != null && editText.getHint().toString().contains("Ending")) {
                                breakEndTimeInput = editText;
                            }
                        }
                    }

                    // Add the break times to the list if both start and end inputs are not null
                    if (breakStartTimeInput != null && breakEndTimeInput != null) {
                        String breakPeriod = breakStartTimeInput.getText().toString() + "," + breakEndTimeInput.getText().toString();
                        // Split the breakPeriod into an array of times
                        List<String> breakTimes = Arrays.asList(breakPeriod.split(","));
                        breaks.addAll(breakTimes);
                    }
                }
            }

            // Convert breaks list to array
            String[] breaksArray = breaks.toArray(new String[0]);

            if (mListener != null) {
                mListener.onBreaksAdded(breaksArray);
            }
            dismiss();
        });

        addMoreBreaksButton.setOnClickListener(view -> {
            LinearLayout newBreakTimePair = createBreakTimeInputPair();
            int index = dialogLayout.indexOfChild(addMoreBreaksButton);
            dialogLayout.addView(newBreakTimePair, index);
        });

        return v;
    }


    public void setListener(BottomSheetListener listener) {
        mListener = listener;
    }

    private LinearLayout createBreakTimeInputPair() {
        LinearLayout breakTimePairLayout = new LinearLayout(getContext());
        breakTimePairLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        breakTimePairLayout.setOrientation(LinearLayout.HORIZONTAL);
        breakTimePairLayout.setWeightSum(2);

        EditText breakStartTimeInput = createBreakTimeEditText("Starting Hour");
        EditText breakEndTimeInput = createBreakTimeEditText("Ending Hour");

        breakTimePairLayout.addView(breakStartTimeInput);
        breakTimePairLayout.addView(breakEndTimeInput);

        return breakTimePairLayout;
    }

    private EditText createBreakTimeEditText(String hint) {
        EditText editText = new EditText(getContext());
        editText.setHint(hint);
        editText.setFocusable(false);
        editText.setOnClickListener(view -> showTimePickerDialog(editText));

        return editText;
    }

    private void showTimePickerDialog(final EditText timeInput) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    timeInput.setText(formattedTime);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void saveBreaks(List<String> breaks) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(BREAKS_KEY + "_count", breaks.size());
        for (int i = 0; i < breaks.size(); i++) {
            editor.putString(BREAKS_KEY + "_" + i, breaks.get(i));
        }
        editor.apply();
    }

    private List<String> loadBreaks() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        int count = sharedPreferences.getInt(BREAKS_KEY + "_count", 0);
        List<String> breaks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String breakTime = sharedPreferences.getString(BREAKS_KEY + "_" + i, "");
            breaks.add(breakTime);
        }
        return breaks;
    }

}
