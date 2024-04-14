package se.hakanostrom.filmappen.api;

import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import se.hakanostrom.filmappen.R;

public class OmdbPosterClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context ctx) {
        retrofit = new Retrofit.Builder()
                .baseUrl(ctx.getResources().getString(R.string.omdb_poster_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
}
