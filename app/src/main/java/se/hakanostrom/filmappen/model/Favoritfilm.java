package se.hakanostrom.filmappen.model;

public class Favoritfilm {
    private String imdbID;
    private String imdbRating;
    private String Title;
    private String Type;

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

    public String getPlot() {
        return Plot;
    }

    public Favoritfilm setPlot(String plot) {
        Plot = plot;
        return this;
    }

    private String Plot;

    public String getValtBetyg() {
        return valtBetyg;
    }

    public Favoritfilm setValtBetyg(String valtBetyg) {
        this.valtBetyg = valtBetyg;
        return this;
    }

    private String valtBetyg;

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
}
