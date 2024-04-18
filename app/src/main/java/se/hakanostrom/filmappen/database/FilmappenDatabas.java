package se.hakanostrom.filmappen.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import se.hakanostrom.filmappen.R;
import se.hakanostrom.filmappen.model.Favoritfilm;

@Database(entities = {Favoritfilm.class}, version = 1)
public abstract class FilmappenDatabas extends RoomDatabase {
    public abstract FavoritfilmDao favoritfilmDao();

    static FilmappenDatabas instance;

    public static synchronized FilmappenDatabas getInstance(Context ctx) {

        if (instance == null) {
            instance = Room.databaseBuilder(ctx, FilmappenDatabas.class, ctx.getResources().getString(R.string.database_name)).build();
        }
        return instance;
    }
}