package com.example.androidexample.Friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidexample.R;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    private Context context;
    private List<Friend> friendsList;
    private OnFriendClickListener friendClickListener;

    public interface OnFriendClickListener {
        void onFriendClick(Friend friend);
    }

    public FriendsAdapter(Context context, List<Friend> friendsList, OnFriendClickListener listener) {
        this.context = context;
        this.friendsList = friendsList;
        this.friendClickListener = listener;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friendsList.get(position);
        holder.friendName.setText(friend.getName());
        // Set placeholder image (replace with actual image loading if needed)
        holder.profileImage.setImageResource(R.drawable.friend);

        // Set click listener for the entire item
        holder.itemView.setOnClickListener(v -> {
            if (friendClickListener != null) {
                friendClickListener.onFriendClick(friend);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView friendName;

        FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            friendName = itemView.findViewById(R.id.friendName);
        }
    }
}