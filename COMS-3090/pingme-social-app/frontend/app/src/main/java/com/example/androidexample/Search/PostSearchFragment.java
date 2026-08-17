//package com.example.androidexample.Search;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.android.volley.Request;
//import com.android.volley.toolbox.JsonArrayRequest;
//import com.android.volley.toolbox.Volley;
//import com.example.androidexample.R;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//import android.view.inputmethod.EditorInfo;
//
//
//public class PostSearchFragment extends Fragment {
//    private static final String TAG = "PostSearchFragment";
//    private static final String BASE_SEARCH_URL =
//            "http://coms-3090-029.class.las.iastate.edu:8080/search/post/First Post";
//
//    private EditText postSearchInput;
//    private Button searchButton;
//    private RecyclerView postRecyclerView;
//    private ProgressBar progressBar;
//
//    private PostUserAdapter postAdapter;
//    private List<PostUser> postList;
//
//    public PostSearchFragment() { }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        Toast.makeText(getContext(), "Loaded PostSearchFragment",
//                Toast.LENGTH_SHORT).show();
//        Log.d(TAG, "onCreateView");
//
//        View view = inflater.inflate(R.layout.fragment_post, container, false);
//
//        // 1) Bind views
//        postSearchInput  = view.findViewById(R.id.post_search_input);
//        searchButton     = view.findViewById(R.id.post_search_button);
//        postRecyclerView = view.findViewById(R.id.post_recycler_view);
//        progressBar      = view.findViewById(R.id.progress_bar);
//
//        // 2) Check button binding
//        if (searchButton == null) {
//            Log.e(TAG, ">>> searchButton is NULL! Check ID in XML");
//        } else {
//            Log.d(TAG, ">>> searchButton bound OK");
//            searchButton.setOnClickListener(v -> {
//                Log.d(TAG, ">>> BUTTON CLICKED!");
//                Toast.makeText(getContext(), "Search clicked!",
//                        Toast.LENGTH_SHORT).show();
//                onSearchButtonClicked();
//            });
//        }
//
//        // 3) Setup RecyclerView + Adapter
//        postList = new ArrayList<>();
//        postAdapter = new PostUserAdapter(postList, post -> {
//            if (post != null) {
//                Toast.makeText(getContext(), "Post: " + post.getTitle(),
//                        Toast.LENGTH_SHORT).show();
//            } else {
//                Log.e(TAG, "Clicked post is null");
//            }
//        });
//        postSearchInput.setOnEditorActionListener((v, actionId, event) -> {
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                onSearchButtonClicked();
//                return true;
//            }
//            return false;
//        });
//
//        postRecyclerView.setLayoutManager(
//                new LinearLayoutManager(getContext()));
//        postRecyclerView.setAdapter(postAdapter);
//
//        return view;
//    }
//
//    private void onSearchButtonClicked() {
//        String query = "";
//        if (postSearchInput != null && postSearchInput.getText() != null) {
//            query = postSearchInput.getText().toString().trim();
//        }
//        Log.d(TAG, "Query entered: \"" + query + "\"");
//
//        if (query.isEmpty()) {
//            Toast.makeText(getContext(),
//                    "Please enter a title to search",
//                    Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        progressBar.setVisibility(View.VISIBLE);
//        postRecyclerView.setVisibility(View.GONE);
//
//        String url = BASE_SEARCH_URL + query;
//        Log.d(TAG, "Request URL: " + url);
//
//        JsonArrayRequest request = new JsonArrayRequest(
//                Request.Method.GET, url, null,
//                response -> {
//                    Log.d(TAG, "Response: " + response.toString());
//                    postList.clear();
//                    try {
//                        for (int i = 0; i < response.length(); i++) {
//                            JSONObject obj = response.getJSONObject(i);
//                            PostUser post = new PostUser();
//                            post.setId(obj.getLong("id"));
//                            post.setTitle(obj.getString("title"));
//                            post.setDescription(obj.getString("description"));
//                            post.setUsername(obj.getString("username"));
//                            post.setFormattedTime(obj.getString("formattedTime"));
//                            post.setTimeRaw(obj.getString("timeRaw"));
//                            postList.add(post);
//                            Log.d(TAG, "  • added post: " + post.getTitle());
//                        }
//                        postAdapter.updateList(postList);
//                        progressBar.setVisibility(View.GONE);
//                        postRecyclerView.setVisibility(View.VISIBLE);
//                    } catch (JSONException e) {
//                        Log.e(TAG, "JSON parse error", e);
//                        Toast.makeText(getContext(),
//                                "Error parsing data",
//                                Toast.LENGTH_SHORT).show();
//                        progressBar.setVisibility(View.GONE);
//                    }
//                },
//                error -> {
//                    Log.e(TAG, "Volley error", error);
//                    Toast.makeText(getContext(),
//                            "Failed to fetch posts",
//                            Toast.LENGTH_SHORT).show();
//                    progressBar.setVisibility(View.GONE);
//                }
//        );
//
//        Volley.newRequestQueue(requireContext()).add(request);
//    }
//}
