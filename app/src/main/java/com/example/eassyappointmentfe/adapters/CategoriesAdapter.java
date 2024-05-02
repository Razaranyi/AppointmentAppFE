package com.example.eassyappointmentfe.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eassyappointmentfe.DTO.Business;
import com.example.eassyappointmentfe.DTO.Category;
import com.example.eassyappointmentfe.R;
import com.example.eassyappointmentfe.adapters.BusinessAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> implements Filterable {
    private final Context context;
    private List<Category> categories;
    private List<Category> categoryListFull;
    public CategoriesAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
        this.categoryListFull = new ArrayList<>(categories);
    }

    public void setCategories(List<Category> newCategories) {
        this.categories = new ArrayList<>(newCategories);
        this.categoryListFull = new ArrayList<>(newCategories);
        notifyDataSetChanged();
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

    @Override
    public Filter getFilter() {
        return businessFilter;
    }
    private Filter businessFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Category> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(categoryListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Category category : categoryListFull) {
                    boolean categoryMatches = category.getName().toLowerCase().contains(filterPattern);
                    List<Business> filteredBusinesses = new ArrayList<>();
                    for (Business business : category.getBusinesses()) {
                        if (business.getName().toLowerCase().contains(filterPattern) || categoryMatches) {
                            filteredBusinesses.add(business);
                        }
                    }
                    if (!filteredBusinesses.isEmpty() || categoryMatches) {
                        filteredList.add(new Category(category.getId(), category.getName(), filteredBusinesses));
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            categories.clear();
            categories.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


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