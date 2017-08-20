package com.example.musedroid.musedroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by frcake on 18/8/2017.
 */

public class GetFirebase extends AppCompatActivity {
    Intent intent;
    private FirebaseHandler firebaseHandler = new FirebaseHandler();


    public ArrayAdapter<Museum> listViewFromFirebase(ArrayAdapter<Museum> adapter, List<Museum> museumList) {

        firebaseHandler.getMuseums(adapter, museumList);
        return adapter;
    }

}
