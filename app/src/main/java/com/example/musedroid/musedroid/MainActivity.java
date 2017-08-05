package com.example.musedroid.musedroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final ListView listView = (ListView) findViewById(R.id.LIstView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        listView.setAdapter(adapter);


        mDatabase.child("museums").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                adapter.add((String) dataSnapshot.child("name").getValue());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                adapter.remove((String) dataSnapshot.child("name").getValue());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // firebase.createMuseum("1", new Museum("1", "museum akropolis", "The Acropolis Museum (Greek: Μουσείο Ακρόπολης, Mouseio Akropolis) is an archaeological museum focused on the findings of the archaeological site of the Acropolis of Athens.", "37.968450", "23.728523"));

        //  firebase.createMuseum("2", new Museum("2", "museum goulandri", "The Goulandris Museum of Natural History is a museum in Kifisia, a northeastern suburb of Athens, Greece. It was founded by Angelos Goulandris and Niki Goulandris in 1965 in order to promote interest in the natural sciences, to raise the awareness of the public, in general, and in particular to call its attention to the need to protect Greece's natural wildlife habitats and species in the danger of extinction.", "38.074472", "23.814854"));


    }
//    public void getMuseum() {
//        // Use Firebase to populate the list.
//
//    }

}