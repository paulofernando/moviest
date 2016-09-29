package br.net.paulofernando.moviest.communication.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Collection implements Parcelable {

    @SerializedName("title") public String title;
    @SerializedName("background_image_url") public String backgroundImageURL;
    @SerializedName("source_url") public String sourceURL;
    @SerializedName("movies_ids") public List<Integer> moviesIds;

    protected Collection(Parcel in) {
        title = in.readString();
        backgroundImageURL = in.readString();
        sourceURL = in.readString();
    }

    public static final Creator<Collection> CREATOR = new Creator<Collection>() {
        @Override
        public Collection createFromParcel(Parcel in) {
            return new Collection(in);
        }

        @Override
        public Collection[] newArray(int size) {
            return new Collection[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.backgroundImageURL);
        dest.writeString(this.sourceURL);
        dest.writeList(this.moviesIds);
    }
}
