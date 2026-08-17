package com.example.androidexample;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class NotificationsAdapterTest {
    // A no-op listener since adapter now requires one
    private static final NotificationAdapter.OnItemClickListener NO_OP_LISTENER = (item, pos) -> {};

    @Test
    public void getItemCount_emptyList_returnsZero() {
        NotificationAdapter adapter = new NotificationAdapter(
                Collections.emptyList(),
                NO_OP_LISTENER
        );
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void getItemCount_nonEmptyList_returnsSize() {
        NotificationItem item1 = new NotificationItem("Title1", "Body1", 123L);
        NotificationItem item2 = new NotificationItem("Title2", "Body2", 456L);
        NotificationAdapter adapter = new NotificationAdapter(
                Arrays.asList(item1, item2),
                NO_OP_LISTENER
        );
        assertEquals(2, adapter.getItemCount());
    }
}
