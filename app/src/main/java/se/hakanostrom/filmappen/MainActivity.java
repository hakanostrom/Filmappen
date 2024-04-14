package se.hakanostrom.filmappen;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button btnNavSearch;

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
    }
}