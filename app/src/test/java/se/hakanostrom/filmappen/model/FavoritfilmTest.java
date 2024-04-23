package se.hakanostrom.filmappen.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FavoritfilmTest {

    private final String FAVORITFILM_TITLE = "Min favoritfilm";
    private final String FAVORITFILM_TYPE = "Movie";
    private final String FAVORITFILM_YEAR = "2014";
    private final int FAVORITFILM_VALTBETYG = 5;

    Favoritfilm favoritfilm;

    @Before
    public void setUp() {
        favoritfilm = new Favoritfilm()
                .setTitle(FAVORITFILM_TITLE)
                .setType(FAVORITFILM_TYPE)
                .setYear(FAVORITFILM_YEAR)
                .setValtBetyg(FAVORITFILM_VALTBETYG);
    }

    @Test
    public void testGetterSetter() {
        assertEquals(FAVORITFILM_TITLE, favoritfilm.getTitle());
        assertEquals(FAVORITFILM_TYPE, favoritfilm.getType());
        assertEquals(FAVORITFILM_YEAR, favoritfilm.getYear());
        assertEquals(FAVORITFILM_VALTBETYG, favoritfilm.getValtBetyg());
    }

    @Test
    public void testToString() {
        String expected = String.format("%s [%s] (%s)", FAVORITFILM_TITLE, FAVORITFILM_TYPE, FAVORITFILM_YEAR);

        assertEquals(expected, favoritfilm.toString());
    }
}