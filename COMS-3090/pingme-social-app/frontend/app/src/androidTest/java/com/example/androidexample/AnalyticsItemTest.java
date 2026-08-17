package com.example.androidexample;

import org.junit.Test;

import static org.junit.Assert.*;

public class AnalyticsItemTest {
    @Test
    public void gettersReturnCorrectValues() {
        AnalyticsItem item = new AnalyticsItem("Total Users", "42");
        assertEquals("Total Users", item.getMetric());
        assertEquals("42", item.getValue());
    }
}
