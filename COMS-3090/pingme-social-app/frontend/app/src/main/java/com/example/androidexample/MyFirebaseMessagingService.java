// File: app/src/main/java/com/example/androidexample/notification/MyFirebaseMessagingService.java
package com.example.androidexample;

import com.example.androidexample.R;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.example.androidexample.R;
import android.util.Log;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "friend_events";

    @Override
    public void onCreate() {
        super.onCreate();
        // Create channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Friend Events",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications when friends post or message you");
            NotificationManager mgr = getSystemService(NotificationManager.class);
            mgr.createNotificationChannel(channel);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage msg) {
        // Expect data payload like: { title:"New post", body:"Alice posted: Hello!", type:"new_post" }
        RemoteMessage.Notification notif = msg.getNotification();
        String title = notif!=null ? notif.getTitle() : msg.getData().get("title");
        String body  = notif!=null ? notif.getBody()  : msg.getData().get("body");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        NotificationManagerCompat.from(this)
                .notify((int)System.currentTimeMillis(), builder.build());
    }

    @Override
    public void onNewToken(String token) {
        // TODO: send token to your backend so it can target this device
        Log.d("FCMService","new token="+token);
    }
}
