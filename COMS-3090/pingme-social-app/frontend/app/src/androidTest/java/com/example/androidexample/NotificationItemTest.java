package com.example.androidexample;


import org.junit.Test;
import static org.junit.Assert.*;

    public class NotificationItemTest {
        @Test
        public void notificationItem_gettersReturnValues() {
            long ts = 1620000000000L;
            NotificationItem item = new NotificationItem("Title1", "Body1", ts);
            assertEquals("Title1", item.getTitle());
            assertEquals("Body1", item.getBody());
            assertEquals(ts, item.getTimestamp());
        }
    }

