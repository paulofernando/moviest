package br.net.paulofernando.moviest.communication.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Collection implements Serializable {

    public static final long serialVersionUID = 200L;

    @SerializedName("title")
    public String title;
    @SerializedName("background_image_url")
    public String backgroundImageURL;
    @SerializedName("source_url")
    public String sourceURL;
    @SerializedName("movies_ids")
    public List<Integer> moviesIds;
}
