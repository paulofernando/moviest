package br.net.paulofernando.moviest.data.entities;

import com.google.gson.annotations.SerializedName;

public class Crew {
    @SerializedName("credit_id")
    public String creditId;
    @SerializedName("department")
    public String department;
    @SerializedName("id")
    public Integer id;
    @SerializedName("job")
    public String job;
    @SerializedName("name")
    public String name;
    @SerializedName("profile_path")
    public String profilePath;
}
