package br.net.paulofernando.moviest.communication.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Credits implements Serializable {

    private static final long serialVersionUID = -571053915733583713L;

    @SerializedName("cast")
    public List<Cast> cast;
    @SerializedName("crew")
    public List<Crew> crew;

}
