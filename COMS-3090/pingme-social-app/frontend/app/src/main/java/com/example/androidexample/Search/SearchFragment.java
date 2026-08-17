package com.example.androidexample.Search;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidexample.LoginSignUp.LoginActivity;
import com.example.androidexample.MainActivity;
import com.example.androidexample.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private static final String BASE_SEARCH_URL = "http://coms-3090-029.class.las.iastate.edu:8080/search/";
    private static final String BASE_FOLLOW_URL = "http://coms-3090-029.class.las.iastate.edu:8080/users/";

    private EditText searchInput;
    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private ProgressBar progressBar;
    private String currentTab = "users";
    private boolean isUpdating = false;
    private View searchContainer;
    private Button enterButton;
    private String username;

    public SearchFragment() {}

    public static SearchFragment newInstance(String username) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString("USERNAME", username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString("USERNAME", "Guest");
        }
        Log.d("SearchFragment", "Current username: " + username);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchInput = view.findViewById(R.id.search_input);
        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        Button usersTab = view.findViewById(R.id.users_tab);
        Button postsTab = view.findViewById(R.id.posts_tab);
        searchContainer = view.findViewById(R.id.search_container);
        enterButton = view.findViewById(R.id.enter_button);

        searchContainer.setVisibility(View.GONE);
        enterButton.setVisibility(View.GONE);

        searchAdapter = new SearchAdapter(new ArrayList<>(), user -> {
            followUser(user.getUsername());
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(searchAdapter);

        usersTab.setOnClickListener(v -> {
            currentTab = "users";
            searchContainer.setVisibility(View.VISIBLE);
            enterButton.setVisibility(View.VISIBLE);
            searchInput.setText("@");
            searchInput.setSelection(searchInput.getText().length());
        });

        postsTab.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PostSearchActivity.class);
            startActivity(intent);
//            intent.putExtra("USERNAME", identifier); // Pass username
//            startActivity(intent);
//            finish();

//            currentTab = "posts";
//            searchContainer.setVisibility(View.VISIBLE);
//            enterButton.setVisibility(View.VISIBLE);
//            searchInput.setText("#");
//            searchInput.setSelection(searchInput.getText().length());
        });

        enterButton.setOnClickListener(v -> {
            String input = searchInput.getText().toString().trim();
            if (input.length() > 1) {
                performSearch(input);
            } else {
                Toast.makeText(getContext(), "Please enter a search query", Toast.LENGTH_SHORT).show();
            }
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating || s == null) return;

                String input = s.toString();
                String prefix = currentTab.equals("users") ? "@" : "#";

                if (!input.startsWith(prefix)) {
                    isUpdating = true;
                    searchInput.setText(prefix + input.replaceAll("^[#@]*", ""));
                    searchInput.setSelection(searchInput.getText().length());
                    isUpdating = false;
                }
            }
        });

        return view;
    }

    private void performSearch(String rawQuery) {
        String cleanQuery = rawQuery.replaceAll("^[#@]", "");
        String url = BASE_SEARCH_URL + cleanQuery;

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<User> users = new ArrayList<>();

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            User user = new User();
                            user.setUsername(obj.getString("username"));
                            user.setName(obj.getString("name"));
                            user.setEmail(obj.getString("email"));
                            user.setFollowers(parseStringArray(obj.getJSONArray("followers")));
                            user.setFollowing(parseStringArray(obj.getJSONArray("following")));
                            users.add(user);
                        }
                    } catch (JSONException e) {
                        Log.e("SearchFragment", "JSON parsing error: " + e.getMessage());
                        Toast.makeText(getContext(), "Failed to parse data", Toast.LENGTH_SHORT).show();
                    }

                    searchAdapter.updateList(users);
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    String msg = "Search failed: ";
                    if (error.networkResponse != null) {
                        msg += "Status code " + error.networkResponse.statusCode;
                    } else {
                        msg += error.toString();
                    }
                    Log.e("VolleyError", msg);
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void followUser(String targetUsername) {
        String cleanTargetUsername = targetUsername.replaceAll("^@", "");
        String url = BASE_FOLLOW_URL + username + "/following";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("followingUsername", cleanTargetUsername);
            Log.d("SearchFragment", "Sending follow request to: " + url);
            Log.d("SearchFragment", "Request body: " + requestBody.toString());
        } catch (JSONException e) {
            Log.e("SearchFragment", "JSON creation error: " + e.getMessage());
            Toast.makeText(getContext(), "Error preparing follow request", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("SearchFragment", "Follow response: " + response);
                    String trimmedResponse = response.trim();
                    if ("success".equals(trimmedResponse) || "User followed".equals(trimmedResponse)) {
                        Toast.makeText(getContext(), "Now following @" + cleanTargetUsername, Toast.LENGTH_SHORT).show();
                    } else if ("User does not exist".equals(trimmedResponse)) {
                        Toast.makeText(getContext(), "User @" + cleanTargetUsername + " does not exist", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Unexpected response: " + response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String msg = "Failed to follow @" + cleanTargetUsername + ": ";
                    if (error.networkResponse != null) {
                        msg += "Status code " + error.networkResponse.statusCode + ", Response: " + new String(error.networkResponse.data);
                    } else {
                        msg += error.toString();
                    }
                    Log.e("VolleyError", msg);
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                }) {
            @Override
            public byte[] getBody() {
                return requestBody.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private List<String> parseStringArray(JSONArray jsonArray) throws JSONException {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }
        return list;
    }
}