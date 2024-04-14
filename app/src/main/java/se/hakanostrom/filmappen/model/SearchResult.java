package se.hakanostrom.filmappen.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResult {
    @SerializedName("totalResults")
    public String totalResults;

    @SerializedName("Response")
    public String response;

    @SerializedName("Error")
    public String error;

    @SerializedName("Search")
    public List<SingleResult> singleResultList;

    public static class SingleResult {
        @SerializedName("Title")
        public String title;

        @Override
        public String toString() {
            return String.format("%s (%s)", title, year);
        }

        @SerializedName("Year")
        public String year;


        @SerializedName("imdbID")
        public String imdbID;


        @SerializedName("Type")
        public String type;


        @SerializedName("Poster")
        public String posterUrl;
    }
}
