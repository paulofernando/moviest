package br.net.paulofernando.moviest.communication.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Genre implements Serializable {
    private static final long serialVersionUID = 3035106469362742241L;
    @SerializedName("id")
    public Integer id;
    @SerializedName("name")
    public String name;
}
