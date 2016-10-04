package br.net.paulofernando.moviest.communication.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Movie implements Parcelable {

    @SerializedName("id") public Integer id;
    @SerializedName("backdrop_path") public String backdropPath;
    @SerializedName("genre_ids") public List<Integer> genres;
    @SerializedName("genres") public List<Genre> genresList;
    @SerializedName("homepage") public String homepage;
    @SerializedName("imdb_id") public String imdbId;
    @SerializedName("original_title") public String originalTitle;
    @SerializedName("overview") public String overview;
    @SerializedName("popularity") public Double popularity;
    @SerializedName("poster_path") public String posterPath;
    @SerializedName("release_date") public String releaseDate; //changed to String because the API was returning a wrong format in the search service
    @SerializedName("title") public String title;
    @SerializedName("vote_average") public Double voteAverage;
    @SerializedName("vote_count") public Integer voteCount;
    @SerializedName("video") public boolean video;
    public Videos videos;

    protected Movie(Parcel in) {
        id = in.readInt();
        backdropPath = in.readString();
        //in.readList(genres, Genre.class.getClassLoader());
        //in.readList(genresList, null);
        homepage = in.readString();
        imdbId = in.readString();
        originalTitle = in.readString();
        overview = in.readString();
        popularity = in.readDouble();
        posterPath = in.readString();
        releaseDate = in.readString();
        title = in.readString();
        voteAverage = in.readDouble();
        voteCount = in.readInt();
        video = in.readByte() != 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(backdropPath);
        //dest.writeList(genres);
        //dest.writeList(genresList);
        dest.writeString(homepage);
        dest.writeString(imdbId);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        dest.writeDouble(popularity);
        dest.writeString(posterPath);
        dest.writeString(releaseDate);
        dest.writeString(title);
        dest.writeDouble(voteAverage);
        dest.writeInt(voteCount);
        dest.writeInt(video ? 1 : 0);

    }
}
