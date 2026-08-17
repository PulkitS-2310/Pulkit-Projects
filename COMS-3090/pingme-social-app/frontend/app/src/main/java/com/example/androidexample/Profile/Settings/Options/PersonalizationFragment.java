package com.example.androidexample.Profile.Settings.Options;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidexample.Profile.Settings.SettingsFragment;
import com.example.androidexample.R;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PersonalizationFragment extends Fragment {

    private static final String TAG = "Personalization";
    private static final String ARG_USERNAME = "USERNAME";
    private static final String BASE_URL = "http://coms-3090-029.class.las.iastate.edu:8080/users/";

    private String username;
    private EditText nicknameInput, descriptionInput;

    public PersonalizationFragment() {
        // Required empty public constructor
    }

    public static PersonalizationFragment newInstance(String username) {
        PersonalizationFragment fragment = new PersonalizationFragment();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personalization, container, false);

        nicknameInput = view.findViewById(R.id.nicknameInput);
        descriptionInput = view.findViewById(R.id.descriptionInput);
        Button saveButton = view.findViewById(R.id.endSaveButton);
        ImageButton backButton = view.findViewById(R.id.backButton);

        // Load current profile data
        loadProfileData();

        saveButton.setOnClickListener(v -> {
            String name = nicknameInput.getText().toString().trim();
            String bio = descriptionInput.getText().toString().trim();

            if (name.isEmpty()) {
                nicknameInput.setError("Name cannot be empty");
                return;
            }

            updateProfile(name, bio);
        });

        backButton.setOnClickListener(v -> navigateBack());

        return view;
    }

    private void loadProfileData() {
        // You can implement this to pre-fill the fields with current data
        // Example: Make a GET request to fetch current profile info
    }

    private void navigateBack() {
        // Navigate back to SettingsFragment with username
        SettingsFragment settingsFragment = SettingsFragment.newInstance(username);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, settingsFragment)
                .addToBackStack(null)
                .commit();
    }

    private void updateProfile(String name, String bio) {
        String url = BASE_URL + username + "/profile";

        Log.d(TAG, "Updating profile at: " + url);
        Log.d(TAG, "New name: " + name);
        Log.d(TAG, "New bio: " + bio);

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", name);
            jsonBody.put("bio", bio);

            StringRequest request = new StringRequest(
                    Request.Method.PUT,
                    url,
                    response -> {
                        Log.d(TAG, "Profile updated successfully");
                        Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                        navigateBack();
                    },
                    error -> {
                        String errorMsg = "Update failed: ";
                        if (error.networkResponse != null) {
                            errorMsg += "Status " + error.networkResponse.statusCode;
                            if (error.networkResponse.data != null) {
                                errorMsg += " - " + new String(error.networkResponse.data);
                            }
                        }
                        Log.e(TAG, errorMsg);
                        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
            ) {
                @Override
                public byte[] getBody() throws AuthFailureError {
                    return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    Log.d(TAG, "Raw response - Status: " + response.statusCode);
                    if (response.data != null && response.data.length > 0) {
                        Log.d(TAG, "Response data: " + new String(response.data));
                    }
                    return super.parseNetworkResponse(response);
                }
            };

            Volley.newRequestQueue(requireContext()).add(request);

        } catch (Exception e) {
            Log.e(TAG, "Error creating request", e);
            Toast.makeText(getContext(), "Error creating request", Toast.LENGTH_SHORT).show();
        }
    }
}