package com.example.eassyappointmentfe.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eassyappointmentfe.DTO.Appointment;
import com.example.eassyappointmentfe.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder> {
    private List<Appointment> appointments;
    private boolean isCustomer;


    public AppointmentsAdapter(List<Appointment> appointments,boolean isCustomer) {
        this.appointments = appointments;
        this.isCustomer = isCustomer;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.startTimeTextView.setText(timeFormat.format(appointment.getStartTime())+ " - ");
        holder.endTimeTextView.setText(timeFormat.format(appointment.getEndTime()));
        holder.startTimeTextView.setBackgroundColor(ContextCompat.getColor(holder.startTimeTextView.getContext(), R.color.imageTextBackground));
        holder.endTimeTextView.setBackgroundColor(ContextCompat.getColor(holder.endTimeTextView.getContext(), R.color.imageTextBackground));

        if (isCustomer) {
            if (appointment.isAvailable()) {
                holder.nameTextView.setVisibility(View.GONE);
                holder.cancelOrBookButton.setVisibility(View.VISIBLE);
                holder.cancelOrBookButton.setText("Book");
                holder.nameTextView.setBackgroundResource(R.color.imageTextBackground);

            } else {
                holder.nameTextView.setVisibility(View.GONE);
                holder.cancelOrBookButton.setEnabled(false);
            }
        }else {
            if(appointment.isAvailable()) {
                holder.cancelOrBookButton.setText("Cancel");
                holder.cancelOrBookButton.setEnabled(false);
                holder.nameTextView.setVisibility(View.VISIBLE);
                holder.nameTextView.setText("Free");
                holder.nameTextView.setBackgroundResource(R.color.imageTextBackground);
            }else {
                holder.nameTextView.setVisibility(View.VISIBLE);
                holder.nameTextView.setText("Name"); //get from Server
                holder.cancelOrBookButton.setText("Cancel");
                holder.cancelOrBookButton.setEnabled(true);
                holder.nameTextView.setBackgroundResource(R.color.imageTextBackground);
            }
        }
    }

    @Override
    public int getItemCount() {
        return appointments != null ? appointments.size() : 0;
    }

    public void updateData(List<Appointment> newAppointments) {
        appointments.clear();
        appointments.addAll(newAppointments);
        notifyDataSetChanged();
    }

    class AppointmentViewHolder extends RecyclerView.ViewHolder {
        private final TextView startTimeTextView, endTimeTextView, nameTextView;
        private final Button cancelOrBookButton;

        public AppointmentViewHolder(View itemView) {
            super(itemView);
            startTimeTextView = itemView.findViewById(R.id.start_time);
            endTimeTextView = itemView.findViewById(R.id.end_time);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            cancelOrBookButton = itemView.findViewById(R.id.cancel_or_book_button);
        }
    }
}
