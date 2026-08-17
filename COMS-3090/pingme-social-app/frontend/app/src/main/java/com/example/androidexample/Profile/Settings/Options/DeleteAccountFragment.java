package com.example.androidexample.Profile.Settings.Options;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidexample.Profile.Settings.SettingsFragment;
import com.example.androidexample.R;

import org.json.JSONException;
import org.json.JSONObject;

public class DeleteAccountFragment extends Fragment {

    private static final String ARG_USERNAME = "USERNAME";
    private String username;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button deleteAccountButton;
    private ImageButton settingsBackButton;
    private static final String DELETE_URL = "http://coms-3090-029.class.las.iastate.edu:8080/users/";

    public DeleteAccountFragment() {
        // Required empty public constructor
    }

    public static DeleteAccountFragment newInstance(String username) {
        DeleteAccountFragment fragment = new DeleteAccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_USERNAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_account, container, false);

        usernameEditText = view.findViewById(R.id.et_delete_username);
        passwordEditText = view.findViewById(R.id.et_delete_password);
        deleteAccountButton = view.findViewById(R.id.btn_delete_account);
        settingsBackButton = view.findViewById(R.id.settingsBackButton);

        // Autofill username
        if (username != null) {
            usernameEditText.setText(username);
            usernameEditText.setEnabled(false);
        }

        deleteAccountButton.setOnClickListener(v -> {
            String enteredPassword = passwordEditText.getText().toString().trim();

            if (enteredPassword.isEmpty()) {
                showPopup("Error", "Password cannot be empty.");
            } else {
                deleteUser(username, enteredPassword);
            }
        });

        settingsBackButton.setOnClickListener(v -> navigateBack());

        return view;
    }

    private void navigateBack() {
        // Create new SettingsFragment with username
        SettingsFragment settingsFragment = SettingsFragment.newInstance(username);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, settingsFragment)
                .addToBackStack(null)
                .commit();
    }

    private void deleteUser(final String username, final String password) {
        String deleteUrl = DELETE_URL + username;

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            showPopup("Error", "Failed to create request body.");
            return;
        }

        StringRequest deleteRequest = new StringRequest(Request.Method.POST, deleteUrl,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.has("message") && "User deleted successfully".equals(jsonResponse.getString("message"))) {
                            showPopup("Success", "Account deleted successfully.");
                        } else {
                            showPopup("Error", "Failed to delete account. Please try again.");
                        }
                    } catch (JSONException e) {
                        showPopup("Success", "Account deleted successfully.");
                    }
                },
                error -> {
                    String errorMessage = "Failed to delete account. Please try again.";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMessage = new String(error.networkResponse.data);
                    }
                    showPopup("Error", errorMessage);
                    Log.e("DeleteAccount", "Error: " + error.toString());
                }) {
            @Override
            public byte[] getBody() {
                return requestBody.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        Volley.newRequestQueue(requireContext()).add(deleteRequest);
    }

    private void restartApp() {
        Intent intent = requireContext().getPackageManager().getLaunchIntentForPackage(requireContext().getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            requireActivity().finish();
        }
    }

    private void showPopup(String title, String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (title.equals("Success")) {
                        restartApp();
                    }
                })
                .show();
    }
}