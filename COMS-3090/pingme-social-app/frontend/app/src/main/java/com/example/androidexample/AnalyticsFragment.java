//package com.example.androidexample;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.android.volley.Request;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class AnalyticsFragment extends Fragment {
//    private static final String TAG      = "AnalyticsFrag";
//    private static final String BASE_URL =
//            "http://coms-3090-029.class.las.iastate.edu:8080/analytics/";
//
//    private ProgressBar progressBar;
//    private RecyclerView rvMetrics;
//    private RecyclerView rvTopPosts;
//    private TextView tvEmptyMetrics;
//    private TextView tvEmptyTopPosts;
//    private AnalyticsAdapter metricsAdapter;
//    private TopPostsAdapter topAdapter;
//    private final List<AnalyticsItem> metrics = new ArrayList<>();
//    private final List<TopPostItem> topPosts   = new ArrayList<>();
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.fragment_analytics, container, false);
//
//        progressBar     = v.findViewById(R.id.progressBar);
//        rvMetrics       = v.findViewById(R.id.rvMetrics);
//        rvTopPosts      = v.findViewById(R.id.rvTopPosts);
//        tvEmptyMetrics  = v.findViewById(R.id.tvEmptyMetrics);
//        tvEmptyTopPosts = v.findViewById(R.id.tvEmptyTopPosts);
//
//        metricsAdapter = new AnalyticsAdapter(metrics);
//        rvMetrics.setLayoutManager(new LinearLayoutManager(requireContext()));
//        rvMetrics.setAdapter(metricsAdapter);
//
//        topAdapter = new TopPostsAdapter(topPosts);
//        rvTopPosts.setLayoutManager(new LinearLayoutManager(requireContext()));
//        rvTopPosts.setAdapter(topAdapter);
//
//        loadAnalytics();
//        return v;
//    }
//
//    private void loadAnalytics() {
//        progressBar.setVisibility(View.VISIBLE);
//        rvMetrics.setVisibility(View.GONE);
//        rvTopPosts.setVisibility(View.GONE);
//        tvEmptyMetrics.setVisibility(View.GONE);
//        tvEmptyTopPosts.setVisibility(View.GONE);
//
//        JsonObjectRequest req = new JsonObjectRequest(
//                Request.Method.GET,
//                BASE_URL + "<user>",
//                null,
//                this::onResponse,
//                error -> {
//                    progressBar.setVisibility(View.GONE);
//                    tvEmptyMetrics.setText("Failed to load analytics");
//                    tvEmptyMetrics.setVisibility(View.VISIBLE);
//                    Log.e(TAG, "Error fetching analytics", error);
//                }
//        );
//        Volley.newRequestQueue(requireContext()).add(req);
//    }
//
//    private void onResponse(JSONObject response) {
//        progressBar.setVisibility(View.GONE);
//        metrics.clear();
//        topPosts.clear();
//
//        try {
//            // 1) Followers count
//            String followers = response.optString("followersCount", "0");
//            metrics.add(new AnalyticsItem("Followers", followers));
//
//            // 2) Average posts per day
//            String avg = response.optString("averagePostsPerDay", "0");
//            metrics.add(new AnalyticsItem("Avg Posts/Day", avg));
//
//            // 3) Total likes per post
//            JSONArray likesArr = response.optJSONArray("likesPerPost");
//            if (likesArr != null) {
//                for (int i = 0; i < likesArr.length(); i++) {
//                    JSONObject obj = likesArr.getJSONObject(i);
//                    String title = obj.optString("postTitle");
//                    String count = obj.optString("likesCount");
//                    metrics.add(new AnalyticsItem(
//                            "Likes: " + title,
//                            count
//                    ));
//                }
//            }
//
//            // 4) Top 5 liked posts
//            JSONArray topArr = response.optJSONArray("topLikedPosts");
//            if (topArr != null) {
//                for (int i = 0; i < Math.min(5, topArr.length()); i++) {
//                    JSONObject obj = topArr.getJSONObject(i);
//                    String title = obj.optString("postTitle");
//                    String date  = obj.optString("likedDate");
//                    topPosts.add(new TopPostItem(title, date));
//                }
//            }
//
//        } catch (JSONException e) {
//            Log.e(TAG, "Parse error", e);
//        }
//
//        // show metrics
//        if (metrics.isEmpty()) {
//            tvEmptyMetrics.setText("No metrics available");
//            tvEmptyMetrics.setVisibility(View.VISIBLE);
//        } else {
//            metricsAdapter.notifyDataSetChanged();
//            rvMetrics.setVisibility(View.VISIBLE);
//        }
//        // show top posts
//        if (topPosts.isEmpty()) {
//            tvEmptyTopPosts.setText("No top posts available");
//            tvEmptyTopPosts.setVisibility(View.VISIBLE);
//        } else {
//            topAdapter.notifyDataSetChanged();
//            rvTopPosts.setVisibility(View.VISIBLE);
//        }
//    }
//}

package com.example.androidexample;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/** Displays a simple list of site‐wide stats from your flat JSON. */
public class AnalyticsFragment extends Fragment {
    private static final String TAG     = "AnalyticsFrag";
    private static final String URL     =
            "http://coms-3090-029.class.las.iastate.edu:8080/analytics/users";

    private ProgressBar progressBar;
    private RecyclerView rvMetrics;
    private TextView tvEmptyMetrics;
    private AnalyticsAdapter adapter;
    private final List<AnalyticsItem> metrics = new ArrayList<>();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_analytics, container, false);

        progressBar    = v.findViewById(R.id.progressBar);
        rvMetrics      = v.findViewById(R.id.rvMetrics);
        tvEmptyMetrics = v.findViewById(R.id.tvEmptyMetrics);

        adapter = new AnalyticsAdapter(metrics);
        rvMetrics.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMetrics.setAdapter(adapter);

        // show spinner
        progressBar.setVisibility(View.VISIBLE);
        rvMetrics.setVisibility(View.GONE);
        tvEmptyMetrics.setVisibility(View.GONE);

        loadAnalytics();
        return v;
    }

    private void loadAnalytics() {
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET, URL, null,
                this::onResponse,
                err -> {
                    progressBar.setVisibility(View.GONE);
                    tvEmptyMetrics.setText("Failed to load analytics");
                    tvEmptyMetrics.setVisibility(View.VISIBLE);
                    Log.e(TAG, "error", err);
                }
        );
        Volley.newRequestQueue(requireContext()).add(req);
    }

    private void onResponse(JSONObject resp) {
        progressBar.setVisibility(View.GONE);
        metrics.clear();

        // map each field into our list
        metrics.add(new AnalyticsItem(
                "Total Users", String.valueOf(resp.optInt("totalUsers", 0))
        ));
        metrics.add(new AnalyticsItem(
                "Active Users", String.valueOf(resp.optInt("activeUsers", 0))
        ));
        metrics.add(new AnalyticsItem(
                "New Users Today", String.valueOf(resp.optInt("newUsersToday", 0))
        ));
        metrics.add(new AnalyticsItem(
                "Total Posts", String.valueOf(resp.optInt("totalPosts", 0))
        ));
        metrics.add(new AnalyticsItem(
                "Total Comments", String.valueOf(resp.optInt("totalComments", 0))
        ));
        metrics.add(new AnalyticsItem(
                "Total Likes", String.valueOf(resp.optInt("totalLikes", 0))
        ));
        metrics.add(new AnalyticsItem(
                "Total Shares", String.valueOf(resp.optInt("totalShares", 0))
        ));
        metrics.add(new AnalyticsItem(
                "Avg Session (s)", String.valueOf(resp.optDouble("averageSessionDuration", 0.0))
        ));
        metrics.add(new AnalyticsItem(
                "Total Sessions", String.valueOf(resp.optInt("totalSessions", 0))
        ));
        metrics.add(new AnalyticsItem(
                "Peak Concurrent", String.valueOf(resp.optInt("peakConcurrentUsers", 0))
        ));
        metrics.add(new AnalyticsItem(
                "Last Updated", resp.optString("lastUpdated", "—")
        ));

        // swap UI
        if (metrics.isEmpty()) {
            tvEmptyMetrics.setVisibility(View.VISIBLE);
        } else {
            adapter.notifyDataSetChanged();
            rvMetrics.setVisibility(View.VISIBLE);
        }
    }
}
