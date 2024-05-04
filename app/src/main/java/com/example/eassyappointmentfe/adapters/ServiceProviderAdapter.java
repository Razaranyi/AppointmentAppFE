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
    private OnServiceProviderClickListener onServiceProviderClickListener;

    private Long selectedServiceProviderId;

    public ServiceProviderAdapter(@NonNull Context context, List<ServiceProvider> serviceProviders, OnServiceProviderClickListener listener) {
        this.context = context;
        this.serviceProviders = serviceProviders;
        this.onServiceProviderClickListener = listener;
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
        selectedServiceProviderId = serviceProvider.getId();
        holder.bind(serviceProvider);
    }

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

        public ServiceProviderViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_service_provider);
            serviceProviderUriTextView = itemView.findViewById(R.id.text_uri_title);
        }

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
