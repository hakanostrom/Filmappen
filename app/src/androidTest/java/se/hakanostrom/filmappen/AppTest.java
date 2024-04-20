package se.hakanostrom.filmappen;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class AppTest {

    private Context appContext;

    @Before
    public void setUp() {
        appContext = getInstrumentation().getTargetContext();
    }

    @Test
    public void useAppContext() {
        assertEquals("se.hakanostrom.filmappen", appContext.getPackageName());
    }

    @Test
    public void stringResources() {
        assertEquals("filmappen-databas", appContext.getString(R.string.database_name));
        assertEquals("http://www.omdbapi.com", appContext.getString(R.string.omdb_base_url));
        assertEquals("http://img.omdbapi.com", appContext.getString(R.string.omdb_poster_base_url));
        assertNotEquals("", appContext.getString(R.string.api_key));

        assertEquals("movie", appContext.getString(R.string.search_alt_movie_key));
        assertEquals("series", appContext.getString(R.string.search_alt_series_key));
        assertEquals("episode", appContext.getString(R.string.search_alt_episode_key));
    }
}
