package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
//
//// RecyclerView.Adapter for NotificationItem
//public class NotificationAdapter
//        extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
//
//    private final List<NotificationItem> items;
//
//    public NotificationAdapter(List<NotificationItem> items) {
//        this.items = items;
//    }
//
//    @NonNull @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        // Inflate your item layout (you'll need to create res/layout/notification_item.xml)
//        View v = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_notification, parent, false);
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        NotificationItem note = items.get(position);
//        holder.title.setText(note.getTitle());
//        holder.body.setText(note.getBody());
//        // format the timestamp nicely
//        String time = DateFormat.getDateTimeInstance()
//                .format(new Date(note.getTimestamp()));
//        holder.time.setText(time);
//    }
//
//    @Override
//    public int getItemCount() {
//        return items.size();
//    }
//
//    static class ViewHolder extends RecyclerView.ViewHolder {
//        final TextView title, body, time;
//
//        ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            title = itemView.findViewById(R.id.notification_title);
//            body  = itemView.findViewById(R.id.notification_body);
//            time  = itemView.findViewById(R.id.notification_time);
//        }
//    }
//}
public class NotificationAdapter
        extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    /** Callback for when a notification is tapped. */
    public interface OnItemClickListener {
        void onItemClick(NotificationItem item, int position);
    }

    private final List<NotificationItem> items;
    private final OnItemClickListener listener;

    // New constructor takes your click‐listener
    public NotificationAdapter(List<NotificationItem> items,
                               OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem note = items.get(position);
        holder.title.setText(note.getTitle());
        holder.body .setText(note.getBody());
        holder.time .setText(
                DateFormat.getDateTimeInstance()
                        .format(new Date(note.getTimestamp()))
        );

        // Attach click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(note, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title, body, time;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notification_title);
            body  = itemView.findViewById(R.id.notification_body);
            time  = itemView.findViewById(R.id.notification_time);
        }
    }
}
