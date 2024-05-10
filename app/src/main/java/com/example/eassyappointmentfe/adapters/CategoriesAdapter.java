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

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the categories recycler view.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> implements Filterable {
    private final Context context;
    private List<Category> categories;
    private List<Category> categoryListFull;

    /**
     * Initializes the adapter.
     *
     * @param context The application context.
     * @param categories The list of categories to display.
     */
    public CategoriesAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
        this.categoryListFull = new ArrayList<>(categories);
    }

    /**
     * Sets the list of categories to display.
     *
     * @param newCategories The new list of categories.
     */
    public void setCategories(List<Category> newCategories) {
        this.categories = new ArrayList<>(newCategories);
        this.categoryListFull = new ArrayList<>(newCategories);
        notifyDataSetChanged();
    }

    /**
     * Creates a new view holder.
     *
     * @param parent The parent view group.
     * @param viewType The view type.
     * @return The new view holder.
     */
    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    /**
     * Binds the view holder to the category at the specified position.
     *
     * @param holder The view holder.
     * @param position The position of the category in the list.
     */
    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);

        if ("Favorites".equals(category.getName())) {
        holder.tvCategoryName.setTextColor(Color.BLACK);
        } else {
            holder.tvCategoryName.setTextColor(Color.BLACK);
        }
    }

    /**
     * Returns the number of categories in the list.
     *
     * @return The number of categories.
     */
    @Override
    public int getItemCount() {
        return categories.size();
    }

    /**
     * Returns the filter for the adapter.
     *
     * @return The filter.
     */
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

        /**
         * Publishes the results of the filtering operation.
         *
         * @param constraint The constraint used to filter the list.
         * @param results The results of the filtering operation.
         */
        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            categories.clear();
            categories.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    /**
     * View holder for the category recycler view.
     */

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerView rvHorizontalBusinesses;
        TextView tvCategoryName;

        /**
         * Creates a new category view holder.
         *
         * @param itemView The view.
         */
        public CategoryViewHolder(View itemView) {
            super(itemView);
            rvHorizontalBusinesses = itemView.findViewById(R.id.rvHorizontalBusinesses);
            rvHorizontalBusinesses.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }

        /**
         * Binds the view holder to the category.
         *
         * @param category The category.
         */
        public void bind(Category category) {
            BusinessAdapter adapter = new BusinessAdapter(context, category.getBusinesses(), null);
            rvHorizontalBusinesses.setAdapter(adapter);
            tvCategoryName.setText(category.getName());
        }
    }
}