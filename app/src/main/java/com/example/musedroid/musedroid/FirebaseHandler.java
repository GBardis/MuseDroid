package com.example.musedroid.musedroid;

import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by gdev-laptop on 4/8/2017.
 */

public class FirebaseHandler extends AppCompatActivity {
    public DatabaseReference mDatabase;
    public final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);

    // function that creates nosql entries from museum object
    public void createMuseum(String museumId, Museum museum) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("museums").child(museumId).setValue(museum);
    }
}
