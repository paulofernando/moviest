package br.net.paulofernando.moviest.data.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Videos {

    public static class Video {
        @SerializedName("id")
        public String id;
        @SerializedName("iso_639_1")
        public String iso_639_1;
        @SerializedName("iso_3166_1")
        public String iso_3166_1;
        @SerializedName("key")
        public String key;
        @SerializedName("name")
        public String name;
        @SerializedName("site")
        public String site;
        @SerializedName("size")
        public Integer size;
        @SerializedName("type")
        public String type;

    }

    @SerializedName("id")
    public Integer id;
    @SerializedName("results")
    public List<Video> results;

}
