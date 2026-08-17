package com.example.androidexample.Friends.Message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.androidexample.Friends.GroupChat.GroupChatSettingsFragment;
import com.example.androidexample.R;
import com.example.androidexample.VolleySingleton;
import com.example.androidexample.WebSocketService;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MessagingFragment extends Fragment {

    private static final String TAG = "MessagingFragment";
    private static final String BASE_WS_URL_DM = "ws://coms-3090-029.class.las.iastate.edu:8080/chat/";
    private static final String BASE_WS_URL_GROUP = "ws://coms-3090-029.class.las.iastate.edu:8080/groupChat/";
    private static final String BASE_GROUP_URL = "http://coms-3090-029.class.las.iastate.edu:8080/group/";

    private ImageButton sendButton;
    private ImageButton backButton;
    private ImageButton settingsButton;
    private EditText messageInput;
    private RecyclerView messageRecyclerView;
    private TextView chatTitle;
    private MessageAdapter messageAdapter;
    private String username;       // Logged-in user's username
    private String friendName;     // Friend's display name
    private String friendUsername; // Friend's actual username
    private String chatId;         // Group chat ID
    private String creatorUsername;
    private String wsKey;
    private boolean isWebSocketConnected = false;

    public MessagingFragment() {}

    public static MessagingFragment newInstance(String username, String friendName, String friendUsername, String chatId) {
        MessagingFragment fragment = new MessagingFragment();
        Bundle args = new Bundle();
        args.putString("USERNAME", username);
        args.putString("FRIEND_NAME", friendName);
        args.putString("FRIEND_USERNAME", friendUsername);
        args.putString("CHAT_ID", chatId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messaging, container, false);

        if (getArguments() != null) {
            username = getArguments().getString("USERNAME", "Guest");
            friendName = getArguments().getString("FRIEND_NAME");
            friendUsername = getArguments().getString("FRIEND_USERNAME");
            chatId = getArguments().getString("CHAT_ID");
        }

        wsKey = friendUsername != null ? "dm_" + username + "_" + friendUsername : "group_" + chatId;

        sendButton = view.findViewById(R.id.sendButton);
        backButton = view.findViewById(R.id.backButton);
        settingsButton = view.findViewById(R.id.settingsButton);
        messageInput = view.findViewById(R.id.messageInput);
        messageRecyclerView = view.findViewById(R.id.messageRecyclerView);
        chatTitle = view.findViewById(R.id.chatTitle);

        if (friendName != null) {
            chatTitle.setText(friendName);
            settingsButton.setVisibility(View.GONE);
        } else if (chatId != null) {
            chatTitle.setText("Group " + chatId);
            fetchGroupDetails(chatId);
        }

        // Set up RecyclerView with reversed LinearLayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setReverseLayout(true); // Reverse layout to start from bottom
        messageRecyclerView.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter();
        messageRecyclerView.setAdapter(messageAdapter);

        // Scroll to bottom initially (if there are messages)
        if (messageAdapter.getItemCount() > 0) {
            messageRecyclerView.scrollToPosition(0); // Bottom is now index 0 due to reverse layout
        }

        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                if (friendUsername != null) {
                    sendMessage("@" + friendUsername + " " + message);
                } else if (chatId != null) {
                    sendMessage("@" + chatId + " " + message);
                }
                messageInput.setText("");
            } else {
                Log.w(TAG, "Send button clicked but message was empty.");
            }
        });

        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        settingsButton.setOnClickListener(v -> {
            if (chatId != null && creatorUsername != null) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new GroupChatSettingsFragment(chatId, creatorUsername, username));
                transaction.addToBackStack(null);
                transaction.commit();
                Log.d(TAG, "Navigating to GroupChatSettingsFragment for chatId: " + chatId);
            } else {
                Log.w(TAG, "Cannot open settings: chatId or creatorUsername is null");
            }
        });

        connectWebSocket();

        return view;
    }

    private void connectWebSocket() {
        String wsUrl = friendUsername != null ? BASE_WS_URL_DM + username : BASE_WS_URL_GROUP + username;
        Intent serviceIntent = new Intent(requireContext(), WebSocketService.class);
        serviceIntent.setAction("CONNECT");
        serviceIntent.putExtra("key", wsKey);
        serviceIntent.putExtra("url", wsUrl);
        requireActivity().startService(serviceIntent);
        Log.d(TAG, "WebSocket connection initiated for " + wsUrl);
    }

    private void sendMessage(String message) {
        Intent intent = new Intent("SendWebSocketMessage");
        intent.putExtra("key", wsKey);
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
        Log.d(TAG, "Message broadcast sent: " + message);
    }

    private void fetchGroupDetails(String chatId) {
        String groupUrl = BASE_GROUP_URL + chatId;
        JsonObjectRequest groupRequest = new JsonObjectRequest(
                Request.Method.GET,
                groupUrl,
                null,
                response -> {
                    try {
                        String groupName = response.optString("groupName", "Group " + chatId);
                        creatorUsername = response.optString("creator");
                        chatTitle.setText(groupName);
                        Log.d(TAG, "Group details fetched - Name: " + groupName + ", Creator: " + creatorUsername);

                        if (username.equals(creatorUsername)) {
                            settingsButton.setVisibility(View.VISIBLE);
                        } else {
                            settingsButton.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error fetching group details: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e(TAG, "Volley error fetching group details: " + error.getMessage());
                }
        );
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(groupRequest);
    }

    private void fetchChatHistory() {
        Log.d(TAG, "fetchChatHistory() called");
        if (friendUsername != null) {
            String msg = "@" + friendUsername + " /getChats";
            Log.d(TAG, "Sending message: " + msg);
            sendMessage(msg);
        } else if (chatId != null) {
            String msg = "@" + chatId + " /getChats";
            Log.d(TAG, "Sending message: " + msg);
            sendMessage(msg);
        } else {
            Log.w(TAG, "No friendUsername or chatId found for fetchChatHistory()");
        }
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String key = intent.getStringExtra("key");
            if (wsKey.equals(key)) {
                String message = intent.getStringExtra("message");
                Log.d(TAG, "Received message: " + message);
                if (message != null) {
                    String type;
                    if (message.startsWith("Welcome to the chat server")) {
                        type = "welcome";
                        isWebSocketConnected = true;
                        Log.d(TAG, "WebSocket confirmed connected");
                    } else if (message.contains(": ")) {
                        String sender = message.substring(0, message.indexOf(": "));
                        if (friendUsername != null) {
                            if (sender.equalsIgnoreCase(username) || sender.equalsIgnoreCase(friendUsername)) {
                                type = "dm";
                            } else {
                                return;
                            }
                        } else if (chatId != null) {
                            type = "group";
                        } else {
                            return;
                        }
                    } else {
                        type = "system";
                        if (friendUsername != null) {
                            return;
                        }
                    }
                    requireActivity().runOnUiThread(() -> {
                        messageAdapter.addMessage(new Message(message, type));
                        // Scroll to bottom (index 0) when a new message is added
                        messageRecyclerView.scrollToPosition(0);
                    });
                } else {
                    Log.w(TAG, "Received null message");
                }
            }
        }
    };

    private BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String key = intent.getStringExtra("key");
            if (wsKey.equals(key)) {
                isWebSocketConnected = true;
                Log.d(TAG, "WebSocket connection confirmed via broadcast");
                fetchChatHistory();
            }
        }
    };

    private BroadcastReceiver groupNameUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String updatedChatId = intent.getStringExtra("chatId");
            String newName = intent.getStringExtra("newName");
            if (chatId != null && chatId.equals(updatedChatId)) {
                chatTitle.setText(newName);
                Log.d(TAG, "Updated chat title to: " + newName);
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
                messageReceiver, new IntentFilter("WebSocketMessageReceived"));
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
                connectionReceiver, new IntentFilter("WebSocketConnected"));
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
                groupNameUpdateReceiver, new IntentFilter("GroupNameUpdated"));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(messageReceiver);
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(connectionReceiver);
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(groupNameUpdateReceiver);
        disconnectWebSocket();
    }

    private void disconnectWebSocket() {
        Intent serviceIntent = new Intent(requireContext(), WebSocketService.class);
        serviceIntent.setAction("DISCONNECT");
        serviceIntent.putExtra("key", wsKey);
        requireActivity().startService(serviceIntent);
        isWebSocketConnected = false;
    }

    private class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Message> messages = new ArrayList<>();

        public void addMessage(Message message) {
            messages.add(0, message); // Add new messages at the top (index 0) due to reverse layout
            notifyItemInserted(0);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new RecyclerView.ViewHolder(view) {};
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Message message = messages.get(position);
            TextView textView = holder.itemView.findViewById(android.R.id.text1);
            textView.setText(message.getContent());
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }
    }

    private class Message {
        private String content;
        private String type;

        public Message(String content, String type) {
            this.content = content;
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public String getType() {
            return type;
        }
    }
}