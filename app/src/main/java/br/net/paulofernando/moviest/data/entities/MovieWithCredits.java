package br.net.paulofernando.moviest.data.entities;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.*;

public class MovieWithCredits extends Movie  {

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
    }
}
