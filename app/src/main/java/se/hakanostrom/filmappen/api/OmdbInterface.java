package se.hakanostrom.filmappen.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import se.hakanostrom.filmappen.model.Favoritfilm;
import se.hakanostrom.filmappen.model.SearchResult;

public interface OmdbInterface {

    @GET("?")
    Call<Favoritfilm> getMovieById(@Query("apikey") String apiKey, @Query("i") String imdbId);

    @GET("?")
    Call<SearchResult> searchMovies(@Query("apikey") String apiKey, @Query("s") String sokord);

}
