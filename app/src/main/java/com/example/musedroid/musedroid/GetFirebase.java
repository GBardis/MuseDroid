package com.example.musedroid.musedroid;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by frcake on 18/8/2017.
 */

public class GetFirebase extends AppCompatActivity {

    private FirebaseHandler firebaseHandler = new FirebaseHandler();


    public MuseumAdapter listViewFromFirebase(MuseumAdapter adapter, ProgressBar progressBar, View view) {

        firebaseHandler.getMuseums(adapter, progressBar, view);
        return adapter;
    }
}
