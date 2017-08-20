package com.example.musedroid.musedroid;

//Class that handles database object inputs that persists in database
// see function createMuseum

import android.os.Parcel;
import android.os.Parcelable;

public class Museum implements Parcelable {

    public static final Creator<Museum> CREATOR = new Creator<Museum>() {
        @Override
        public Museum createFromParcel(Parcel in) {
            return new Museum(in);
        }

        @Override
        public Museum[] newArray(int size) {
            return new Museum[size];
        }
    };
    String name;
    String description;
    String lat;
    String lon;
    String placeId;
    String distance = "";

    public Museum() {

    }

    public Museum(String name, String description, String lat, String lon, String placeId, String distance) {

        this.name = name;
        this.description = description;
        this.lat = lat;
        this.lon = lon;
        this.placeId = placeId;
        this.distance = distance;
    }

    protected Museum(Parcel in) {
        name = in.readString();
        description = in.readString();
        lat = in.readString();
        lon = in.readString();
        placeId = in.readString();
        distance = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(lat);
        dest.writeString(lon);
        dest.writeString(placeId);
        dest.writeString(distance);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return this.name.toString();
    }
}
