package com.example.eassyappointmentfe.businessActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eassyappointmentfe.R;

public class DayOfWeekSelectorDialogFragment extends DialogFragment {
    private CheckBox[] checkBoxes;
    private LinearLayout dialogLayout;
    private Button confirmButton;

    private DayOfWeekSelectorListener mListener;

    public interface DayOfWeekSelectorListener {
        void onDaysSelected(boolean[] days);
    }

    public void setDayOfWeekSelectorListener(DayOfWeekSelectorListener listener) {
        mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_day_of_week_selector, container, false);
        dialogLayout = v.findViewById(R.id.daysLayout); // Make sure this ID is correct in your layout XML

        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        checkBoxes = new CheckBox[daysOfWeek.length];

        for (int i = 0; i < checkBoxes.length; i++) {
            checkBoxes[i] = new CheckBox(getContext());
            checkBoxes[i].setText(daysOfWeek[i]);
            dialogLayout.addView(checkBoxes[i]);
        }

        confirmButton = v.findViewById(R.id.confirmDaysButton); // Ensure this ID is in your layout
        confirmButton.setOnClickListener(view -> {
            boolean[] selectedDays = new boolean[checkBoxes.length];
            for (int i = 0; i < checkBoxes.length; i++) {
                selectedDays[i] = checkBoxes[i].isChecked();
            }
            if (mListener != null) {
                mListener.onDaysSelected(selectedDays);
            }
            dismiss();
        });

        return v;
    }
}
