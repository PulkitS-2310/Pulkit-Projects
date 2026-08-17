package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TopPostsAdapter extends RecyclerView.Adapter<TopPostsAdapter.ViewHolder> {
    private final List<TopPostItem> items;

    public TopPostsAdapter(List<TopPostItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_post, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TopPostItem item = items.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvLikedDate.setText(item.getLikedDate());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle;
        final TextView tvLikedDate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle     = itemView.findViewById(R.id.tvPostTitle);
            tvLikedDate = itemView.findViewById(R.id.tvLikedDate);
        }
    }
}