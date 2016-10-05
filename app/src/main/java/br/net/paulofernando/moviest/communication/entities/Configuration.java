package br.net.paulofernando.moviest.communication.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Configuration {

    public static class ImagesConfiguration {
        @SerializedName("base_url")
        public String baseUrl;
        @SerializedName("secure_base_url")
        public String secureBaseUrl;
        @SerializedName("poster_sizes")
        public List<String> posterSizes;
        @SerializedName("backdrop_sizes")
        public List<String> backdropSizes;
        @SerializedName("profile_sizes")
        public List<String> profileSizes;
        @SerializedName("logo_sizes")
        public List<String> logoSizes;
    }

    @SerializedName("images")
    public ImagesConfiguration images;
    @SerializedName("change_keys")
    public List<String> changeKeys;
}
