package com.example.androidexample.Profile.Settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private List<String> settingsList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String option);
    }

    public SettingsAdapter(List<String> settingsList, OnItemClickListener listener) {
        this.settingsList = settingsList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String option = settingsList.get(position);
        holder.textView.setText(option);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(option));
    }

    @Override
    public int getItemCount() {
        return settingsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}