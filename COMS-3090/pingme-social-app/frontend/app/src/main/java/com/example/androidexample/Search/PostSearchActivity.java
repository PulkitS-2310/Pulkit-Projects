package com.example.androidexample.Search;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidexample.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostSearchActivity extends AppCompatActivity {
    private static final String TAG = "PostSearchActivity";
    private static final String BASE_SEARCH_URL =
            "http://coms-3090-029.class.las.iastate.edu:8080/search/post/";

    private EditText searchInput;
    private Button searchButton;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private PostUserAdapter adapter;
    private List<PostUser> postList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_post);

        searchInput   = findViewById(R.id.post_search_input);
        searchButton  = findViewById(R.id.post_search_button);
        recyclerView  = findViewById(R.id.post_recycler_view);
        progressBar   = findViewById(R.id.progress_bar);

        postList = new ArrayList<>();
        adapter  = new PostUserAdapter(
                postList,
                post -> Toast.makeText(this, "Post: " + post.getTitle(), Toast.LENGTH_SHORT).show()
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Trigger search on button
        searchButton.setOnClickListener(v -> doSearch());

        // Also trigger when user taps “Search” on the keyboard
        searchInput.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch();
                return true;
            }
            return false;
        });
    }

    private void doSearch() {
        String query = searchInput.getText().toString();
        if (query.isEmpty()) {
            Toast.makeText(this, "Enter a title to search", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(ProgressBar.VISIBLE);
        recyclerView.setVisibility(RecyclerView.GONE);

        String url = BASE_SEARCH_URL + query;
        Log.d(TAG, "Request URL: " + url);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    postList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            PostUser post = new PostUser();
                            post.setId(obj.getLong("id"));
                            post.setTitle(obj.getString("title"));
                            post.setDescription(obj.getString("description"));
                            post.setHashtags(obj.optJSONArray("hashtags").toString());
                            post.setUsername(obj.getString("username"));
                            post.setFormattedTime(obj.getString("formattedTime"));
                            post.setTimeRaw(obj.getString("timeRaw"));
                            postList.add(post);
                            Log.d(TAG, "  + added post: " + post.getTitle());
                        }
                        adapter.updateList(postList);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parse error", e);
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    } finally {
                        progressBar.setVisibility(ProgressBar.GONE);
                        recyclerView.setVisibility(RecyclerView.VISIBLE);
                    }
                },
                error -> {
                    Log.e(TAG, "volley error " + error.getMessage());
                    Toast.makeText(this, "Failed to fetch posts", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(ProgressBar.GONE);
                }
        );

        Volley.newRequestQueue(this).add(request);
    }
}
