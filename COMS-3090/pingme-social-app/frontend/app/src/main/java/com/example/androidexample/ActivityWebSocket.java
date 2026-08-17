package com.example.androidexample;
//
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//public class ActivityWebSocket extends AppCompatActivity {
//
//    private TextView likeCountText;
//    private Button likeButton;
//    private int likeCount = 0;
//    private final String postId = "123"; // Simulated post ID
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_websocket);
//
//        likeCountText = findViewById(R.id.like_count);
//        likeButton = findViewById(R.id.like_button);
//
//        likeButton.setOnClickListener(v -> {
//            // Simulate a WebSocket-like "like" event
////            String mockMessage = "{\"type\":\"like\", \"postId\":\"" + postId + "\", \"likedBy\":\"user42\"}";
////            simulateWebSocketMessage(mockMessage);
//        });
//    }
//
//    private void simulateWebSocketMessage(String message) {
//        try {
//            JSONObject json = new JSONObject(message);
//
//            if (json.getString("type").equals("like") && json.getString("postId").equals(postId)) {
//                likeCount++;
//                likeCountText.setText("Likes: " + likeCount);
//
//                String user = json.getString("likedBy");
//                Toast.makeText(this, user + " liked this post!", Toast.LENGTH_SHORT).show();
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//}
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class ActivityWebSocket extends AppCompatActivity {

    private TextView likeCountText;
    private Button likeButton;
    private int likeCount = 0;
    private final String postId = "123";

    private WebSocket webSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_websocket);

        likeCountText = findViewById(R.id.like_count);
        likeButton = findViewById(R.id.like_button);

        // ✅ Connect to WebSocket server
        initWebSocket();

        likeButton.setOnClickListener(v -> {
            try {
                JSONObject likeMessage = new JSONObject();
                likeMessage.put("type", "like");
                likeMessage.put("postId", postId);
                likeMessage.put("likedBy", "gman"); // You can pass the actual username here

                Boolean check = webSocket.send(likeMessage.toString()); // ✅ Send to server
                Log.d("Websocket Test",check.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void initWebSocket() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("ws://coms-3090-029.class.las.iastate.edu:8080/ws") // ✅ Your backend WebSocket endpoint
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                runOnUiThread(() -> handleIncomingMessage(text));
                Log.d("Websocket Test", text);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                runOnUiThread(() -> Toast.makeText(ActivityWebSocket.this, "WebSocket failed: " + t.getMessage(), Toast.LENGTH_LONG).show());
            }

        });
    }

    private void handleIncomingMessage(String message) {
        try {
            JSONObject json = new JSONObject(message);
            if (json.getString("type").equals("like") && json.getString("postId").equals(postId)) {
                likeCount++;
                likeCountText.setText("Likes: " + likeCount);
                String user = json.getString("likedBy");
                Toast.makeText(this, user + " liked this post!", Toast.LENGTH_SHORT).show();
                Log.d("Websocket Test", message);
            }
        } catch (JSONException e) {
            Log.e("Websocket Test", e.getMessage());
            e.printStackTrace();
        }

    }
}