package com.example.musedroid.musedroid;

import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by gdev-laptop on 4/8/2017.
 */

public class FirebaseHandler extends AppCompatActivity {

    public DatabaseReference mDatabase;

    public void createMuseum(String museumId,Museum museum) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("museums").child(museumId).setValue(museum);
    }
}
