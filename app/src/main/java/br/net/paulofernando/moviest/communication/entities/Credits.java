package br.net.paulofernando.moviest.communication.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Credits implements Parcelable{

    @SerializedName("cast")
    public List<Cast> cast;
    @SerializedName("crew")
    public List<Crew> crew;

    protected Credits(Parcel in) {
        in.readList(cast, null);
        in.readList(crew, null);
    }

    public static final Creator<Credits> CREATOR = new Creator<Credits>() {
        @Override
        public Credits createFromParcel(Parcel in) {
            return new Credits(in);
        }

        @Override
        public Credits[] newArray(int size) {
            return new Credits[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(cast);
        dest.writeList(crew);
    }
}
