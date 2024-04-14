package se.hakanostrom.filmappen;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.hakanostrom.filmappen.api.OmdbClient;
import se.hakanostrom.filmappen.api.OmdbInterface;
import se.hakanostrom.filmappen.api.OmdbPosterClient;
import se.hakanostrom.filmappen.api.OmdbPosterInterface;
import se.hakanostrom.filmappen.database.FavoritfilmDao;
import se.hakanostrom.filmappen.database.FilmappenDatabas;
import se.hakanostrom.filmappen.model.Favoritfilm;
import se.hakanostrom.filmappen.model.SearchResult;

public class SearchActivity extends AppCompatActivity {

    OmdbInterface omdbClient;
    EditText etSokord;
    TextView tvSokresultat;
    ListView lvSearchResults;
    ArrayAdapter<SearchResult.SingleResult> aaSearchResults;
    FilmappenDatabas db;

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

        // Retrofit client
        omdbClient = OmdbClient.getClient(SearchActivity.this).create(OmdbInterface.class);

        // Global views
        etSokord = findViewById(R.id.etSokord);
        tvSokresultat = findViewById(R.id.tvSokresultat);
        lvSearchResults = findViewById(R.id.lvSearchResults);

        // searchbox
        etSokord.setOnEditorActionListener((v, actionId, event) -> {
            if ((actionId == EditorInfo.IME_ACTION_SEARCH)) {
                doSearch(String.valueOf(etSokord.getText()));
            }
            return false;
        });
        findViewById(R.id.btnSok).setOnClickListener(v -> {
            doSearch(String.valueOf(etSokord.getText()));
        });

        // db
        db = Room.databaseBuilder(getApplicationContext(), FilmappenDatabas.class, getString(R.string.database_name)).build();

        // resultlist
        aaSearchResults = new ArrayAdapter<SearchResult.SingleResult>(SearchActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, new ArrayList<>());
        lvSearchResults.setAdapter(aaSearchResults);

        lvSearchResults.setOnItemClickListener((parent, view, position, id) -> {

            SearchResult.SingleResult selectedItem = aaSearchResults.getItem(position);

            // Alerta

            AlertDialog.Builder alert = new AlertDialog.Builder(SearchActivity.this);
            final EditText edittext = new EditText(SearchActivity.this);
            edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
            alert.setView(edittext);
            alert.setTitle(selectedItem.title);
            alert.setMessage("Vill du lägga till denna film som en favorit? Ange även ditt betyg (1-10)");

            alert.setPositiveButton("Ja", (dialog, whichButton) -> {
                String betyg = edittext.getText().toString();

                try {
                    // Hämta detaljer
                    getDetails(selectedItem.imdbID, betyg);

                    // Spara ned bild/poster
                    savePoster(selectedItem.imdbID);

                    // Spara data i databas (starta från async callback istället)
                    //saveToDatabase(favoritfilm);

                    // Stäng sidan
                    //SearchActivity.this.finish();

                } catch (IOException ioe) {
                    Log.d("TAG", "Nu blev det fel " + ioe.getMessage());
                    Toast.makeText(SearchActivity.this, "Kommunikationsfel", Toast.LENGTH_SHORT).show();

                }
            });

            alert.setNegativeButton("Avbryt", (dialog, whichButton) -> {
                // le och vinka
            });

            alert.show();

        });
    }

    /*
     Helper functions
     */

    private void doSearch(String sokord) {

        if (sokord.isEmpty())
            Toast.makeText(this, "Ange sökord", Toast.LENGTH_SHORT).show();
        else {
            Log.d(SearchActivity.this.getLocalClassName(), "Sök nu på " + sokord);

            Call<SearchResult> call = omdbClient.searchMovies(getResources().getString(R.string.api_key), sokord);
            call.enqueue(new Callback<SearchResult>() {
                @Override
                public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {

                    SearchResult searchResult = response.body();
                    if (searchResult.error != null) {
                        tvSokresultat.setText(searchResult.error);
                    } else {
                        tvSokresultat.setText(String.format("Hittade %s, visar %s första", searchResult.totalResults, searchResult.singleResultList.size()));
                        aaSearchResults.clear();
                        searchResult.singleResultList.forEach(singleResult -> {
                            aaSearchResults.add(singleResult);
                        });

                    }
                }

                @Override
                public void onFailure(Call<SearchResult> call, Throwable throwable) {
                    Log.d("TAG", "Nu blev det fel " + throwable.getMessage());
                    Toast.makeText(SearchActivity.this, "Kommunikationsfel", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void savePoster(String imdbId) {
        // Retrofit poster client
        OmdbPosterInterface omdbPosterClient = OmdbPosterClient.getClient(SearchActivity.this).create(OmdbPosterInterface.class);

        Call<ResponseBody> call = omdbPosterClient.getMoviePoster(getResources().getString(R.string.api_key), imdbId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.code() >= 400)
                    Toast.makeText(SearchActivity.this, "Filmposter kan inte hittas ", Toast.LENGTH_SHORT).show();
                else {
                    // Ta in bitström
                    InputStream bis = new BufferedInputStream(response.body().byteStream());
                    Bitmap bitmap = BitmapFactory.decodeStream(bis);

                    //spara bitström

                    ContextWrapper cw = new ContextWrapper(getApplicationContext());
                    File directory = cw.getDir("movie-poster", Context.MODE_PRIVATE);
                    if (!directory.exists()) {
                        directory.mkdir();
                    }
                    File posterpath = new File(directory, imdbId + ".png");
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(posterpath);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.close();
                    } catch (IOException e) {
                        Log.e(SearchActivity.class.getCanonicalName(), e.getMessage());
                        Toast.makeText(SearchActivity.this, "Fel vid sparande av poster", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e(SearchActivity.class.getCanonicalName(), throwable.getMessage());
                Toast.makeText(SearchActivity.this, "Nedladdningsfel av poster", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void getDetails(String imdbId, String betyg) throws IOException {

        Call<Favoritfilm> call = omdbClient.getMovieById(getResources().getString(R.string.api_key), imdbId);

        call.enqueue(new Callback<Favoritfilm>() {
            @Override
            public void onResponse(Call<Favoritfilm> call, Response<Favoritfilm> response) {

                if (response.code() >= 400) {
                    Toast.makeText(SearchActivity.this, "Filmens detaljer kan inte hittas ", Toast.LENGTH_SHORT).show();
                } else {
                    Favoritfilm favoritfilm = response.body();
                    favoritfilm.setValtBetyg(betyg);

                    // Database operations in separate thread
                    new Thread(() -> saveToDatabase(favoritfilm)).start();
                }

                // Stäng sidan
                SearchActivity.this.finish();
            }

            @Override
            public void onFailure(Call<Favoritfilm> call, Throwable throwable) {

            }
        });


    }

    private void saveToDatabase(Favoritfilm favoritfilm) {

        FavoritfilmDao favoritfilmDao = db.favoritfilmDao();
        favoritfilmDao.upsert(favoritfilm);

        Log.d(SearchActivity.class.getCanonicalName(), "Sparat till databas: " + favoritfilm.getTitle());
    }
}