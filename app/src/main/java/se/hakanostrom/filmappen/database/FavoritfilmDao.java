package se.hakanostrom.filmappen.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import se.hakanostrom.filmappen.model.Favoritfilm;

@Dao
public interface FavoritfilmDao {

    @Query("SELECT * FROM favoritfilm")
    List<Favoritfilm> getAll();

    @Query("UPDATE favoritfilm SET valt_betyg=:betyg WHERE imdbID=:imdbId")
    void uppdateraBetyg(String imdbId, int betyg);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(Favoritfilm favoritfilm);

    @Delete
    void delete(Favoritfilm favoritfilm);
}
