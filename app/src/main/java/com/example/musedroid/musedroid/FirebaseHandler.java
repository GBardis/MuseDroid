package com.example.musedroid.musedroid;

import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by gdev-laptop on 4/8/2017.
 */

public class FirebaseHandler extends AppCompatActivity {
    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference mDatabase = database.getReference();


    // function that creates nosql entries from museum object
    public void createMuseum(String museumId, Museum museum) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("museums").child(museumId).setValue(museum);
    }

    public void getMuseums(final MuseumAdapter adapter) {
        mDatabase.child("museums").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //It is important for the adapter to works to use museumAdapter.notifyDataSetChanged(); after
                //firebase add all museum inside the list , triggers adapter to see the data changes
                adapter.add(dataSnapshot.getValue(Museum.class));
                adapter.notifyDataSetChanged();
//                adapter.getItem(adapter.getCount() - 1).key = dataSnapshot.getKey().toString();
//
//                museumList.add(dataSnapshot.getValue(Museum.class));
//                int i = 0;
//                for (Museum museum : museumList) {
//                    i++;
//                    if (i == museumList.size()) {
//                        museum.key = dataSnapshot.getKey();
//                    }
//                }
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
