package br.net.paulofernando.moviest.data.entities;

import com.google.gson.annotations.SerializedName;

public class Cast {
    @SerializedName("cast_id")
    public Integer castId;
    @SerializedName("character")
    public String character;
    @SerializedName("credit_id")
    public String creditId;
    @SerializedName("id")
    public Integer id;
    @SerializedName("name")
    public String name;
    @SerializedName("order")
    public Integer order;
    @SerializedName("profile_path")
    public String profilePath;
}
