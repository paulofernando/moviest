package br.net.paulofernando.moviest.communication.entities;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Cast implements Parcelable{

    @SerializedName("cast_id") public Integer castId;
    @SerializedName("character") public String character;
    @SerializedName("credit_id") public String creditId;
    @SerializedName("id") public Integer id;
    @SerializedName("name") public String name;
    @SerializedName("order") public Integer order;
    @SerializedName("profile_path") public String profilePath;

    protected Cast(Parcel in) {
        castId = in.readInt();
        character = in.readString();
        creditId = in.readString();
        id = in.readInt();
        name = in.readString();
        order = in.readInt();
        profilePath = in.readString();
    }

    public static final Creator<Cast> CREATOR = new Creator<Cast>() {
        @Override
        public Cast createFromParcel(Parcel in) {
            return new Cast(in);
        }

        @Override
        public Cast[] newArray(int size) {
            return new Cast[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(castId);
        dest.writeString(character);
        dest.writeString(creditId);
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(order);
        dest.writeString(profilePath);
    }
}
