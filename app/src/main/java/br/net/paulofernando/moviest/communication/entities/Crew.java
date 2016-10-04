package br.net.paulofernando.moviest.communication.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Crew implements Parcelable {
    @SerializedName("credit_id") public String creditId;
    @SerializedName("department") public String department;
    @SerializedName("id") public Integer id;
    @SerializedName("job") public String job;
    @SerializedName("name") public String name;
    @SerializedName("profile_path") public String profilePath;

    protected Crew(Parcel in) {
        creditId = in.readString();
        department = in.readString();
        id = in.readInt();
        job = in.readString();
        name = in.readString();
        profilePath = in.readString();
    }

    public static final Creator<Crew> CREATOR = new Creator<Crew>() {
        @Override
        public Crew createFromParcel(Parcel in) {
            return new Crew(in);
        }

        @Override
        public Crew[] newArray(int size) {
            return new Crew[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(creditId);
        dest.writeString(department);
        dest.writeInt(id);
        dest.writeString(job);
        dest.writeString(name);
        dest.writeString(profilePath);
    }
}
