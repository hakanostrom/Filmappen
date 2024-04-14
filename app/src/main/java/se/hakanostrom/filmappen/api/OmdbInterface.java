package se.hakanostrom.filmappen.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import se.hakanostrom.filmappen.model.Favoritfilm;

public interface OmdbInterface {

    @GET("?")
    Call<Favoritfilm> getMovie(@Query("apikey") String apiKey, @Query("i") String imdbId);
}
