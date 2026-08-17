package com.example.androidexample.Profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.androidexample.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_TEXT = 1;
    private static final int VIEW_TYPE_IMAGE = 2;

    private List<Post> posts;

    public PostAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @Override
    public int getItemViewType(int position) {
        Post post = posts.get(position);
        return post.isImagePost() ? VIEW_TYPE_IMAGE : VIEW_TYPE_TEXT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_TEXT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_view_text_post, parent, false);
            return new TextPostViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_view_image_post, parent, false);
            return new ImagePostViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Post post = posts.get(position);
        if (holder instanceof TextPostViewHolder) {
            ((TextPostViewHolder) holder).bind(post);
        } else if (holder instanceof ImagePostViewHolder) {
            ((ImagePostViewHolder) holder).bind(post);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // ViewHolder for text posts
    static class TextPostViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView timestampTextView;
        TextView titleTextView;
        TextView descriptionTextView;
        TextView hashtagsTextView;

        TextPostViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            hashtagsTextView = itemView.findViewById(R.id.hashtagsTextView);
        }

        void bind(Post post) {
            usernameTextView.setText(post.getUsername());
            timestampTextView.setText(post.getTimestamp());
            titleTextView.setText(post.getTitle());
            descriptionTextView.setText(post.getDescription());
            hashtagsTextView.setText(post.getHashtags());
        }
    }

    // ViewHolder for image posts
    static class ImagePostViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView timestampTextView;
        TextView titleTextView;
        ImageView postImageView;
        TextView descriptionTextView;
        TextView hashtagsTextView;

        ImagePostViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            postImageView = itemView.findViewById(R.id.postImageView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            hashtagsTextView = itemView.findViewById(R.id.hashtagsTextView);
        }

        void bind(com.example.androidexample.Profile.Post post) {
            usernameTextView.setText(post.getUsername());
            timestampTextView.setText(post.getTimestamp());
            titleTextView.setText(post.getTitle());
            descriptionTextView.setText(post.getDescription());
            hashtagsTextView.setText(post.getHashtags());

            // Load image using Glide
            if (post.isImagePost()) {
                Glide.with(itemView.getContext())
                        .load(post.getImageUrl())
                        .placeholder(R.drawable.baseline_broken_image_24)
                        .error(R.drawable.baseline_broken_image_24)
                        .into(postImageView);
            }
        }
    }
}
