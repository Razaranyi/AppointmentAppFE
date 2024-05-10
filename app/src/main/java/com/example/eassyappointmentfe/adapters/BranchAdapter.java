package com.example.eassyappointmentfe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eassyappointmentfe.DTO.Branch;
import com.example.eassyappointmentfe.R;

import java.util.List;

/**
 * Adapter for the branch recycler view.
 */

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.BranchViewHolder> {
    private final List<Branch> branches;
    private final Context context;
    private OnBranchClickListener onBranchClickListener;


    /**
     * Initializes the adapter.
     *
     * @param context The application context.
     * @param branches The list of branches to display.
     * @param onBranchClickListener The listener for branch click events.
     */
    public BranchAdapter(Context context, List<Branch> branches, OnBranchClickListener onBranchClickListener) {
        this.context = context;
        this.branches = branches;
        this.onBranchClickListener = onBranchClickListener;
    }

    /**
     * Creates a new view holder.
     *
     * @param parent The parent view group.
     * @param viewType The view type.
     * @return The new view holder.
     */

    @Override
    public BranchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()).inflate
                (R.layout.item_branch_image,
                        parent,
                        false);
        return new BranchViewHolder(view);
    }

    /**
     * Binds the view holder to the branch at the specified position.
     *
     * @param holder The view holder to bind.
     * @param position The position of the branch in the list.
     */

    @Override
    public void onBindViewHolder(BranchViewHolder holder, int position) {
        Branch branch = branches.get(position);
        holder.bind(branch);
    }

    /**
     * Returns the number of branches in the list.
     *
     * @return The number of branches.
     */

    @Override
    public int getItemCount() {
        return branches.size();
    }


    public interface OnBranchClickListener {
        void onBranchClick(Branch branch);
    }

    /**
     * View holder for the branch recycler view.
     */
    class BranchViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView branchUriTextView;

        public BranchViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_branch);
            imageView.setBackground(ContextCompat.getDrawable(context, R.color.imageTextBackground));
            branchUriTextView = itemView.findViewById(R.id.text_uri_title);
        }

        /**
         * Binds the branch to the view holder.
         *
         * @param branch The branch to bind.
         */
        public void bind(Branch branch) {
            imageView.setImageURI(branch.getBranchImage());
            branchUriTextView.setText(branch.getName());

            itemView.setOnClickListener(v -> {
                if (onBranchClickListener != null) {

                    onBranchClickListener.onBranchClick(branch);
                }
            });

        }
    }




}