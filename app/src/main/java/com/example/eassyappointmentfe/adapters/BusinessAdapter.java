package com.example.eassyappointmentfe.adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eassyappointmentfe.DTO.Business;
import com.example.eassyappointmentfe.R;
import com.example.eassyappointmentfe.commonActivity.CommonBusinessActivity;
import com.example.eassyappointmentfe.util.NetworkUtils;

import org.json.JSONObject;

import java.util.List;

public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.BusinessViewHolder> {

    private final List<Business> businesses;
    private final Context context;
    private OnBusinessClickListener onBusinessClickListener;

    public BusinessAdapter(Context context, List<Business> businesses, OnBusinessClickListener listener) {
        this.businesses = businesses;
        this.context = context;
        this.onBusinessClickListener = listener;
    }


    @Override
    public BusinessViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_business, parent, false);
        return new BusinessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BusinessViewHolder holder, int position) {
        Business business = businesses.get(position);
        holder.bind(business);
        holder.tvBusinessName.setText(business.getName());
        holder.ivBusinessImage.setImageURI(business.getBusinessImage());

        if (business.isFavorite()) {
            holder.imgFavorite.setImageResource(R.drawable.star1);
        } else {
            holder.imgFavorite.setImageResource(R.drawable.stardefault);
        }

    }

    private void updateFavoriteStatus(long id) {
        new Thread(() -> {
            try {
                NetworkUtils.performPostRequest(
                        context,
                        "favorites/add-or-remove/" + id,
                        new JSONObject(),
                        true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return businesses.size();
    }
    public interface OnBusinessClickListener {
        void onBusinessClick(Business business);
    }

    public class BusinessViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvBusinessName;
        private final ImageView ivBusinessImage;
        private final ImageView imgFavorite;

        public BusinessViewHolder(View itemView) {
            super(itemView);
            tvBusinessName = itemView.findViewById(R.id.tvBusinessName);
            ivBusinessImage = itemView.findViewById(R.id.imgLogo);
            imgFavorite = itemView.findViewById(R.id.imgFavorite);

        }

        public void bind(Business business) {
            tvBusinessName.setText(business.getName());
            ivBusinessImage.setImageURI(business.getBusinessImage());

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, CommonBusinessActivity.class);
                System.out.println("" +
                        "Business Adapter:\n" +
                        "Business ID: " + business.getId()
                        + " Business Name: " + business.getName() );
                intent.putExtra("businessName", business.getName());
                intent.putExtra("businessId", String.valueOf(business.getId()));
                intent.putExtra("isCustomer", true);
                context.startActivity(intent);
            });

            imgFavorite.setOnClickListener(v -> {
                business.setFavorite(!business.isFavorite());
                if (business.isFavorite()) {
                    imgFavorite.setImageResource(R.drawable.star1);
                } else {
                    imgFavorite.setImageResource(R.drawable.stardefault);
                }
                notifyItemChanged(getAdapterPosition());
                updateFavoriteStatus(business.getId());
            });
        }
    }

}