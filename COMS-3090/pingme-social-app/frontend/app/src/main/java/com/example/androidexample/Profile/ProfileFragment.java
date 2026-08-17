package com.example.androidexample.Profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.androidexample.Posts.UserPostsFragment;
import com.example.androidexample.R;
import com.example.androidexample.Profile.Settings.SettingsFragment;
import com.example.androidexample.VolleySingleton;

import org.json.JSONException;

public class ProfileFragment extends Fragment {

    private static final String ARG_USERNAME = "USERNAME";
    private static final String BASE_URL = "http://coms-3090-029.class.las.iastate.edu:8080/users/";
    private String username;
    private TextView nameTextView;
    private TextView usernameTextView;
    private TextView descriptionTextView;
    private ImageButton settingsButton;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String username) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_USERNAME);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        nameTextView = view.findViewById(R.id.nameText);
        usernameTextView = view.findViewById(R.id.usernameText);
        descriptionTextView = view.findViewById(R.id.userDescription);
        settingsButton = view.findViewById(R.id.settingsButton);

        nameTextView.setText("Loading...");
        usernameTextView.setText("@" + username);
        descriptionTextView.setText("No description set");

        // Add UserPostsFragment as a child fragment
        UserPostsFragment userPostsFragment = UserPostsFragment.newInstance(username);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.postsContainer, userPostsFragment)
                .commit();

        fetchProfileData();

        settingsButton.setOnClickListener(v -> replaceFragment(SettingsFragment.newInstance(username)));

        return view;
    }

    private void fetchProfileData() {
        String url = BASE_URL + username + "/profile";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        String name = response.getString("name");
                        String usernameFromResponse = response.getString("username");
                        String bio = response.optString("bio", "No description set");

                        nameTextView.setText(name);
                        usernameTextView.setText("@" + usernameFromResponse);
                        descriptionTextView.setText(bio);
                    } catch (JSONException e) {
                        Log.e("ProfileFragment", "JSON parsing error: " + e.getMessage());
                        Toast.makeText(requireContext(), "Error parsing profile data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("ProfileFragment", "Volley error: " + error.toString());
                    Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                    nameTextView.setText(username);
                });

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void replaceFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}