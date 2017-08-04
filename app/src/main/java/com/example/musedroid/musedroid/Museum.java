package com.example.musedroid.musedroid;

//Class that handles database object inputs that persists in database
// see function createMuseum

public class Museum {
    String museumId;
    String name;
    String description;
    String lat;
    String lon;

    public Museum(String museumId, String name, String description,String lat,String lon) {
        this.museumId = museumId;
        this.name = name;
        this.description = description;
        this.lat = lat;
        this.lon = lon;
    }
}
