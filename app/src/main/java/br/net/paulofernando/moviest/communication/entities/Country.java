package br.net.paulofernando.moviest.communication.entities;

import com.google.gson.annotations.SerializedName;

public class Country {
    @SerializedName("iso_3166_1")
    public String iso31661;
    @SerializedName("name")
    public String name;
}
