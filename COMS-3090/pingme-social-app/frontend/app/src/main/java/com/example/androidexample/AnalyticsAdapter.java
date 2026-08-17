package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AnalyticsAdapter extends RecyclerView.Adapter<AnalyticsAdapter.ViewHolder> {
    private final List<AnalyticsItem> items;

    public AnalyticsAdapter(List<AnalyticsItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_analytics, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnalyticsItem item = items.get(position);
        holder.tvMetric.setText(item.getMetric());
        holder.tvValue.setText(item.getValue());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvMetric;
        final TextView tvValue;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMetric = itemView.findViewById(R.id.tvMetric);
            tvValue  = itemView.findViewById(R.id.tvValue);
        }
    }
}