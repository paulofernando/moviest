package br.net.paulofernando.moviest.communication.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Images {

    public static class Image {
        @SerializedName("file_path")
        public String filePath;
        @SerializedName("width")
        public Integer width;
        @SerializedName("height")
        public Integer height;
        @SerializedName("aspect_ratio")
        public Float aspectRatio;
        @SerializedName("vote_average")
        public Float voteAverage;
        @SerializedName("vote_count")
        public Integer voteCount;
    }

    @SerializedName("id")
    public Integer id;
    @SerializedName("backdrops")
    public List<Image> backdrops;
    @SerializedName("posters")
    public List<Image> posters;
}
