package com.example.androidexample.Friends;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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
import com.example.androidexample.Friends.GroupChat.GroupChatSelectionFragment;
import com.example.androidexample.Friends.Item.Item;
import com.example.androidexample.Friends.Item.ItemsAdapter;
import com.example.androidexample.Friends.Message.MessagingFragment;
import com.example.androidexample.R;
import com.example.androidexample.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsFragment extends Fragment {

    private RecyclerView friendsRecyclerView;
    private ItemsAdapter adapter;
    private List<Item> itemsList;
    private String username;
    private static final String BASE_URL = "http://coms-3090-029.class.las.iastate.edu:8080/";
    private static final String GROUP_BASE_URL = BASE_URL + "group/";

    public FriendsFragment() {}

    public static FriendsFragment newInstance(String username) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString("USERNAME", username);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        if (getArguments() != null) {
            username = getArguments().getString("USERNAME", "Guest");
        }

        friendsRecyclerView = view.findViewById(R.id.friendsRecyclerView);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        itemsList = new ArrayList<>();
        adapter = new ItemsAdapter(requireContext(), itemsList, item -> {
            if (item.getType() == Item.TYPE_FRIEND) {
                navigateToDM(item.getName(), item.getUsername());
            } else if (item.getType() == Item.TYPE_GROUP_CHAT) {
                navigateToGroupChat(item.getChatId());
            }
        });
        friendsRecyclerView.setAdapter(adapter);

        ImageButton composeButton = view.findViewById(R.id.composeButton);
        composeButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, GroupChatSelectionFragment.newInstance(username));
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            Log.d("FriendsFragment", "Navigating to GroupChatSelectionFragment");
        });

        fetchFriendsAndGroupChats();
        return view;
    }

    private void fetchFriendsAndGroupChats() {
        fetchFriends();
        fetchGroupChats();
    }

    private void fetchFriends() {
        String url = BASE_URL + "users/" + username + "/friends";
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            String name = jsonObject.getString("name");
                            String username = jsonObject.getString("username");
                            itemsList.add(new Item(Item.TYPE_FRIEND, name, username));
                        }
                        adapter.notifyDataSetChanged();
                        Log.d("FriendsFragment", "Friends loaded: " + itemsList.size());
                    } catch (JSONException e) {
                        Log.e("FriendsFragment", "JSON error: " + e.getMessage());
                    }
                },
                error -> Log.e("FriendsFragment", "Volley error: " + error.getMessage()));
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(jsonArrReq);
    }

    private void fetchGroupChats() {
        String profileUrl = BASE_URL + "users/" + username + "/profile";
        JsonObjectRequest profileRequest = new JsonObjectRequest(
                Request.Method.GET,
                profileUrl,
                null,
                response -> {
                    try {
                        JSONArray chats = response.getJSONArray("chats");
                        for (int i = 0; i < chats.length(); i++) {
                            String chatId = String.valueOf(chats.getInt(i));
                            fetchGroupDetails(chatId);
                        }
                        if ("gman".equals(username)) {
                            fetchGroupDetails("352");
                        }
                    } catch (JSONException e) {
                        Log.e("FriendsFragment", "JSON error: " + e.getMessage());
                    }
                },
                error -> Log.e("FriendsFragment", "Volley error: " + error.getMessage()));
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(profileRequest);
    }

    private void fetchGroupDetails(String chatId) {
        String groupUrl = GROUP_BASE_URL + chatId;
        Log.d("FriendsFragment", "Fetching group from: " + groupUrl);

        JsonObjectRequest groupRequest = new JsonObjectRequest(
                Request.Method.GET,
                groupUrl,
                null,
                response -> {
                    try {
                        Log.d("FriendsFragment", "Group response: " + response.toString());

                        String groupName = response.optString("groupName", "Group " + chatId);
                        List<String> groupUsernames = new ArrayList<>();

                        if (response.has("usernames")) {
                            JSONArray usernamesArray = response.getJSONArray("usernames");
                            for (int i = 0; i < usernamesArray.length(); i++) {
                                groupUsernames.add(usernamesArray.getString(i));
                            }
                        }

                        itemsList.add(new Item(Item.TYPE_GROUP_CHAT, groupName, chatId, groupUsernames));
                        adapter.notifyDataSetChanged();
                        Log.d("FriendsFragment", "Group loaded: " + groupName);
                    } catch (JSONException e) {
                        Log.e("FriendsFragment", "JSON error: " + e.getMessage());
                        itemsList.add(new Item(Item.TYPE_GROUP_CHAT, "Group " + chatId, chatId, new ArrayList<>()));
                        adapter.notifyDataSetChanged();
                    }
                },
                error -> {
                    Log.e("FriendsFragment", "Volley error: " + error.getMessage());
                    if (error.networkResponse != null) {
                        String errorData = new String(error.networkResponse.data);
                        Log.e("FriendsFragment", "Error response: " + errorData);
                        itemsList.add(new Item(Item.TYPE_GROUP_CHAT, "Group " + chatId, chatId, new ArrayList<>()));
                        adapter.notifyDataSetChanged();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(groupRequest);
    }

    private void navigateToDM(String friendName, String friendUsername) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Pass both friendName and friendUsername to MessagingFragment
        fragmentTransaction.replace(R.id.frame_layout, MessagingFragment.newInstance(username, friendName, friendUsername, null));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        Log.d("FriendsFragment", "Navigating to DM with " + friendName + " (" + friendUsername + ")");
    }

    private void navigateToGroupChat(String chatId) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, MessagingFragment.newInstance(username, null, null, chatId));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        Log.d("FriendsFragment", "Navigating to group chat with chatId: " + chatId);
    }
}