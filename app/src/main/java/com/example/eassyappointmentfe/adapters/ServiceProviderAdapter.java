package com.example.eassyappointmentfe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eassyappointmentfe.DTO.ServiceProvider;
import com.example.eassyappointmentfe.R;

import java.util.List;

/**
 * Adapter for the service provider recycler view.
 */
public class ServiceProviderAdapter extends RecyclerView.Adapter<ServiceProviderAdapter.ServiceProviderViewHolder> {
    private final List<ServiceProvider> serviceProviders;
    private final Context context;
    private OnServiceProviderClickListener onServiceProviderClickListener;

    private Long selectedServiceProviderId;

    /**
     * Initializes the adapter.
     *
     * @param context The application context.
     * @param serviceProviders The list of service providers to display.
     * @param listener The listener for service provider click events.
     */
    public ServiceProviderAdapter(@NonNull Context context, List<ServiceProvider> serviceProviders, OnServiceProviderClickListener listener) {
        this.context = context;
        this.serviceProviders = serviceProviders;
        this.onServiceProviderClickListener = listener;
    }

    /**
     * Creates a new view holder.
     *
     * @param parent The parent view group.
     * @param viewType The view type.
     * @return The new view holder.
     */
    @NonNull
    @Override
    public ServiceProviderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_provider_image, parent, false);
        return new ServiceProviderViewHolder(view);
    }

    /**
     * Binds the view holder to the service provider at the specified position.
     *
     * @param holder The view holder.
     * @param position The position of the service provider in the list.
     */

    @Override
    public void onBindViewHolder(@NonNull ServiceProviderViewHolder holder, int position) {
        ServiceProvider serviceProvider = serviceProviders.get(position);
        selectedServiceProviderId = serviceProvider.getId();
        holder.bind(serviceProvider);
    }

    /**
     * Returns the number of service providers in the list.
     *
     * @return The number of service providers.
     */

    @Override
    public int getItemCount() {
        return serviceProviders.size();
    }

    public long getSelectedServiceProviderId() {
        return selectedServiceProviderId;
    }

    class ServiceProviderViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView serviceProviderUriTextView;

        /**
         * Initializes the view holder.
         *
         * @param itemView The view for the item.
         */
        public ServiceProviderViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_service_provider);
            serviceProviderUriTextView = itemView.findViewById(R.id.text_uri_title);
        }

        /**
         * Binds the view holder to the specified service provider.
         *
         * @param serviceProvider The service provider to bind.
         */
        public void bind(ServiceProvider serviceProvider) {
            imageView.setImageURI(serviceProvider.getServiceProviderImage());
            serviceProviderUriTextView.setText(serviceProvider.getName());
            itemView.setOnClickListener(v -> {
                if (onServiceProviderClickListener != null) {
                    onServiceProviderClickListener.onServiceProviderClick(serviceProvider.getId());
                }
            });
        }
    }

    public interface OnServiceProviderClickListener {
        void onServiceProviderClick(long serviceProviderId);
    }
}
