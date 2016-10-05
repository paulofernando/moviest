package br.net.paulofernando.moviest.communication.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Genres {
    @SerializedName("genres")
    public List<Genre> genres;
}
