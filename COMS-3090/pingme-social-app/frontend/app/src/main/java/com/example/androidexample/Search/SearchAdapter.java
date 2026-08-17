//package com.example.androidexample.Search;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import android.annotation.SuppressLint;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.androidexample.R;
//
//import java.util.List;
//
//public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
//
//    public interface OnHashtagClickListener {
//        void onHashtagClick(String hashtag);
//    }
//
//    private List<String> hashtagList;
//    private final OnHashtagClickListener clickListener;
//
//    // ✅ Constructor accepts both data and click listener
//    public SearchAdapter(List<String> hashtagList, OnHashtagClickListener clickListener) {
//        this.hashtagList = hashtagList;
//        this.clickListener = clickListener;
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView hashtagText;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            hashtagText = itemView.findViewById(android.R.id.text1);
//        }
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(android.R.layout.simple_list_item_1, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @SuppressLint("RecyclerView")
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        String hashtag = hashtagList.get(position);
//        holder.hashtagText.setText(hashtag);
//
//        holder.itemView.setOnClickListener(v -> {
//            if (clickListener != null) {
//                clickListener.onHashtagClick(hashtag);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return hashtagList != null ? hashtagList.size() : 0;
//    }
//
//    public void updateList(List<String> newList) {
//        this.hashtagList = newList;
//        notifyDataSetChanged();
//    }
//}
package com.example.androidexample.Search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidexample.R;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    private List<User> userList;
    private final OnUserClickListener clickListener;

    public SearchAdapter(List<User> userList, OnUserClickListener clickListener) {
        this.userList = userList;
        this.clickListener = clickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView usernameText;
        TextView emailText;
        TextView passwordText;
        TextView followersText;
        TextView followingText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.name_text);
            usernameText = itemView.findViewById(R.id.username_text);
            emailText = itemView.findViewById(R.id.email_text);
            passwordText = itemView.findViewById(R.id.password_text);
            followersText = itemView.findViewById(R.id.followers_text);
            followingText = itemView.findViewById(R.id.following_text);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameText.setText("Name: " + user.getName());
        holder.usernameText.setText("Username: @" + user.getUsername());
        holder.emailText.setText("Email: " + user.getEmail());
        holder.passwordText.setText("Password: " + user.getPassword());
        holder.followersText.setText("Followers: " + String.join(", ", user.getFollowers()));
        holder.followingText.setText("Following: " + String.join(", ", user.getFollowing()));

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onUserClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public void updateList(List<User> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }
}
