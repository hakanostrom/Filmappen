package se.hakanostrom.filmappen;

import android.app.Instrumentation;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class IntentsTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void intentNavigation() {
        Instrumentation.ActivityMonitor sm = getInstrumentation().addMonitor(SearchActivity.class.getName(), null, true);

        onView(withId(R.id.nav_search)).perform(click());

        sm.waitForActivityWithTimeout(2000L);

        assertEquals(1, sm.getHits());
    }
}
