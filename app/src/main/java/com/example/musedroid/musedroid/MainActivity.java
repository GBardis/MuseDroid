package com.example.musedroid.musedroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        createMuseum("1", "george", "bardis");
        createMuseum("2", "john", "bardis");
        createMuseum("3", "michael", "michael");
        createMuseum("4", "lila", "lila");
        createMuseum("5", "lala", "lala");

    }

    // function that creates nosql entries from museum object
    private void createMuseum(String museumId, String name, String description) {
        Museum museum = new Museum(museumId, name, description);
        mDatabase.child("museums").child(museumId).setValue(museum);
    }
}
