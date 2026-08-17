//package com.example.androidexample;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.android.volley.Request;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//public class NotificationsFragment extends Fragment {
//    private static final String BASE_URL = "http://coms-3090-029.class.las.iastate.edu:8080/notifications/";
//    private RecyclerView            recycler;
//    private NotificationAdapter     adapter;
//    private List<NotificationItem>  items = new ArrayList<>();
//
//    @Nullable @Override
//    public View onCreateView(
//            @NonNull LayoutInflater inflater,
//            @Nullable ViewGroup container,
//            @Nullable Bundle savedInstanceState
//    ) {
//        View v = inflater.inflate(R.layout.fragment_notifications, container, false);
//
//        recycler = v.findViewById(R.id.notifications_recycler);
//        adapter  = new NotificationAdapter(items);
//        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
//        recycler.setAdapter(adapter);
//
//        fetchNotifications();
//        return v;
//    }
//
//    private void fetchNotifications() {
//        // pull the username your MainActivity passed in
//        Intent launch = requireActivity().getIntent();
//        String username = launch.getStringExtra("USERNAME");
//        if (username == null || username.isEmpty()) {
//            username = "Guest";  // or bail out
//        }
//
//        String url = BASE_URL + username;
//        StringRequest req = new StringRequest(
//                Request.Method.GET, url,
//                response -> {
//                    // if the server literally returned "Failure: …"
//                    if (response.startsWith("Failure:")) {
//                        Toast.makeText(requireContext(),
//                                "Server error: " + response,
//                                Toast.LENGTH_LONG).show();
//                        return;
//                    }
//
//                    // otherwise we expect valid JSON
//                    try {
//                        JSONObject root  = new JSONObject(response);
//                        JSONObject posts = root.optJSONObject("Posts");
//
//                        items.clear();
//                        if (posts != null) {
//                            Iterator<String> keys = posts.keys();
//                            while (keys.hasNext()) {
//                                String from = keys.next();
//                                String body = posts.getString(from);
//                                // timestamp is not provided by your API; just use now()
//                                items.add(new NotificationItem(from, body, System.currentTimeMillis()));
//                            }
//                        }
//
//                        adapter.notifyDataSetChanged();
//                    } catch (JSONException e) {
//                        Toast.makeText(requireContext(),
//                                "Parse error: " + e.getMessage(),
//                                Toast.LENGTH_LONG).show();
//                    }
//                },
//                error -> Toast.makeText(requireContext(),
//                        "Network error: " + error.getMessage(),
//                        Toast.LENGTH_LONG).show()
//        );
//
//        Volley.newRequestQueue(requireContext()).add(req);
//    }
//    /**
//     * Call this to “manually” push a new notification into the list.
//     */
//    public void addNotification(NotificationItem note) {
//        // make sure the RecyclerView+adapter are already set up (i.e. after onCreateView)
//        items.add(0, note);                  // newest on top
//        adapter.notifyItemInserted(0);
//        recycler.scrollToPosition(0);
//    }
//
//}

//package com.example.androidexample;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.android.volley.Request;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * Displays both post and chat notifications for the logged-in user.
// */
//public class NotificationsFragment extends Fragment {
//    private static final String TAG      = "NotificationsFrag";
//    private static final String BASE_URL =
//            "http://coms-3090-029.class.las.iastate.edu:8080/notifications/";
//
//    private ProgressBar progressBar;
//    private RecyclerView rvNotifications;
//    private TextView tvEmptyState;
//    private NotificationAdapter adapter;
//    private final List<NotificationItem> items = new ArrayList<>();
//
//    @Nullable @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.fragment_notifications, container, false);
//
//        progressBar     = v.findViewById(R.id.progressBar);
//        rvNotifications = v.findViewById(R.id.notifications_recycler);
//        tvEmptyState    = v.findViewById(R.id.tvEmptyState);
//
//
//        adapter = new NotificationAdapter(items);
//        rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
//        rvNotifications.setAdapter(adapter);
//
//        // show loader
//        progressBar.setVisibility(View.VISIBLE);
//        rvNotifications.setVisibility(View.GONE);
//        tvEmptyState.setVisibility(View.GONE);
//
//        loadNotifications();
//        return v;
//    }
//
//    private void loadNotifications() {
//        // get username
//        Intent intent = requireActivity().getIntent();
//        String username = intent.getStringExtra("USERNAME");
//        if (username == null || username.isEmpty()) {
//            username = "Guest";
//        }
//
//        String url = BASE_URL + username;
//        JsonObjectRequest req = new JsonObjectRequest(
//                Request.Method.GET,
//                url,
//                null,
//                this::onResponse,
//                error -> {
//                    progressBar.setVisibility(View.GONE);
//                    tvEmptyState.setText("Failed to load notifications");
//                    tvEmptyState.setVisibility(View.VISIBLE);
//                }
//        );
//        Volley.newRequestQueue(requireContext()).add(req);
//    }
//
//    private void onResponse(JSONObject response) {
//        progressBar.setVisibility(View.GONE);
//        items.clear();
//
//        // Parse Posts
//        JSONObject posts = response.optJSONObject("Posts");
//        if (posts != null) {
//            Iterator<String> it = posts.keys();
//            while (it.hasNext()) {
//                String user = it.next();
//                String text = posts.optString(user);
//                items.add(new NotificationItem(
//                        "New post by " + user,
//                        text,
//                        System.currentTimeMillis()
//                ));
//            }
//        }
//
//        // Parse Chats
//        JSONObject chats = response.optJSONObject("Chats");
//        if (chats != null) {
//            Iterator<String> it2 = chats.keys();
//            while (it2.hasNext()) {
//                String key = it2.next();
//                String msg = chats.optString(key);
//                String label;
//                if (key.matches("\\d+")) {
//                    label = "Group chat " + key;
//                } else {
//                    label = "Message from " + key;
//                }
//                items.add(new NotificationItem(
//                        label,
//                        msg,
//                        System.currentTimeMillis()
//                ));
//            }
//        }
//
//        if (items.isEmpty()) {
//            tvEmptyState.setText("No notifications");
//            tvEmptyState.setVisibility(View.VISIBLE);
//            rvNotifications.setVisibility(View.GONE);
//        } else {
//            adapter.notifyDataSetChanged();
//            rvNotifications.setVisibility(View.VISIBLE);
//        }
//    }
//
//    /**
//     * Manually push a new notification (e.g. from Activity).
//     */
//    public void addNotification(NotificationItem note) {
//        items.add(0, note);
//        adapter.notifyItemInserted(0);
//        rvNotifications.scrollToPosition(0);
//        if (items.size() == 1) {
//            tvEmptyState.setVisibility(View.GONE);
//            rvNotifications.setVisibility(View.VISIBLE);
//        }
//    }
//}

package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidexample.Friends.Message.MessagingFragment;
import com.example.androidexample.Friends.GroupChat.GroupChatSelectionFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Fragment that displays notifications for posts and chats.
 * Tapping a notification navigates into the appropriate chat/post and
 * removes that notification from the list.
 */
public class NotificationsFragment extends Fragment {
    private static final String BASE_URL =
            "http://coms-3090-029.class.las.iastate.edu:8080/notifications/";

    private String currentUser;
    private ProgressBar progressBar;
    private RecyclerView rvNotifications;
    private TextView tvEmptyState;
    private NotificationAdapter adapter;
    private final List<NotificationItem> items = new ArrayList<>();

    @Nullable @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View v = inflater.inflate(R.layout.fragment_notifications, container, false);

        // Read current user from intent
        Intent intent = requireActivity().getIntent();
        currentUser = intent.getStringExtra("USERNAME");
        if (currentUser == null || currentUser.isEmpty()) {
            currentUser = "Guest";
        }

        progressBar     = v.findViewById(R.id.progressBar);
        rvNotifications = v.findViewById(R.id.notifications_recycler);
        tvEmptyState    = v.findViewById(R.id.tvEmptyState);

        // Set up adapter with click listener
        adapter = new NotificationAdapter(items, (note, pos) -> {
            String title = note.getTitle();

            if (title.startsWith("Message from ")) {
                // Direct message
                String friendUsername = title.substring("Message from ".length());
                String friendName     = friendUsername;
                // Construct a chat ID for this DM
                String chatId = currentUser + "__" + friendUsername;
                replaceFragment(
                        MessagingFragment.newInstance(
                                currentUser,
                                friendName,
                                friendUsername,
                                chatId
                        )
                );

            } else if (title.startsWith("Group chat ")) {
                // Group chat notification
                int groupId = Integer.parseInt(
                        title.substring("Group chat ".length())
                );
                replaceFragment(
                        GroupChatSelectionFragment.newInstance(String.valueOf(groupId))
                );

            }

            // Remove the tapped notification
            items.remove(pos);
            adapter.notifyItemRemoved(pos);

            // Show empty state if no items left
            if (items.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                rvNotifications.setVisibility(View.GONE);
            }
        });

        rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvNotifications.setAdapter(adapter);

        // Initial UI state
        progressBar.setVisibility(View.VISIBLE);
        rvNotifications.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);

        loadNotifications();
        return v;
    }

    /** Fetch notifications from backend */
    private void loadNotifications() {
        String url = BASE_URL + currentUser;
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                this::onResponse,
                error -> {
                    progressBar.setVisibility(View.GONE);
                    tvEmptyState.setText("Failed to load notifications");
                    tvEmptyState.setVisibility(View.VISIBLE);
                }
        );
        Volley.newRequestQueue(requireContext()).add(req);
    }

    /** Called when JSON response is received */
    private void onResponse(JSONObject response) {
        progressBar.setVisibility(View.GONE);
        items.clear();

        // Parse post notifications
        JSONObject posts = response.optJSONObject("Posts");
        if (posts != null) {
            Iterator<String> it = posts.keys();
            while (it.hasNext()) {
                String user = it.next();
                String body = posts.optString(user);
                items.add(new NotificationItem(
                        "New post by " + user,
                        body,
                        System.currentTimeMillis()
                ));
            }
        }

        // Parse chat notifications
        JSONObject chats = response.optJSONObject("Chats");
        if (chats != null) {
            Iterator<String> it2 = chats.keys();
            while (it2.hasNext()) {
                String key = it2.next();
                String msg = chats.optString(key);
                String label = key.matches("\\d+")
                        ? "Group chat " + key
                        : "Message from " + key;
                items.add(new NotificationItem(
                        label,
                        msg,
                        System.currentTimeMillis()
                ));
            }
        }

        // Show list or empty state
        if (items.isEmpty()) {
            tvEmptyState.setText("No notifications");
            tvEmptyState.setVisibility(View.VISIBLE);
            rvNotifications.setVisibility(View.GONE);
        } else {
            adapter.notifyDataSetChanged();
            rvNotifications.setVisibility(View.VISIBLE);
        }
    }

    /** Helper to replace fragments */
    private void replaceFragment(Fragment frag) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, frag)
                .addToBackStack(null)
                .commit();
    }

    /** Allows external insertion of a notification */
    public void addNotification(NotificationItem note) {
        items.add(0, note);
        adapter.notifyItemInserted(0);
        rvNotifications.scrollToPosition(0);
        if (items.size() == 1) {
            tvEmptyState.setVisibility(View.GONE);
            rvNotifications.setVisibility(View.VISIBLE);
        }
    }
}


