package com.example.androidexample.Friends.GroupChat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.androidexample.Friends.FriendSelectionAdapter;
import com.example.androidexample.Friends.SelectableFriend;
import com.example.androidexample.R;
import com.example.androidexample.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EditGroupMembersFragment extends Fragment {

    private RecyclerView friendsRecyclerView;
    private FriendSelectionAdapter adapter;
    private List<SelectableFriend> friendsList;
    private String username; // Logged-in user
    private String chatId;   // Group ID
    private String creator;  // Group creator, fetched from server
    private List<String> currentMembers; // Existing group members
    private static final String BASE_URL = "http://coms-3090-029.class.las.iastate.edu:8080/users/";
    private static final String GROUP_URL = "http://coms-3090-029.class.las.iastate.edu:8080/group/";
    private static final String PREFS_NAME = "GroupChatPrefs";

    public EditGroupMembersFragment() {
        // Required empty public constructor
    }

    public static EditGroupMembersFragment newInstance(String username, String chatId) {
        EditGroupMembersFragment fragment = new EditGroupMembersFragment();
        Bundle args = new Bundle();
        args.putString("USERNAME", username);
        args.putString("CHAT_ID", chatId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString("USERNAME");
            chatId = getArguments().getString("CHAT_ID");
        }
        currentMembers = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_group_members, container, false);

        // Set header text
        TextView headerText = view.findViewById(R.id.headerText);
        headerText.setText("Edit Group Members");

        // Initialize RecyclerView
        friendsRecyclerView = view.findViewById(R.id.friendsSelectionRecyclerView);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        friendsList = new ArrayList<>();
        adapter = new FriendSelectionAdapter(friendsList);
        friendsRecyclerView.setAdapter(adapter);

        // Close button
        ImageButton closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Update group button
        Button updateGroupButton = view.findViewById(R.id.updateGroupButton);
        updateGroupButton.setOnClickListener(v -> {
            List<String> selectedUsernames = new ArrayList<>();
            for (SelectableFriend friend : friendsList) {
                if (friend.isSelected()) {
                    selectedUsernames.add(friend.getUsername());
                }
            }
            if (selectedUsernames.isEmpty() && (creator == null || !creator.equals(username))) {
                Toast.makeText(requireContext(), "Please select at least one member", Toast.LENGTH_SHORT).show();
            } else if (creator == null) {
                Toast.makeText(requireContext(), "Creator not loaded, please try again", Toast.LENGTH_SHORT).show();
            } else {
                // Ensure creator is included in the list
                if (!selectedUsernames.contains(creator)) {
                    selectedUsernames.add(creator);
                }
                updateGroup(selectedUsernames);
            }
        });

        // Fetch group members first
        fetchGroupMembers();

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
                            String friendUsername = jsonObject.getString("username");
                            SelectableFriend friend = new SelectableFriend(name, friendUsername);
                            friend.setSelected(currentMembers.contains(friendUsername));
                            friendsList.add(friend);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(requireContext(), "Failed to load friends", Toast.LENGTH_SHORT).show());
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(jsonArrReq);
    }

    private void fetchGroupMembers() {
        String url = GROUP_URL + chatId; // GET /group/{chatId}
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray usernames = response.getJSONArray("usernames");
                        creator = response.optString("creator"); // Fetch creator from response
                        currentMembers.clear();
                        for (int i = 0; i < usernames.length(); i++) {
                            currentMembers.add(usernames.getString(i));
                        }
                        fetchFriends(); // Fetch friends after getting group members
                    } catch (JSONException e) {
                        Toast.makeText(requireContext(), "Error parsing group members", Toast.LENGTH_SHORT).show();
                        fetchFriends(); // Still fetch friends to show the list
                    }
                },
                error -> {
                    Toast.makeText(requireContext(), "Failed to load group members", Toast.LENGTH_SHORT).show();
                    fetchFriends(); // Fetch friends even if group members fail
                });
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(jsonObjReq);
    }

    private void updateGroup(List<String> selectedUsernames) {
        String url = "http://coms-3090-029.class.las.iastate.edu:8080/group"; // PUT /group
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("groupId", chatId);
            requestBody.put("creator", creator); // Use fetched creator
            JSONArray usernamesArray = new JSONArray();
            for (String user : selectedUsernames) {
                usernamesArray.put(user);
            }
            requestBody.put("usernames", usernamesArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                requestBody,
                response -> {
                    Toast.makeText(requireContext(), "Group updated successfully", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                },
                error -> Toast.makeText(requireContext(), "Failed to update group: " + error.toString(), Toast.LENGTH_SHORT).show());
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(jsonObjReq);
    }
}