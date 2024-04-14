package se.hakanostrom.filmappen.model;

public class Favoritfilm {
    private String imdbID;
    private String Title;

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
