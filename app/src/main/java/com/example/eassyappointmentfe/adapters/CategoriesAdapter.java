package com.example.eassyappointmentfe.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eassyappointmentfe.DTO.Category;
import com.example.eassyappointmentfe.R;
import com.example.eassyappointmentfe.adapters.BusinessAdapter;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {
    private final Context context;
    private final List<Category> categories;

    public CategoriesAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);

        if ("Favorites".equals(category.getName())) {
            holder.tvCategoryName.setTextColor(Color.RED); // Just an example
        } else {
            holder.tvCategoryName.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerView rvHorizontalBusinesses;
        TextView tvCategoryName;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            rvHorizontalBusinesses = itemView.findViewById(R.id.rvHorizontalBusinesses);
            rvHorizontalBusinesses.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }

        public void bind(Category category) {
            BusinessAdapter adapter = new BusinessAdapter(context, category.getBusinesses());
            rvHorizontalBusinesses.setAdapter(adapter);
            tvCategoryName.setText(category.getName());
        }
    }
}