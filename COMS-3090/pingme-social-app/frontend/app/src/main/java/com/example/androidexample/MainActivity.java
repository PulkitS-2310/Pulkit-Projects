package com.example.androidexample;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import com.example.androidexample.NotificationsFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.androidexample.AnalyticsFragment;
import com.example.androidexample.Friends.FriendsFragment;
import com.example.androidexample.Profile.ProfileFragment;
import com.example.androidexample.Search.SearchFragment;
import com.example.androidexample.Upload.ImagePosting.UploadFragment;
import com.example.androidexample.WelcomePage.TimeLine.TimeLineFragment;
import com.example.androidexample.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID    = "default";
    private static final int    REQ_NOTIF     = 101;

    private ActivityMainBinding binding;
    private String              username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // create our notification channel (Android 8+)
        createNotificationChannel();

        // request POST_NOTIFICATIONS on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{ Manifest.permission.POST_NOTIFICATIONS },
                        REQ_NOTIF
                );
            }
        }

        // normal view binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // timeline button (if you still want a quick-launch)
        findViewById(R.id.timeline_button).setOnClickListener(v -> {
            replaceFragment(new TimeLineFragment());
        });

        findViewById(R.id.notifications_button)
                .setOnClickListener(v -> replaceFragment(new NotificationsFragment()));

        findViewById(R.id.analytics_button).setOnClickListener(v -> {
            replaceFragment(new AnalyticsFragment());
        });
//        findViewById(R.id.analytics_button).setOnClickListener(v ->
//                replaceFragment(new AnalyticsFragment())
//        );
        // grab username from intent or default to "Guest"
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        if (username == null) username = "Guest";

        // default home fragment
        replaceFragment(HomeFragment.newInstance(username));

        // bottom nav
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                replaceFragment(HomeFragment.newInstance(username));
            } else if (itemId == R.id.nav_search) {
                replaceFragment(SearchFragment.newInstance(username)); // Pass username to SearchFragment
            } else if (itemId == R.id.nav_upload) {
                replaceFragment(new UploadFragment());
            } else if (itemId == R.id.nav_friends) {
                replaceFragment(FriendsFragment.newInstance(username));
            } else if (itemId == R.id.nav_profile) {
                replaceFragment(ProfileFragment.newInstance(username));
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        tx.replace(R.id.frame_layout, fragment);
        tx.addToBackStack(null);
        tx.commit();
    }

    // handle the notification‐permission result
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_NOTIF) {
            // if you want, you can check here whether they granted and
            // show a “please enable notifications” UI if they didn’t.
        }
    }

    // call this from anywhere once you have context + permission
    public void showNotification(String title, String body) {
        NotificationCompat.Builder b = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_24)  // your drawable
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true);

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {
            // permission missing, bail out
            return;
        }
        NotificationManagerCompat.from(this)
                .notify((int) System.currentTimeMillis(), b.build());

        // B) if the in-app NotificationsFragment is visible, inject it there too
        Fragment f = getSupportFragmentManager()
                .findFragmentById(R.id.frame_layout);
        if (f instanceof NotificationsFragment) {
            ((NotificationsFragment) f).addNotification(
                    new NotificationItem(title, body, System.currentTimeMillis())
            );
        }
    }

    // create the channel on Android 8+
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name        = "Default";
            String        description = "General notifications";
            int           importance  = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }
}
