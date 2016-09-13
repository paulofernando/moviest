package br.net.paulofernando.moviest.communication.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Credits {

    @SerializedName("cast")
    public List<Cast> cast;
    @SerializedName("crew")
    public List<Crew> crew;

}
