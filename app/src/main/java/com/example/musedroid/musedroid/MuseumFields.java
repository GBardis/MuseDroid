package com.example.musedroid.musedroid;

/**
 * Created by gdev on 9/10/2017.
 */

public class MuseumFields {
    public String name;
    public String description;
    public String museum;
    public String language;
    public String shortDescription;
    MuseumFields(){

    }

    MuseumFields(String name , String description,String exhibit,String language,String shortDescription){
        this.name = name;
        this.description = description;
        this.museum = exhibit;
        this.language = language;
        this.shortDescription = shortDescription;
    }
}
