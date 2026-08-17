package com.example.androidexample.Friends.Item;

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

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {

    private Context context;
    private List<Item> itemsList;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public ItemsAdapter(Context context, List<Item> itemsList, OnItemClickListener listener) {
        this.context = context;
        this.itemsList = itemsList;
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_friend, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemsList.get(position);
        holder.friendName.setText(item.getName());

        if (item.getType() == Item.TYPE_FRIEND) {
            holder.subText.setText("@" + item.getUsername());
        } else if (item.getType() == Item.TYPE_GROUP_CHAT) {
            String membersText = "@" + String.join(", @", item.getGroupUsernames());
            holder.subText.setText(membersText);
        }

        holder.profileImage.setImageResource(R.drawable.ic_message_groupchat);

        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView friendName;
        TextView subText;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            friendName = itemView.findViewById(R.id.friendName);
            subText = itemView.findViewById(R.id.subText);
        }
    }
}