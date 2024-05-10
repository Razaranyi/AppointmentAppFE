package com.example.eassyappointmentfe.adapters;

import android.content.Context;
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
import com.example.eassyappointmentfe.userActivity.AppointmentActivity;
import com.example.eassyappointmentfe.util.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for the RecyclerView in the AppointmentActivity.
 * It handles the display of appointments and the booking/canceling of appointments.
 */

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder> {
    private List<Appointment> appointments;
    private boolean isCustomer;
    private Context context;


    /**
     * Constructor for the AppointmentsAdapter.
     *
     * @param context      The context of the activity.
     * @param appointments The list of appointments to be displayed.
     * @param isCustomer   A boolean indicating if the user is a customer or a business owner.
     */
    public AppointmentsAdapter(Context context, List<Appointment> appointments, boolean isCustomer) {
        this.appointments = appointments;
        this.isCustomer = isCustomer;
        this.context = context;
    }

    /**
     * Creates a new AppointmentViewHolder.
     *
     * @param parent   The parent view.
     * @param viewType The view type.
     * @return A new AppointmentViewHolder.
     */
    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    /**
     * Binds the data to the AppointmentViewHolder.
     *
     * @param holder   The AppointmentViewHolder.
     * @param position The position of the item in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        if (appointment == null) {
            appointment = appointments.get(0);
        }
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        if (context instanceof AppointmentActivity) {
            timeFormat = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
        }
        holder.startTimeTextView.setText(timeFormat.format(appointment.getStartTime())+ " - ");
        holder.endTimeTextView.setText(timeFormat.format(appointment.getEndTime()));
        holder.startTimeTextView.setBackgroundColor(ContextCompat.getColor(holder.startTimeTextView.getContext(), R.color.imageTextBackground));
        holder.endTimeTextView.setBackgroundColor(ContextCompat.getColor(holder.endTimeTextView.getContext(), R.color.imageTextBackground));

        if (isCustomer) { //handle if user is customer
            //costumer in My Appointments page can cancel his own appointments
            if (context instanceof AppointmentActivity) {
                holder.nameTextView.setVisibility(View.VISIBLE);
                holder.nameTextView.setText(appointment.getBookedBusinessName());
                holder.cancelOrBookButton.setVisibility(View.VISIBLE);
                holder.cancelOrBookButton.setText("Cancel");
                holder.nameTextView.setBackgroundResource(R.color.imageTextBackground);
                setCancelButtonClickListener(holder, appointment);
            }
            else { // customer in Business page can only book available appointments

                if (appointment.isAvailable()) {
                    holder.nameTextView.setVisibility(View.GONE);
                    holder.cancelOrBookButton.setVisibility(View.VISIBLE);
                    holder.cancelOrBookButton.setText("Book");
                    holder.nameTextView.setBackgroundResource(R.color.imageTextBackground);

                    setBookButtonClickListener(holder, appointment);

                } else {
                    holder.nameTextView.setVisibility(View.GONE);
                    holder.cancelOrBookButton.setEnabled(false);
                    holder.cancelOrBookButton.setText("Book");
                }
            }
        }else { // business owner can cancel appointments of every customer
            if (!(context instanceof AppointmentActivity)) {
                System.out.println("oh-oh got here when I shouldnt");
                if (appointment.isAvailable()) {
                    holder.cancelOrBookButton.setText("Cancel");
                    holder.cancelOrBookButton.setEnabled(false);
                    holder.nameTextView.setVisibility(View.VISIBLE);
                    holder.nameTextView.setText("Free");
                    holder.nameTextView.setBackgroundResource(R.color.imageTextBackground);
                } else {
                    holder.nameTextView.setVisibility(View.VISIBLE);
                    holder.nameTextView.setText(appointment.getBookingUserName()); //get from Server
                    holder.cancelOrBookButton.setText("Cancel");
                    holder.cancelOrBookButton.setEnabled(true);
                    holder.nameTextView.setBackgroundResource(R.color.imageTextBackground);
                    setCancelButtonClickListener(holder, appointment);
                }
            }
        }
    }

    /**
     * Sets the click listener for the cancel button.
     *
     * @param holder      The AppointmentViewHolder.
     * @param appointment The appointment to be canceled.
     */
    private void setCancelButtonClickListener(AppointmentViewHolder holder, Appointment appointment) {
        holder.cancelOrBookButton.setOnClickListener(v -> {
            new Thread(() -> {
                String response = String.valueOf(NetworkUtils.performPostRequest(
                        holder.cancelOrBookButton.getContext(),
                        "bookings/cancel/" + appointment.getId(), new JSONObject(), true));
                try {
                    int status = new JSONObject(response).getInt("status");
                    if (status == 200) {
                        if (!(context instanceof AppointmentActivity)) {
                            holder.cancelOrBookButton.post(() -> holder.cancelOrBookButton.setEnabled(false));
                            holder.cancelOrBookButton.post(() -> holder.cancelOrBookButton.setText("Cancelled"));
                        }

                        else {
                            ((AppointmentActivity) context).refreshAppointments();
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }


    /**
     * Sets the click listener for the book button.
     *
     * @param holder      The AppointmentViewHolder.
     * @param appointment The appointment to be booked.
     */
    private void setBookButtonClickListener(AppointmentViewHolder holder, Appointment appointment) {
    holder.cancelOrBookButton.setOnClickListener(v -> {
        JSONObject bookingData = new JSONObject();
        JSONArray appointments = new JSONArray();
        appointments.put(appointment.getId());
        JSONObject rootObject = new JSONObject();

        insertBookingData(bookingData, appointments, rootObject, appointment);

        new Thread(() -> {
            String response = String.valueOf(NetworkUtils.performPostRequest(
                    holder.cancelOrBookButton.getContext(),
                    "bookings/book",
                    rootObject,
                    true
            ));
            try {
                int status = new JSONObject(response).getInt("status");
                if (status == 200) {
                    holder.cancelOrBookButton.post(() -> holder.cancelOrBookButton.setEnabled(false));
                    holder.cancelOrBookButton.post(() -> holder.cancelOrBookButton.setText("Booked"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    });
}

/**
 * Inserts the booking data into the JSONObject.
 *
 * @param bookingData  The JSONObject to insert the data into.
 * @param appointments The JSONArray of appointments.
 * @param rootObject   The root JSONObject.
 * @param appointment  The appointment to be booked.
 */
private void insertBookingData(JSONObject bookingData, JSONArray appointments, JSONObject rootObject, Appointment appointment) {
    try {
        bookingData.put("appointmentsIds", appointments);
        bookingData.put("bookingTime", LocalDateTime.now().toString());
        bookingData.put("serviceProviderId", appointment.getServiceProviderId());
        rootObject.put("data", bookingData);
        System.out.println("booking request: " + rootObject.toString());
    } catch (Exception e) {
        e.printStackTrace();
    }

}


    /**
     * Returns the number of items in the list.
     * @return
     */
    @Override
    public int getItemCount() {
        return appointments != null ? appointments.size() : 0;
    }

    /**
     * Updates the data in the list.
     * @param newAppointments The new list of appointments.
     */

    public void updateData(List<Appointment> newAppointments) {
        appointments.clear();
        appointments.addAll(newAppointments);
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for the RecyclerView.
     */
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
