package se.hakanostrom.filmappen;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import se.hakanostrom.filmappen.database.FavoritfilmDao;
import se.hakanostrom.filmappen.database.FilmappenDatabas;
import se.hakanostrom.filmappen.model.Favoritfilm;

public class MainActivity extends AppCompatActivity {

    private Button btnNavSearch;
    FilmappenDatabas db;

    ListView lvFavoritfilmer;
    ArrayAdapter<Favoritfilm> aaFavoritfilmer;
    List<Favoritfilm> favoritfilmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // nav
        btnNavSearch = findViewById(R.id.nav_search);
        btnNavSearch.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(i);
        });

        // filter radios
        findViewById(R.id.rbMainFilterAll).setOnClickListener(v -> {
            aaFavoritfilmer.clear();
            aaFavoritfilmer.addAll(favoritfilmer);
        });
        findViewById(R.id.rbMainFilterMovie).setOnClickListener(v -> {
            aaFavoritfilmer.clear();
            aaFavoritfilmer.addAll(favoritfilmer.stream().filter(f -> f.getType().equals(getString(R.string.search_alt_movie_key))).collect(Collectors.toList()));
        });
        findViewById(R.id.rbMainFilterSeries).setOnClickListener(v -> {
            aaFavoritfilmer.clear();
            aaFavoritfilmer.addAll(favoritfilmer.stream().filter(f -> f.getType().equals(getString(R.string.search_alt_series_key))).collect(Collectors.toList()));
        });
        findViewById(R.id.rbMainFilterEpisode).setOnClickListener(v -> {
            aaFavoritfilmer.clear();
            aaFavoritfilmer.addAll(favoritfilmer.stream().filter(f -> f.getType().equals(getString(R.string.search_alt_episode_key))).collect(Collectors.toList()));
        });

        // Favoritlista
        favoritfilmer = new ArrayList<>();

        lvFavoritfilmer = findViewById(R.id.lvFavoritfilmer);
        aaFavoritfilmer = new ArrayAdapter<Favoritfilm>(MainActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, new ArrayList<>());
        lvFavoritfilmer.setAdapter(aaFavoritfilmer);
        lvFavoritfilmer.setEmptyView(findViewById(R.id.tvEmptyList));

        lvFavoritfilmer.setOnItemClickListener((parent, view, position, id) -> {

            Favoritfilm favoritfilm = aaFavoritfilmer.getItem(position);

            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            final ImageView imageView = new ImageView(MainActivity.this);

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("movie-poster", Context.MODE_PRIVATE);
            File myPosterFile = new File(directory, String.format("%s.png", favoritfilm.getImdbID()));

            if (myPosterFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(myPosterFile.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                alert.setView(imageView);
            }

            String alertMessage = String.format("Betyg: %s\n%s\n%s", favoritfilm.getValtBetyg(), favoritfilm.getType(), favoritfilm.getPlot());

            alert.setTitle(favoritfilm.getTitle());
            alert.setMessage(alertMessage);

            alert.setPositiveButton("stäng", (dialog, which) -> {

            });

            alert.setNegativeButton("ta bort", (dialog, which) -> {
                aaFavoritfilmer.remove(favoritfilm);

                new Thread(() -> {
                    FavoritfilmDao favoritfilmDao = db.favoritfilmDao();
                    favoritfilmDao.delete(favoritfilm);
                }).start();
            });

            alert.show();
        });

        // db
        // borde läggas i en singleton ...
        db = Room.databaseBuilder(getApplicationContext(), FilmappenDatabas.class, getString(R.string.database_name)).build();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Database operations in separate thread
        new Thread(() -> populateFavoritfilmListFromDb("")).start();

    }


    private void populateFavoritfilmListFromDb(String filterType) {

        FavoritfilmDao favoritfilmDao = db.favoritfilmDao();
        favoritfilmer = favoritfilmDao.getAll();
        Log.d(MainActivity.class.getCanonicalName(), "Found favvosar: " + favoritfilmer.size());

        // filter result
        favoritfilmer.stream().filter(f -> f.getType().equals(filterType));

        runOnUiThread(() -> {
            aaFavoritfilmer.clear();
            Collections.reverse(favoritfilmer);
            favoritfilmer.forEach(favoritfilm -> aaFavoritfilmer.add(favoritfilm));
        });

    }
}