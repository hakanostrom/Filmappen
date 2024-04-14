package se.hakanostrom.filmappen.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Favoritfilm {
    @PrimaryKey
    @NonNull
    public String imdbID;
    @ColumnInfo(name = "imdb_rating")
    public String imdbRating;
    @ColumnInfo(name = "title")
    public String Title;
    @ColumnInfo(name = "year")
    public String Year;
    @ColumnInfo(name = "type")
    public String Type;
    @ColumnInfo(name = "valt_betyg")
    public String valtBetyg;
    @ColumnInfo(name = "plot")
    public String Plot;

    public String getImdbRating() {
        return imdbRating;
    }

    public Favoritfilm setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
        return this;
    }

    public String getType() {
        return Type;
    }

    public Favoritfilm setType(String type) {
        Type = type;
        return this;
    }

    public String getYear() {
        return Year;
    }

    public Favoritfilm setYear(String year) {
        Year = year;
        return this;
    }

    public String getPlot() {
        return Plot;
    }

    public Favoritfilm setPlot(String plot) {
        Plot = plot;
        return this;
    }


    public String getValtBetyg() {
        return valtBetyg;
    }

    public Favoritfilm setValtBetyg(String valtBetyg) {
        this.valtBetyg = valtBetyg;
        return this;
    }


    public String getImdbID() {
        return imdbID;
    }

    public Favoritfilm setImdbID(String imdbID) {
        this.imdbID = imdbID;
        return this;
    }

    public String getTitle() {
        return Title;
    }

    public Favoritfilm setTitle(String title) {
        Title = title;
        return this;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", getTitle(), getYear());
    }
}
