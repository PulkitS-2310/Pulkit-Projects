package com.example.androidexample.Search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidexample.Profile.Post;
import com.example.androidexample.R;

import java.util.ArrayList;
import java.util.List;

public class PostUserAdapter extends RecyclerView.Adapter<PostUserAdapter.ViewHolder> {

    private List<PostUser> postList;
    private  boolean isUserMode;
    private static List<PostUser> posts;

    private final OnPostClickListener clickListener;

    public PostUserAdapter(List<PostUser> postList, OnPostClickListener clickListener) {
        this.postList = postList;
        this.clickListener = clickListener;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, descriptionText, timeText, usernameText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.title_text);
            descriptionText = itemView.findViewById(R.id.description_text);
            timeText = itemView.findViewById(R.id.time_text);
            usernameText = itemView.findViewById(R.id.username_text);
        }
    }


    @Override
    public PostUserAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostUser post = postList.get(position);
        holder.titleText.setText(post.getTitle());
        holder.descriptionText.setText(post.getDescription());
        holder.usernameText.setText("@" + post.getUsername());
        holder.timeText.setText(post.getFormattedTime());

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onPostClick(post);
            }
        });
    }


    @Override
    public int getItemCount () {
        return postList != null ? postList.size() : 0;
    }


    public void updateList (List < PostUser > posts) {
        this.isUserMode = false;
        notifyDataSetChanged();
    }

    public interface OnPostClickListener {
        void onPostClick(PostUser post);

    }

}

