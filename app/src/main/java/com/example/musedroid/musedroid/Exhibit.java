package com.example.musedroid.musedroid;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gdev on 24/8/2017.
 */

class Exhibit implements Parcelable {
    public static final Creator<Exhibit> CREATOR = new Creator<Exhibit>() {
        @Override
        public Exhibit createFromParcel(Parcel in) {
            return new Exhibit(in);
        }

        @Override
        public Exhibit[] newArray(int size) {
            return new Exhibit[size];
        }
    };
    String key;
    String name;
    String description;
    String musuemId;

    public Exhibit() {

    }

    public Exhibit(String key, String name, String description, String musuemId) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.musuemId = musuemId;
    }

    private Exhibit(Parcel in) {
        key = in.readString();
        name = in.readString();
        description = in.readString();
        musuemId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(musuemId);
    }
}
