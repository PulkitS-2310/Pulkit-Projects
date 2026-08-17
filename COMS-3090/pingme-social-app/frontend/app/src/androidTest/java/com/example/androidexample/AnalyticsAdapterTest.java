package com.example.androidexample;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class AnalyticsAdapterTest {
    @Test
    public void getItemCount_emptyList_returnsZero() {
        AnalyticsAdapter adapter = new AnalyticsAdapter(Collections.emptyList());
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void getItemCount_nonEmptyList_returnsSize() {
        AnalyticsItem item1 = new AnalyticsItem("A", "1");
        AnalyticsItem item2 = new AnalyticsItem("B", "2");
        AnalyticsAdapter adapter = new AnalyticsAdapter(Arrays.asList(item1, item2));
        assertEquals(2, adapter.getItemCount());
    }
}

