package br.net.paulofernando.moviest.data.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

public class Movie implements Parcelable {

    @SerializedName("id") public Integer id;
    @SerializedName("backdrop_path") public String backdropPath;
    @SerializedName("genre_ids") public int[] genreIds;
    @SerializedName("genres") public List<Genre> genresList;
    @SerializedName("original_title") public String originalTitle;
    @SerializedName("overview") public String overview;
    @SerializedName("popularity") public Float popularity;
    @SerializedName("poster_path") public String posterPath;
    @SerializedName("release_date") public String releaseDate; //changed to String because the API was returning a wrong format in the search service
    @SerializedName("title") public String title;
    @SerializedName("vote_average") public Float voteAverage;
    @SerializedName("vote_count") public Integer voteCount;
    @SerializedName("video") public boolean isVideo;
    public Videos videos;

    protected Movie(Parcel in) {
        this.id = in.readInt();
        this.backdropPath = in.readString();
        this.genreIds = in.createIntArray();
        this.originalTitle = in.readString();
        this.overview = in.readString();
        this.releaseDate = in.readString();
        this.posterPath = in.readString();
        this.popularity = in.readFloat();
        this.title = in.readString();
        this.isVideo = in.readByte() != 0;
        this.voteAverage = in.readFloat();
        this.voteCount = in.readInt();
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
        dest.writeInt(this.id);
        dest.writeString(this.backdropPath);
        dest.writeIntArray(this.genreIds);
        dest.writeString(this.originalTitle);
        dest.writeString(this.overview);
        dest.writeString(this.releaseDate);
        dest.writeString(this.posterPath);
        dest.writeFloat(this.popularity);
        dest.writeString(this.title);
        dest.writeByte(isVideo ? (byte) 1 : (byte) 0);
        dest.writeFloat(this.voteAverage);
        dest.writeInt(this.voteCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Movie movie = (Movie) o;

        if (isVideo != movie.isVideo) return false;
        if (!id.equals(movie.id)) return false;
        if (!backdropPath.equals(movie.backdropPath)) return false;
        if (!Arrays.equals(genreIds, movie.genreIds)) return false;
        if (genresList != null ? !genresList.equals(movie.genresList) : movie.genresList != null)
            return false;
        if (originalTitle != null ? !originalTitle.equals(movie.originalTitle) : movie.originalTitle != null)
            return false;
        if (!overview.equals(movie.overview)) return false;
        if (popularity != null ? !popularity.equals(movie.popularity) : movie.popularity != null)
            return false;
        if (posterPath != null ? !posterPath.equals(movie.posterPath) : movie.posterPath != null)
            return false;
        if (!releaseDate.equals(movie.releaseDate)) return false;
        if (!title.equals(movie.title)) return false;
        if (!voteAverage.equals(movie.voteAverage)) return false;
        if (!voteCount.equals(movie.voteCount)) return false;
        return videos != null ? videos.equals(movie.videos) : movie.videos == null;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + backdropPath.hashCode();
        result = 31 * result + Arrays.hashCode(genreIds);
        result = 31 * result + (genresList != null ? genresList.hashCode() : 0);
        result = 31 * result + (originalTitle != null ? originalTitle.hashCode() : 0);
        result = 31 * result + overview.hashCode();
        result = 31 * result + (popularity != null ? popularity.hashCode() : 0);
        result = 31 * result + (posterPath != null ? posterPath.hashCode() : 0);
        result = 31 * result + releaseDate.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + voteAverage.hashCode();
        result = 31 * result + voteCount.hashCode();
        result = 31 * result + (isVideo ? 1 : 0);
        result = 31 * result + (videos != null ? videos.hashCode() : 0);
        return result;
    }
}
