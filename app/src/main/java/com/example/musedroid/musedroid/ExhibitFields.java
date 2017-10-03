package com.example.musedroid.musedroid;

/**
 * Created by frcake on 20/9/2017.
 */

public class ExhibitFields {
    public String name;
    public String description;
    public String exhibit;
    public String language;
    ExhibitFields(){

    }

    ExhibitFields(String name , String description,String exhibit,String language){
        this.name = name;
        this.description = description;
        this.exhibit = exhibit;
        this.language = language;
    }
}
