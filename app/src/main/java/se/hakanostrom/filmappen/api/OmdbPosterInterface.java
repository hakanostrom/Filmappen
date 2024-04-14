package se.hakanostrom.filmappen.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OmdbPosterInterface {

    @GET("?")
    Call<ResponseBody> getMoviePoster(@Query("apikey") String apiKey, @Query("i") String imdbId);
}
