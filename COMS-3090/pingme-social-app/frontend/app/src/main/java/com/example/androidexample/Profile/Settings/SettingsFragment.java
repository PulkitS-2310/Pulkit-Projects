// SettingsFragment.java
package com.example.androidexample.Profile.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidexample.Profile.Settings.Options.ChangePasswordFragment;
import com.example.androidexample.Profile.Settings.Options.DeleteAccountFragment;
import com.example.androidexample.Profile.Settings.Options.PersonalizationFragment;
import com.example.androidexample.Profile.ProfileFragment;
import com.example.androidexample.R;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private static final String ARG_USERNAME = "USERNAME";
    private RecyclerView recyclerView;
    private SettingsAdapter adapter;
    private String username;
    private ImageButton settingsBackButton;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(String username) {
        SettingsFragment fragment = new SettingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        recyclerView = view.findViewById(R.id.settingsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        settingsBackButton = view.findViewById(R.id.settingsBackButton);

        List<String> settingsOptions = new ArrayList<>();
        settingsOptions.add("Change Password");
        settingsOptions.add("Delete Account");
        settingsOptions.add("Personalization");

        adapter = new SettingsAdapter(settingsOptions, option -> {
            Fragment fragment = null;
            if ("Change Password".equals(option)) {
                fragment = new ChangePasswordFragment();
            } else if ("Delete Account".equals(option)) {
                fragment = new DeleteAccountFragment();
            } else if ("Personalization".equals(option)) {
                fragment = new PersonalizationFragment();
            }

            if (fragment != null) {
                Bundle args = new Bundle();
                args.putString("USERNAME", username);
                fragment.setArguments(args);
                replaceFragment(fragment);
            }
        });

        recyclerView.setAdapter(adapter);

        settingsBackButton.setOnClickListener(v -> {
            ProfileFragment profileFragment = ProfileFragment.newInstance(username);
            replaceFragment(profileFragment);
        });

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}