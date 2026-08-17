package com.example.androidexample.WelcomePage.TimeLine;



import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidexample.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimeLineFragment extends Fragment {

    private RecyclerView recyclerView;
    private TimelineAdapter adapter;
    private ProgressBar progressBar;

    private static final String TIMELINE_URL = "http://coms-3090-029.class.las.iastate.edu:8080/users/jdog357/timeline"; // Replace with dynamic username if needed

    public TimeLineFragment() {}

    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        recyclerView = view.findViewById(R.id.timeline_recycler_view);
        progressBar = view.findViewById(R.id.timeline_progress_bar);

        adapter = new TimelineAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        fetchTimeline();

        return view;
    }

    private void fetchTimeline() {
        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, TIMELINE_URL, null,
                this::onResponse,
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("TimelineError", error.toString());
                    Toast.makeText(getContext(), "Error loading timeline", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void onResponse(JSONObject response) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONArray feedArray = response.getJSONArray("feed");
            List<TimelineItem> items = new ArrayList<>();

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject postObj = feedArray.getJSONObject(i);
                TimelineItem item = new TimelineItem(
                        postObj.getLong("id"),
                        postObj.getString("title"),
                        postObj.getString("description"),
                        postObj.getString("username"),
                        postObj.getString("formattedTime")
                );
                items.add(item);
            }

            adapter.updateTimeline(items);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to parse timeline data", Toast.LENGTH_SHORT).show();
        }
    }
}
