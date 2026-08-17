package com.example.androidexample;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility;

@RunWith(AndroidJUnit4.class)
public class NotificationsFragmentTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testNavigateToNotifications_showsEmptyState() {
        // Tap the notifications button in MainActivity
        onView(withId(R.id.notifications_button)).perform(click());
        // Verify empty-state TextView is displayed
        onView(withId(R.id.tvEmptyMetrics)).check(matches(isDisplayed()));
    }

    @Test
    public void testRecyclerViewInitiallyHidden() {
        // Navigate
        onView(withId(R.id.notifications_button)).perform(click());
        // Verify RecyclerView is gone
        onView(withId(R.id.notifications_recycler))
                .check(matches(withEffectiveVisibility(Visibility.GONE)));
    }
}
