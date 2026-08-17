package com.example.androidexample.WelcomePage.TimeLine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidexample.R;

import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {

    private List<TimelineItem> timelineList;

    public TimelineAdapter(List<TimelineItem> timelineList) {
        this.timelineList = timelineList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timeline_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimelineItem item = timelineList.get(position);
        holder.userName.setText("@" + item.getUsername());
        holder.postContent.setText(item.getDescription());
        holder.timestamp.setText(item.getFormattedTime());

        // You can load image with Glide/Picasso if imageUrl exists
        // Example using Glide (uncomment if you have Glide):
        // Glide.with(holder.itemView.getContext()).load(item.getImageUrl()).into(holder.postImage);
    }

    @Override
    public int getItemCount() {
        return timelineList != null ? timelineList.size() : 0;
    }



    public void updateTimeline(List<TimelineItem> items) {
        this.timelineList = items;
        notifyDataSetChanged(); // Refresh the RecyclerView to reflect new data
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, postContent, timestamp;
        ImageView postImage;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name_text_view);
            postContent = itemView.findViewById(R.id.post_content_text_view);
            timestamp = itemView.findViewById(R.id.timestamp_text_view);
            postImage = itemView.findViewById(R.id.post_image_view);
        }
    }
}

