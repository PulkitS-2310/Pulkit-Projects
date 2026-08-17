package com.example.androidexample.Profile.Settings.Options;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidexample.Profile.Settings.SettingsFragment;
import com.example.androidexample.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ChangePasswordFragment extends Fragment {

    private static final String ARG_USERNAME = "USERNAME";
    private String username;
    private EditText usernameEditText;
    private EditText currentPasswordEditText;
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private Button changePasswordButton;
    private CheckBox showPasswordCheckbox;
    private ImageButton backButton;

    private static final String CHANGE_PASSWORD_URL = "http://coms-3090-029.class.las.iastate.edu:8080/user/change-password";

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public static ChangePasswordFragment newInstance(String username) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        usernameEditText = view.findViewById(R.id.et_username);
        currentPasswordEditText = view.findViewById(R.id.et_current_password);
        newPasswordEditText = view.findViewById(R.id.et_new_password);
        confirmPasswordEditText = view.findViewById(R.id.et_confirm_password);
        changePasswordButton = view.findViewById(R.id.btn_change_password);
        showPasswordCheckbox = view.findViewById(R.id.show_password_checkbox);
        backButton = view.findViewById(R.id.backButton);

        // Autofill username if available
        if (username != null) {
            usernameEditText.setText(username);
            usernameEditText.setEnabled(false); // Prevent editing
        }

        backButton.setOnClickListener(v -> navigateBack());

        changePasswordButton.setOnClickListener(v -> {
            String currentPassword = currentPasswordEditText.getText().toString().trim();
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (username.isEmpty() || currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showPopup("Change Password Failed", "Fields cannot be empty.");
            } else if (!newPassword.equals(confirmPassword)) {
                showPopup("Change Password Failed", "New passwords do not match.");
            } else {
                changePassword(username, currentPassword, newPassword);
            }
        });

        showPasswordCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                currentPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                newPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                confirmPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                currentPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                newPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                confirmPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

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

    private void changePassword(String username, String currentPassword, String newPassword) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CHANGE_PASSWORD_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String message = jsonResponse.getString("message");
                        if ("Password changed successfully".equals(message)) {
                            showPopup("Change Password Success", "Your password has been changed.");
                        } else {
                            showPopup("Change Password Failed", "Current password is incorrect.");
                        }
                    } catch (JSONException e) {
                        showPopup("Change Password Success", "Your password has been changed.");
                    }
                },
                error -> showPopup("Change Password Failed", "Check your internet connection and try again.")) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("password", currentPassword);
                    params.put("newPassword", newPassword);
                    return new JSONObject(params).toString().getBytes("utf-8");
                } catch (Exception e) {
                    return null;
                }
            }
        };

        Volley.newRequestQueue(requireContext()).add(stringRequest);
    }

    private void showPopup(String title, String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}