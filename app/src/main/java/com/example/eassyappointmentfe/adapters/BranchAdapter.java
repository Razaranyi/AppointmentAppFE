package com.example.eassyappointmentfe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eassyappointmentfe.DTO.Branch;
import com.example.eassyappointmentfe.R;

import java.util.List;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.BranchViewHolder> {
    private final List<Branch> branches;
    private final Context context;

    public BranchAdapter(Context context, List<Branch> branches) {
        this.context = context;
        this.branches = branches;
    }

    @Override
    public BranchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_branch_image, parent, false);
        return new BranchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BranchViewHolder holder, int position) {
        Branch branch = branches.get(position);
        holder.bind(branch);
    }

    @Override
    public int getItemCount() {
        return branches.size();
    }

    class BranchViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView branchUriTextView;

        public BranchViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_branch);
            branchUriTextView = itemView.findViewById(R.id.text_uri_title);
        }

        public void bind(Branch branch) {
            imageView.setImageURI(branch.getBranchImage());
            branchUriTextView.setText(branch.getName());
            // Set click listener
            itemView.setOnClickListener(v -> {/* Handle click */});
        }
    }
}

