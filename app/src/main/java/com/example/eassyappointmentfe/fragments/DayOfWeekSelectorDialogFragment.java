package com.example.eassyappointmentfe.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eassyappointmentfe.R;


/**
 * Fragment for selecting days of the week.
 */
public class DayOfWeekSelectorDialogFragment extends DialogFragment {

    private static final String SHARED_PREFS_NAME = "DayOfWeekPrefs";
    private static final String SELECTED_DAYS_KEY = "SelectedDays";
    private CheckBox[] checkBoxes;
    private boolean[] selectedDays = new boolean[7];
    private DayOfWeekSelectorListener mListener;

    public interface DayOfWeekSelectorListener {
        void onDaysSelected(boolean[] days);
    }

    public void setDayOfWeekSelectorListener(DayOfWeekSelectorListener listener) {
        mListener = listener;
    }

    /**
     * Creates the view for the fragment.
     *
     * @param inflater The layout inflater.
     * @param container The view group container.
     * @param savedInstanceState The saved instance state.
     * @return The view for the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_day_of_week_selector, container, false);
        LinearLayout dialogLayout = v.findViewById(R.id.daysLayout);
        String[] daysOfWeek = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        checkBoxes = new CheckBox[daysOfWeek.length];
        for (int i = 0; i < checkBoxes.length; i++) {
            checkBoxes[i] = new CheckBox(getContext());
            checkBoxes[i].setText(daysOfWeek[i]);
            dialogLayout.addView(checkBoxes[i]);
            checkBoxes[i].setChecked(loadSelectedDay(i));
        }
        v.findViewById(R.id.confirmDaysButton).setOnClickListener(view -> {
            for (int i = 0; i < checkBoxes.length; i++) {
                selectedDays[i] = checkBoxes[i].isChecked();
                saveSelectedDay(i, selectedDays[i]);
            }
            if (mListener != null) {
                mListener.onDaysSelected(selectedDays);
            }
            dismiss();
        });
        return v;
    }

    /**
     * Saves the selected state of a day.
     *
     * @param dayIndex The index of the day.
     * @param isSelected Whether the day is selected.
     */
    private void saveSelectedDay(int dayIndex, boolean isSelected) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(SELECTED_DAYS_KEY + dayIndex, isSelected).apply();
    }

    /**
     * Loads the selected state of a day.
     *
     * @param dayIndex The index of the day.
     * @return Whether the day is selected.
     */
    private boolean loadSelectedDay(int dayIndex) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SELECTED_DAYS_KEY + dayIndex, false);
    }

    /**
     * Clears the selection.
     */
    public void clearSelection() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        for (int i = 0; i < checkBoxes.length; i++) {
            checkBoxes[i].setChecked(false);
            selectedDays[i] = false;
        }
    }
}