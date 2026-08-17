package com.example.androidexample.Friends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidexample.R;

import java.util.List;

public class FriendSelectionAdapter extends RecyclerView.Adapter<FriendSelectionAdapter.FriendViewHolder> {

    private List<SelectableFriend> friendsList;

    public FriendSelectionAdapter(List<SelectableFriend> friendsList) {
        this.friendsList = friendsList;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_selection, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        SelectableFriend friend = friendsList.get(position);
        holder.friendName.setText(friend.getName());
        holder.friendEmail.setText("@" + friend.getUsername()); // Display @username instead of email
        holder.selectionCheckBox.setChecked(friend.isSelected());

        holder.itemView.setOnClickListener(v -> {
            friend.setSelected(!friend.isSelected());
            holder.selectionCheckBox.setChecked(friend.isSelected());
        });
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView friendName, friendEmail; // Keeping friendEmail as ID but using it for username
        CheckBox selectionCheckBox;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.friendName);
            friendEmail = itemView.findViewById(R.id.friendEmail); // Reusing this ID for username
            selectionCheckBox = itemView.findViewById(R.id.selectionCheckBox);
        }
    }
}