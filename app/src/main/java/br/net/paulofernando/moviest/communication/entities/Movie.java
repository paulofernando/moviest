package br.net.paulofernando.moviest.communication.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class Movie implements Serializable {

    private static final long serialVersionUID = -8786346856404476366L;

    @SerializedName("id")
    public Integer id;
    @SerializedName("backdrop_path")
    public String backdropPath;
    @SerializedName("belongsToCollection")
    public Collection belongs_to_collection;
    @SerializedName("budget")
    public Integer budget;
    @SerializedName("genre_ids")
    public List<Integer> genres;
    @SerializedName("homepage")
    public String homepage;
    @SerializedName("imdb_id")
    public String imdbId;
    @SerializedName("original_title")
    public String originalTitle;
    @SerializedName("overview")
    public String overview;
    @SerializedName("popularity")
    public Double popularity;
    @SerializedName("poster_path")
    public String posterPath;
    @SerializedName("release_date")
    //changed to String because the API was returning a wrong format in the search service
    public String releaseDate;
    @SerializedName("title")
    public String title;
    @SerializedName("vote_average")
    public Double voteAverage;
    @SerializedName("vote_count")
    public Integer voteCount;
    @SerializedName("video")
    public boolean video;

    public Videos videos;
}
