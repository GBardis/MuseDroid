package com.example.musedroid.musedroid;

import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by gdev-laptop on 4/8/2017.
 */

public class FirebaseHandler extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();


    // function that creates nosql entries from museum object
    public void createMuseum(String museumId, Museum museum) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("museums").child(museumId).setValue(museum);
    }


    public void getMuseums(final ArrayAdapter<Museum> adapter, final List<Museum> museumList) {

        mDatabase.child("museums").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //String museumName = (String) dataSnapshot.child("name").getValue();
                //adapter.add(museumName);
                //museumList.add((Museum) dataSnapshot.getValue(Museum.class));
                //museumList.add((Museum) dataSnapshot.getValue(Museum.class));
                adapter.add(dataSnapshot.getValue(Museum.class));

                adapter.getItem(adapter.getCount() - 1).key = dataSnapshot.getKey().toString();

                museumList.add(dataSnapshot.getValue(Museum.class));
                int i = 0;
                for (Museum museum : museumList) {
                    i++;
                    if (i == museumList.size()) {
                        museum.key = dataSnapshot.getKey();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //  String museumName = (String) dataSnapshot.child("name")
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //adapter.remove((String) dataSnapshot.child("name").getValue());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
