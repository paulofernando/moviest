package br.net.paulofernando.moviest.communication.entities;

import com.google.gson.annotations.SerializedName;

public class Language {
    @SerializedName("iso_639_1")
    public Integer iso6391;
    @SerializedName("name")
    public String name;
}
