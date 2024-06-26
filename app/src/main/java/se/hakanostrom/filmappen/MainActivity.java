package se.hakanostrom.filmappen;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import se.hakanostrom.filmappen.database.FavoritfilmDao;
import se.hakanostrom.filmappen.database.FilmappenDatabas;
import se.hakanostrom.filmappen.model.Favoritfilm;

public class MainActivity extends AppCompatActivity {

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
        findViewById(R.id.nav_search).setOnClickListener(v -> {
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
        aaFavoritfilmer = new ArrayAdapter<>(MainActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, new ArrayList<>());
        lvFavoritfilmer.setAdapter(aaFavoritfilmer);
        lvFavoritfilmer.setEmptyView(findViewById(R.id.tvEmptyList));

        lvFavoritfilmer.setOnItemClickListener((parent, view, position, id) -> {

            final Favoritfilm favoritfilm = aaFavoritfilmer.getItem(position);

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

            alert.setPositiveButton("Stäng", null);
            alert.setNegativeButton("Ändra betyg", (dialog, which) -> changeRating(position));
            alert.setNeutralButton("Ta bort", (dialog, which) -> {
                aaFavoritfilmer.remove(favoritfilm);

                new Thread(() -> {
                    FavoritfilmDao favoritfilmDao = db.favoritfilmDao();
                    favoritfilmDao.delete(favoritfilm);
                }).start();
            });

            alert.show();
        });

        // db
        db = FilmappenDatabas.getInstance(getApplicationContext());

        try {
            // version display
            TextView tvVersion = findViewById(R.id.tvVersion);
            String versionText = "v" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            tvVersion.setText(versionText);
            tvVersion.setOnClickListener(v -> {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.app_name))
                        .setMessage(R.string.app_description)
                        .setPositiveButton(android.R.string.yes, null)
                        .show();
            });

        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        ((RadioButton) findViewById(R.id.rbMainFilterAll)).setChecked(true);

        // Database operations in separate thread
        new Thread(this::populateFavoritfilmListFromDb).start();

    }


    /*
     Helper functions
     */

    private void changeRating(int position) {

        final Favoritfilm favoritfilm = aaFavoritfilmer.getItem(position);

        final NumberPicker numberPicker = new NumberPicker(MainActivity.this);
        numberPicker.setMaxValue(getResources().getInteger(R.integer.betyg_max));
        numberPicker.setMinValue(getResources().getInteger(R.integer.betyg_min));
        numberPicker.setValue(favoritfilm.getValtBetyg());

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this)
                .setView(numberPicker)
                .setTitle("Ange nytt betyg")
                .setPositiveButton("Spara betyg", (dialog, which) -> {

                    // Favoritfilmen är en direktreferens till den i listan så den uppdateras automatiskt
                    favoritfilm.setValtBetyg(numberPicker.getValue());

                    // Spara i databas
                    new Thread(() -> {
                        FavoritfilmDao favoritfilmDao = db.favoritfilmDao();
                        favoritfilmDao.uppdateraBetyg(favoritfilm.getImdbID(), favoritfilm.getValtBetyg());
                    }).start();

                })
                .setNegativeButton("Avbryt", null);

        alert.show();
    }

    private void populateFavoritfilmListFromDb() {

        FavoritfilmDao favoritfilmDao = db.favoritfilmDao();
        favoritfilmer = favoritfilmDao.getAll();
        Log.d(MainActivity.class.getCanonicalName(), "Found favvosar: " + favoritfilmer.size());

        runOnUiThread(() -> {
            aaFavoritfilmer.clear();
            Collections.reverse(favoritfilmer);
            favoritfilmer.forEach(favoritfilm -> aaFavoritfilmer.add(favoritfilm));
        });

    }
}