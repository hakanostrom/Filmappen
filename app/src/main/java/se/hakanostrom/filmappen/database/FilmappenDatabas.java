package se.hakanostrom.filmappen.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import se.hakanostrom.filmappen.entity.User;
import se.hakanostrom.filmappen.model.Favoritfilm;

@Database(entities = {User.class, Favoritfilm.class}, version = 1)
public abstract class FilmappenDatabas extends RoomDatabase {
    public abstract FavoritfilmDao favoritfilmDao();

    public abstract UserDao userDao();
}
