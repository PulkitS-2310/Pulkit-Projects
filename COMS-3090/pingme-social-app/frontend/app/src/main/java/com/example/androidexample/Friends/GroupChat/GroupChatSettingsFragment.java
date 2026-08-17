package com.example.androidexample.Friends.GroupChat;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidexample.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GroupChatSettingsFragment extends Fragment {

    private String chatId;
    private String creatorUsername;
    private String currentUsername;
    private RecyclerView settingsRecyclerView;
    private SettingsAdapter settingsAdapter;

    public GroupChatSettingsFragment() {
        // Required empty public constructor
    }

    public GroupChatSettingsFragment(String chatId, String creatorUsername, String currentUsername) {
        this.chatId = chatId;
        this.creatorUsername = creatorUsername;
        this.currentUsername = currentUsername;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_chat_settings, container, false);

        ImageButton backButton = view.findViewById(R.id.settingsBackButton);
        settingsRecyclerView = view.findViewById(R.id.settingsRecyclerView);

        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Set up RecyclerView
        settingsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        settingsAdapter = new SettingsAdapter(getSettingsOptions(), chatId, creatorUsername, currentUsername, this);
        settingsRecyclerView.setAdapter(settingsAdapter);

        return view;
    }

    private List<String> getSettingsOptions() {
        List<String> options = new ArrayList<>();
        options.add("Edit Group Name");
        options.add("Add or Remove Member");
        options.add("Delete Group");
        return options;
    }

    // Method to handle group deletion success
    void onGroupDeleted() {
        Toast.makeText(requireContext(), "Group deleted successfully", Toast.LENGTH_SHORT).show();
        requireActivity().getSupportFragmentManager().popBackStack();
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    // Method to handle group name update success
    void onGroupNameUpdated() {
        Toast.makeText(requireContext(), "Group name updated successfully", Toast.LENGTH_SHORT).show();
    }

    // Method to fetch group name from server
    void fetchGroupName(Callback callback) {
        String url = "http://coms-3090-029.class.las.iastate.edu:8080/group/" + chatId;

        JsonObjectRequest groupRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        String groupName = response.optString("groupName", "");
                        Log.d("GroupChatSettings", "Fetched group name: " + groupName);
                        callback.onSuccess(groupName);
                    } catch (Exception e) {
                        Log.e("GroupChatSettings", "Error parsing group details: " + e.getMessage());
                        callback.onError("Failed to parse group details");
                    }
                },
                error -> {
                    String errorMsg = error.getMessage() != null ? error.getMessage() : "Unknown error";
                    Log.e("GroupChatSettings", "Error fetching group details: " + errorMsg);
                    callback.onError("Failed to fetch group details: " + errorMsg);
                }
        );

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(groupRequest);
    }

    // Callback interface for async group name fetching
    interface Callback {
        void onSuccess(String groupName);
        void onError(String error);
    }
}

class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder> {

    private List<String> settingsOptions;
    private String chatId;
    private String creatorUsername;
    private String currentUsername;
    private GroupChatSettingsFragment fragment;

    public SettingsAdapter(List<String> settingsOptions, String chatId, String creatorUsername, String currentUsername, GroupChatSettingsFragment fragment) {
        this.settingsOptions = settingsOptions;
        this.chatId = chatId;
        this.creatorUsername = creatorUsername;
        this.currentUsername = currentUsername;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public SettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new SettingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsViewHolder holder, int position) {
        String option = settingsOptions.get(position);
        holder.textView.setText(option);
        holder.itemView.setOnClickListener(v -> {
            if (option.equals("Delete Group")) {
                deleteGroup();
            } else if (option.equals("Edit Group Name")) {
                showEditGroupNameDialog();
            } else if (option.equals("Add or Remove Member")) {
                // Open EditGroupMembersFragment with chatId
                EditGroupMembersFragment editFragment = EditGroupMembersFragment.newInstance(
                        currentUsername, chatId);
                FragmentTransaction transaction = fragment.getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, editFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return settingsOptions.size();
    }

    private void showEditGroupNameDialog() {
        final EditText input = new EditText(fragment.requireContext());
        input.setHint("Enter new group name");

        new AlertDialog.Builder(fragment.requireContext())
                .setTitle("Edit Group Name")
                .setView(input)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        updateGroupName(newName);
                    } else {
                        Toast.makeText(fragment.requireContext(),
                                "Group name cannot be empty",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }

    private void updateGroupName(String newName) {
        fragment.fetchGroupName(new GroupChatSettingsFragment.Callback() {
            @Override
            public void onSuccess(String groupName) {
                String url = "http://coms-3090-029.class.las.iastate.edu:8080/group";

                RequestQueue queue = Volley.newRequestQueue(fragment.requireContext());

                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("groupName", groupName);
                    jsonBody.put("creator", creatorUsername);
                    jsonBody.put("newGroupName", newName);
                } catch (JSONException e) {
                    Log.e("GroupChatSettings", "JSON error: " + e.getMessage());
                    Toast.makeText(fragment.requireContext(),
                            "Error preparing request",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                        response -> {
                            Log.d("GroupChatSettings", "Name update successful: " + response.toString());
                            fragment.onGroupNameUpdated();
                            Intent intent = new Intent("GroupNameUpdated");
                            intent.putExtra("chatId", chatId);
                            intent.putExtra("newName", newName);
                            LocalBroadcastManager.getInstance(fragment.requireContext()).sendBroadcast(intent);
                        },
                        error -> {
                            String errorMsg = error.getMessage() != null ? error.getMessage() : "Unknown error";
                            if (error.networkResponse != null) {
                                errorMsg += ", Status Code: " + error.networkResponse.statusCode;
                                try {
                                    errorMsg += ", Response: " + new String(error.networkResponse.data, "UTF-8");
                                } catch (Exception e) {
                                    errorMsg += ", Failed to parse response";
                                }
                            }
                            Log.e("GroupChatSettings", "Name update failed: " + errorMsg);
                            Toast.makeText(fragment.requireContext(),
                                    "Failed to update group name: " + errorMsg,
                                    Toast.LENGTH_LONG).show();
                        });

                queue.add(jsonObjectRequest);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(fragment.requireContext(),
                        error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteGroup() {
        String url = "http://coms-3090-029.class.las.iastate.edu:8080/group/" + chatId;

        RequestQueue queue = Volley.newRequestQueue(fragment.requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    Log.d("GroupChatSettings", "Delete successful: " + response);
                    fragment.onGroupDeleted();
                },
                error -> {
                    String errorMsg = error.getMessage() != null ? error.getMessage() : "Unknown error";
                    Log.e("GroupChatSettings", "Delete failed: " + errorMsg);
                    Toast.makeText(fragment.requireContext(),
                            "Failed to delete group: " + errorMsg,
                            Toast.LENGTH_SHORT).show();
                });

        queue.add(stringRequest);
    }

    static class SettingsViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        SettingsViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}