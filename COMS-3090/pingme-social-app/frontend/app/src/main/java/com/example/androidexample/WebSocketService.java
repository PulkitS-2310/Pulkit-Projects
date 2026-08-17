package com.example.androidexample;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class WebSocketService extends Service {

    private final Map<String, WebSocketClient> webSockets = new HashMap<>();
    private static final String TAG = "WebSocketService";
    private static final String CHANNEL_ID = "pingme_channel";

    public WebSocketService() {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if ("CONNECT".equals(action)) {
                String url = intent.getStringExtra("url");
                String key = intent.getStringExtra("key");
                connectWebSocket(key, url);
            } else if ("DISCONNECT".equals(action)) {
                String key = intent.getStringExtra("key");
                disconnectWebSocket(key);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel(); // Create notification channel on startup
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(messageReceiver, new IntentFilter("SendWebSocketMessage"));
        Log.d(TAG, "Service created and receiver registered");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (WebSocketClient client : webSockets.values()) {
            client.close();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        Log.d(TAG, "Service destroyed and receiver unregistered");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void connectWebSocket(String key, String url) {
        try {
            URI serverUri = URI.create(url);
            WebSocketClient webSocketClient = new WebSocketClient(serverUri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d(TAG, key + ": Connected to " + url);
                    Intent intent = new Intent("WebSocketConnected");
                    intent.putExtra("key", key);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }

                @Override
                public void onMessage(String message) {
                    Log.d(TAG, key + ": Message received from server: " + message);

                    try {
                        JSONObject json = new JSONObject(message);
                        String type = json.optString("type");

                        if ("notification".equals(type)) {
                            String title = json.optString("title", "New Notification");
                            String content = json.optString("content", "You have a new update");
                            sendNotification(title, content);
                        }

                        // Still broadcast to UI
                        Intent intent = new Intent("WebSocketMessageReceived");
                        intent.putExtra("key", key);
                        intent.putExtra("message", message);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                    } catch (JSONException e) {
                        Log.e(TAG, "Invalid JSON in message", e);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(TAG, key + ": Closed - Code: " + code + ", Reason: " + reason);
                    webSockets.remove(key);
                }

                @Override
                public void onError(Exception ex) {
                    Log.e(TAG, key + ": Error: " + ex.getMessage());
                    Intent intent = new Intent("WebSocketError");
                    intent.putExtra("key", key);
                    intent.putExtra("error", ex.getMessage());
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }
            };

            webSocketClient.connect();
            webSockets.put(key, webSocketClient);
            Log.d(TAG, key + ": WebSocket connection attempt started");

        } catch (Exception e) {
            Log.e(TAG, key + ": WebSocket connection error", e);
        }
    }

    private void disconnectWebSocket(String key) {
        WebSocketClient client = webSockets.get(key);
        if (client != null) {
            client.close();
            webSockets.remove(key);
            Log.d(TAG, key + ": WebSocket disconnected");
        }
    }

    private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String key = intent.getStringExtra("key");
            String message = intent.getStringExtra("message");
            Log.d(TAG, key + ": Broadcast received - Message: " + message);
            WebSocketClient webSocket = webSockets.get(key);
            if (webSocket != null && webSocket.isOpen()) {
                webSocket.send(message);
                Log.d(TAG, key + ": Message sent to server: " + message);
            } else {
                Log.w(TAG, key + ": WebSocket not open or not found");
            }
        }
    };

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "PingMe Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void sendNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
   //     notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
