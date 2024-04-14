package se.hakanostrom.filmappen;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.hakanostrom.filmappen.api.OmdbClient;
import se.hakanostrom.filmappen.api.OmdbInterface;
import se.hakanostrom.filmappen.model.Favoritfilm;

public class SearchActivity extends AppCompatActivity {

    OmdbInterface omdbInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        omdbInterface = OmdbClient.getClient(SearchActivity.this).create(OmdbInterface.class);

        Call<Favoritfilm> call = omdbInterface.getMovie(getResources().getString(R.string.api_key), "tt15398776");
        call.enqueue(new Callback<Favoritfilm>() {
            @Override
            public void onResponse(Call<Favoritfilm> call, Response<Favoritfilm> response) {
                Log.d("TAG", response.code() + "");
                Log.i(this.getClass().toString(), response.body().getTitle());
            }

            @Override
            public void onFailure(Call<Favoritfilm> call, Throwable throwable) {
                Log.d("TAG", "Nu blev det fel " + throwable.getMessage());
            }
        });
    }
}