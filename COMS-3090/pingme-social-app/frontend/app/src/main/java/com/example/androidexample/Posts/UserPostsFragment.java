package com.example.androidexample.Posts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.androidexample.Profile.Post;
import com.example.androidexample.Profile.PostAdapter;
import com.example.androidexample.R;
import com.example.androidexample.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserPostsFragment extends Fragment {

    private static final String ARG_USERNAME = "USERNAME";
    private static final String BASE_URL = "http://coms-3090-029.class.las.iastate.edu:8080/users/";
    private String username;
    private RecyclerView postsRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    public UserPostsFragment() {
        // Required empty public constructor
    }

    public static UserPostsFragment newInstance(String username) {
        UserPostsFragment fragment = new UserPostsFragment();
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
        postList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_posts, container, false);

        postsRecyclerView = view.findViewById(R.id.postsRecyclerView);

        // Set up LinearLayoutManager to stack from top (default behavior)
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setReverseLayout(false); // Explicitly ensure top-to-bottom order (oldest first)
        postsRecyclerView.setLayoutManager(layoutManager);

        postAdapter = new PostAdapter(postList);
        postsRecyclerView.setAdapter(postAdapter);

        fetchUserPosts();

        return view;
    }

    private void fetchUserPosts() {
        String url = BASE_URL + username + "/posts";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        postList.clear(); // Clear existing posts to ensure fresh load
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject postJson = response.getJSONObject(i);
                            Post post = new Post();
                            post.setId(postJson.getInt("id"));
                            post.setUsername(postJson.getString("username"));
                            post.setTitle(postJson.getString("title"));
                            post.setDescription(postJson.getString("description"));
                            post.setTimestamp(postJson.getString("formattedTime"));

                            // Use tagsString for hashtags
                            if (postJson.isNull("tagsString")) {
                                post.setHashtags("");
                            } else {
                                JSONArray tagsStringArray = postJson.getJSONArray("tagsString");
                                List<String> tagsList = new ArrayList<>();
                                for (int j = 0; j < tagsStringArray.length(); j++) {
                                    tagsList.add(tagsStringArray.getString(j));
                                }
                                post.setHashtags(String.join(" ", tagsList));
                            }

                            String imageUrl = postJson.optString("imageUrl", "");
                            post.setImageUrl(imageUrl);

                            postList.add(post);
                        }
                        postAdapter.notifyDataSetChanged();
                        // Scroll to the top (index 0) after loading posts
                        postsRecyclerView.scrollToPosition(0);
                    } catch (JSONException e) {
                        Log.e("UserPostsFragment", "JSON parsing error: " + e.getMessage());
                        Toast.makeText(requireContext(), "Error parsing posts", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("UserPostsFragment", "Volley error: " + error.toString());
                    Toast.makeText(requireContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(jsonArrayRequest);
    }
}
