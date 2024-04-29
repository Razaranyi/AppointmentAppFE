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

public class ServiceProviderAdapter extends RecyclerView.Adapter<ServiceProviderAdapter.ServiceProviderViewHolder> {

    private final List<ServiceProvider> serviceProviders;
    private final Context context;

    public ServiceProviderAdapter(@NonNull Context context, List<ServiceProvider> serviceProviders) {
        this.context = context;
        this.serviceProviders = serviceProviders;
    }

    @NonNull
    @Override
    public ServiceProviderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_provider_image, parent, false);
        return new ServiceProviderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceProviderViewHolder holder, int position) {
        ServiceProvider serviceProvider = serviceProviders.get(position);
        holder.bind(serviceProvider);
    }

    @Override
    public int getItemCount() {
        return serviceProviders.size();
    }

    class ServiceProviderViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView serviceProviderUriTextView;

        public ServiceProviderViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_service_provider);
            serviceProviderUriTextView = itemView.findViewById(R.id.text_uri_title);
        }

        public void bind(ServiceProvider serviceProvider) {
            imageView.setImageURI(serviceProvider.getServiceProviderImage());
            serviceProviderUriTextView.setText(serviceProvider.getName());
            // Set click listener if needed
            itemView.setOnClickListener(v -> {/* Handle click */});
        }
    }
}
