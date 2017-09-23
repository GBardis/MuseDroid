package com.example.musedroid.musedroid;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by frcake on 18/8/2017.
 */

public class GetFirebase extends AppCompatActivity {

    private FirebaseHandler firebaseHandler = new FirebaseHandler();


    public MuseumAdapter listViewFromFirebase(MuseumAdapter adapter) {

        firebaseHandler.getMuseums(adapter);
        return adapter;
    }
}
