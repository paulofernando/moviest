package br.net.paulofernando.moviest.data.entities;

import com.google.gson.annotations.SerializedName;

public class Company {
    @SerializedName("id")
    public Integer id;
    @SerializedName("name")
    public String name;
}
