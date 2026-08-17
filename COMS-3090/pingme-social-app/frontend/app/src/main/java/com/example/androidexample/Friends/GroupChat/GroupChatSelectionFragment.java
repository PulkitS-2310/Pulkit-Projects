package com.example.androidexample.Friends.GroupChat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.androidexample.Friends.FriendSelectionAdapter;
import com.example.androidexample.Friends.Message.MessagingFragment;
import com.example.androidexample.R;
import com.example.androidexample.Friends.SelectableFriend;
import com.example.androidexample.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupChatSelectionFragment extends Fragment {

    private RecyclerView friendsRecyclerView;
    private FriendSelectionAdapter adapter;
    private List<SelectableFriend> friendsList;
    private String username;
    private static final String BASE_URL = "http://coms-3090-029.class.las.iastate.edu:8080/users/";
    private static final String PREFS_NAME = "GroupChatPrefs";
    private static final String KEY_GROUP_CHATS = "groupChats";

    public GroupChatSelectionFragment() {}

    public static GroupChatSelectionFragment newInstance(String username) {
        GroupChatSelectionFragment fragment = new GroupChatSelectionFragment();
        Bundle args = new Bundle();
        args.putString("USERNAME", username);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_chat_selection, container, false);

        if (getArguments() != null) {
            username = getArguments().getString("USERNAME", "Guest");
        }

        friendsRecyclerView = view.findViewById(R.id.friendsSelectionRecyclerView);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        friendsList = new ArrayList<>();
        adapter = new FriendSelectionAdapter(friendsList);
        friendsRecyclerView.setAdapter(adapter);

        ImageButton closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        Button createChatButton = view.findViewById(R.id.createChatButton);
        createChatButton.setOnClickListener(v -> {
            List<String> selectedFriends = new ArrayList<>();
            for (SelectableFriend friend : friendsList) {
                if (friend.isSelected()) {
                    selectedFriends.add(friend.getUsername()); // Use username instead of name
                }
            }
            if (selectedFriends.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one friend", Toast.LENGTH_SHORT).show();
            } else {
                createGroupChat(selectedFriends);
            }
        });

        fetchFriends();

        return view;
    }

    private void fetchFriends() {
        String url = BASE_URL + username + "/friends";
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    friendsList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            String name = jsonObject.getString("name");
                            String username = jsonObject.getString("username");
                            friendsList.add(new SelectableFriend(name, username));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(requireContext(), "Failed to load friends", Toast.LENGTH_SHORT).show());
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(jsonArrReq);
    }

    private void createGroupChat(List<String> selectedFriends) {
        String url = "http://coms-3090-029.class.las.iastate.edu:8080/group";
        JSONObject requestBody = new JSONObject();
        try {
            // Generate group name from usernames (restored from old version)
            List<String> allUsernames = new ArrayList<>(selectedFriends);
            allUsernames.add(username); // Include creator
            String groupName = String.join(", ", allUsernames); // e.g., "user1, user2, user3"
            requestBody.put("groupName", groupName);
            requestBody.put("creator", username);
            JSONArray usernames = new JSONArray();
            usernames.put(username); // Include creator
            for (String friendUsername : selectedFriends) {
                usernames.put(friendUsername);
            }
            requestBody.put("usernames", usernames);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error creating group request", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                response -> {
                    try {
                        String chatId = response.getString("id"); // API returns "id" as per old version
                        String groupName = response.optString("groupName", String.join(", ", selectedFriends) + ", " + username); // Fallback to generated name
                        saveGroupChat(chatId, groupName, username);
                        Toast.makeText(requireContext(), "Group chat created with ID: " + chatId, Toast.LENGTH_SHORT).show();
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        // Fix: Pass 4 arguments to MessagingFragment.newInstance
                        fragmentTransaction.replace(R.id.frame_layout, MessagingFragment.newInstance(username, null, null, chatId));
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } catch (JSONException e) {
                        Toast.makeText(requireContext(), "Error parsing chat ID", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(requireContext(), "Failed to create group chat: " + error.toString(), Toast.LENGTH_SHORT).show());
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(jsonObjReq);
    }

    private void saveGroupChat(String chatId, String groupName, String creator) {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> groupChats = prefs.getStringSet(KEY_GROUP_CHATS, new HashSet<>());
        String groupEntry = chatId + "|" + groupName + "|" + creator;
        groupChats.add(groupEntry);
        editor.putStringSet(KEY_GROUP_CHATS, groupChats);
        editor.apply();
    }
}