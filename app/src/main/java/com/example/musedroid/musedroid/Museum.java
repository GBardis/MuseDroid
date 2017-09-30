package com.example.musedroid.musedroid;

//Class that handles database object inputs that persists in database
// see function createMuseum

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Museum implements Parcelable {
    String key;
    String name;
    String description;
    String lat;
    String lon;
    String placeId;
    String distance = "";
    String address;
    Bitmap photo;
    String website;
    float rating;

    public Museum() {

    }

    //Added
    public String getPlaceId(){return placeId;}

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getAddress() {
        return address;
    }

    public String getWebsite() {
        return website;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public Museum(String name, String description, String lat, String lon,String placeId,String distance,
                  String address,String key, Bitmap photo, String website, float rating) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.lat = lat;
        this.lon = lon;
        this.placeId = placeId;
        this.distance = distance;
        this.address = address;
        this.photo = photo;
        this.website = website;
        this.rating = rating;
    }

    protected Museum(Parcel in) {
        key = in.readString();
        name = in.readString();
        description = in.readString();
        lat = in.readString();
        lon = in.readString();

        placeId = in.readString();
        distance = in.readString();
        address = in.readString();
        photo = in.readParcelable(Bitmap.class.getClassLoader());
        website = in.readString();
        rating = in.readFloat();
    }
    //he writeToParcel() method flattens the Parcelable object into a parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(lat);
        dest.writeString(lon);
        dest.writeString(placeId);
        dest.writeString(distance);
        dest.writeString(address);
        dest.writeParcelable(photo, flags);
        dest.writeString(website);
        dest.writeFloat(rating);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    //generates instances of your Parcelable class
    public static final Creator<Museum> CREATOR = new Creator<Museum>() {
        @Override
        public Museum createFromParcel(Parcel in) {
            //createFromParcel  receives the parcel as a parameter and returns the Parcelable object
            return new Museum(in);
        }
        //array of objects, of type Museum object from a Parcel
        @Override
        public Museum[] newArray(int size) {
            return new Museum[size];
        }
    };

    @Override
    public String toString() {
        return this.name.toString();
    }
}
