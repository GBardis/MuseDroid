package com.example.musedroid.musedroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frcake on 18/8/2017.
 */

public class GetFirebase extends AppCompatActivity {
    Intent intent;
    private FirebaseHandler firebaseHandler = new FirebaseHandler();
    private List<Exhibit> exhibitList = new ArrayList<>();

    public ArrayAdapter<Museum> listViewFromFirebase(ArrayAdapter<Museum> adapter, List<Museum> museumList) {

        firebaseHandler.getMuseums(adapter, museumList);
        return adapter;
    }

    public List<Exhibit> getExhibit(String id, List<Exhibit> exhibit) {
        firebaseHandler.getExibitById(id,exhibit);
        return exhibit;
    }

}
