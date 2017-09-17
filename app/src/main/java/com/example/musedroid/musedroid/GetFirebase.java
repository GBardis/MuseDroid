package com.example.musedroid.musedroid;

import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frcake on 18/8/2017.
 */

public class GetFirebase extends AppCompatActivity {

    private FirebaseHandler firebaseHandler = new FirebaseHandler();


    public ArrayAdapter<Museum> listViewFromFirebase(ArrayAdapter<Museum> adapter, List<Museum> museumList) {

        firebaseHandler.getMuseums(adapter, museumList);
        return adapter;
    }


    public ArrayList<Museum> getMuseumList() {
        return firebaseHandler.getMuseums();
    }
}
