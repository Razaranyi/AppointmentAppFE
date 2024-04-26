package com.example.eassyappointmentfe.businessActivity;

import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eassyappointmentfe.R;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class BreakTimeBottomSheetDialogFragment extends DialogFragment {

    private EditText breakStartTimeInput;
    private EditText breakEndTimeInput;

    private LinearLayout dialogLayout;

    public interface BottomSheetListener {
        void onBreaksAdded(List<String> breaks);
        void onDaysSelected(Set<DayOfWeek> days);
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

        setupButtons();

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
        java.util.Calendar c = java.util.Calendar.getInstance();
        int hour = c.get(java.util.Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    timeInput.setText(formattedTime);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void setupButtons() {
        Button addButton = new Button(getContext());
        addButton.setText("Add");
        addButton.setOnClickListener(view -> {
            // Add your logic for the 'Add' button here
        });

        Button cancelButton = new Button(getContext());
        cancelButton.setText("Cancel");
        cancelButton.setOnClickListener(view -> {
            // Add your logic for the 'Cancel' button here
            dismiss(); // This will close the dialog
        });

        dialogLayout.addView(addButton);
        dialogLayout.addView(cancelButton);
    }
}