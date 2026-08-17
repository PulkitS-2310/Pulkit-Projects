package com.example.androidexample.WelcomePage.TimeLine;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeAgo {

    public static String getTimeAgo(Date date) {
        long time = date.getTime();
        long now = System.currentTimeMillis();
        long diff = now - time;

        if (diff < TimeUnit.MINUTES.toMillis(1)) {
            return "just now";
        } else if (diff < TimeUnit.MINUTES.toMillis(60)) {
            return TimeUnit.MILLISECONDS.toMinutes(diff) + " minutes ago";
        } else if (diff < TimeUnit.HOURS.toMillis(24)) {
            return TimeUnit.MILLISECONDS.toHours(diff) + " hours ago";
        } else if (diff < TimeUnit.DAYS.toMillis(7)) {
            return TimeUnit.MILLISECONDS.toDays(diff) + " days ago";
        } else {
            return "more than a week ago";
        }
    }
}

