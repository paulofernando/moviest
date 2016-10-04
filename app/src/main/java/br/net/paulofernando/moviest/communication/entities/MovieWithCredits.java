package br.net.paulofernando.moviest.communication.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.*;

public class MovieWithCredits extends Movie implements Parcelable {

    @SerializedName("adult") public Boolean adult;
    @SerializedName("original_language") public String originalLanguage;
    @SerializedName("production_companies") public List<Company> productionCompanies;
    @SerializedName("production_countries") public List<Country> productionCountries;
    @SerializedName("revenue") public Integer revenue;
    @SerializedName("runtime") public Long runtime;
    @SerializedName("spoken_languages") public List<Country> spokenLanguages;
    @SerializedName("status") public String status;
    @SerializedName("tagline") public String tagline;
    @SerializedName("credits") public Credits credits;

    protected MovieWithCredits(Parcel in) {
        super(in);
        adult = in.readByte() != 0;
        originalLanguage = in.readString();
        in.readList(productionCompanies, null);
        in.readList(productionCountries, null);
        revenue = in.readInt();
        runtime = in.readLong();
        in.readList(spokenLanguages, null);
        status = in.readString();
        tagline = in.readString();
        credits = in.readParcelable(Credits.class.getClassLoader());
    }

    public static final Creator<MovieWithCredits> CREATOR = new Creator<MovieWithCredits>() {
        @Override
        public MovieWithCredits createFromParcel(Parcel in) {
            return new MovieWithCredits(in);
        }

        @Override
        public MovieWithCredits[] newArray(int size) {
            return new MovieWithCredits[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(adult ? 1 : 0);
        dest.writeString(originalTitle);
        dest.writeList(productionCompanies);
        dest.writeList(productionCountries);
        dest.writeInt(revenue);
        dest.writeLong(runtime);
        dest.writeList(spokenLanguages);
        dest.writeString(status);
        dest.writeString(tagline);
        dest.writeParcelable(credits, flags);
    }
}
