package br.net.paulofernando.moviest.communication.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Page {
    @SerializedName("page")
    public Integer page;
    @SerializedName("results")
    public List<Movie> movies;
}
