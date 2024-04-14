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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import se.hakanostrom.filmappen.database.FavoritfilmDao;
import se.hakanostrom.filmappen.database.FilmappenDatabas;
import se.hakanostrom.filmappen.model.Favoritfilm;

public class MainActivity extends AppCompatActivity {

    private Button btnNavSearch;
    FilmappenDatabas db;

    ListView lvFavoritfilmer;
    ArrayAdapter<Favoritfilm> aaFavoritfilmer;

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

        // Favoritlista
        lvFavoritfilmer = findViewById(R.id.lvFavoritfilmer);
        aaFavoritfilmer = new ArrayAdapter<Favoritfilm>(MainActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, new ArrayList<>());
        lvFavoritfilmer.setAdapter(aaFavoritfilmer);

        lvFavoritfilmer.setOnItemClickListener((parent, view, position, id) -> {

        });

        // db
        // borde läggas i en singleton ...
        db = Room.databaseBuilder(getApplicationContext(), FilmappenDatabas.class, getString(R.string.database_name)).build();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // DEBUG
        // läs in bitström till fil

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("movie-poster", Context.MODE_PRIVATE);
        File myPosterFile = new File(directory, "tt0066385.png");

        if (myPosterFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(myPosterFile.getAbsolutePath());
            ImageView ivPoster = findViewById(R.id.ivPosterPic);
            ivPoster.setImageBitmap(myBitmap);
        }

        // Database operations in separate thread
        new Thread(this::populateFavoritfilmList).start();

    }

    private void populateFavoritfilmList() {

        FavoritfilmDao favoritfilmDao = db.favoritfilmDao();
        List<Favoritfilm> favvosar = favoritfilmDao.getAll();
        Log.d(MainActivity.class.getCanonicalName(), "Found favvosar: " + favvosar.size());

        runOnUiThread(() -> {
            aaFavoritfilmer.clear();
            favvosar.forEach(favoritfilm -> {
                Log.d(MainActivity.class.getCanonicalName(), favoritfilm.getTitle() + " - " + favoritfilm.getPlot());
                aaFavoritfilmer.add(favoritfilm);
            });
        });

    }
}