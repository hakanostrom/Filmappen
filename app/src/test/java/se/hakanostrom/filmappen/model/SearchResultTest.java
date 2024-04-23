package se.hakanostrom.filmappen.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SearchResultTest {

    private final String SINGLERESULT_TITLE = "Min favoritfilm";
    private final String SINGLERESULT_YEAR = "2014";

    SearchResult.SingleResult singleResult;

    @Before
    public void setUp() {
        singleResult = new SearchResult.SingleResult();
        singleResult.title = SINGLERESULT_TITLE;
        singleResult.year = SINGLERESULT_YEAR;
    }

    @Test
    public void testToString() {
        String expected = String.format("%s (%s)", SINGLERESULT_TITLE, SINGLERESULT_YEAR);

        assertEquals(expected, singleResult.toString());
    }
}