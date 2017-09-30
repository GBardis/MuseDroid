package com.example.musedroid.musedroid;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by gdev-laptop on 4/8/2017.
 */

public class FirebaseHandler extends AppCompatActivity {
    public static boolean flag = false;
    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference mDatabase = database.getReference();

    public void getMuseums(final MuseumAdapter adapter, final ProgressBar progressBar, final View view) {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (flag == true) {
                    progressBar.setVisibility(view.GONE);
                    flag = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabase.child("museums").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //It is important for the adapter to works to use museumAdapter.notifyDataSetChanged(); after
                //firebase add all museum inside the list , triggers adapter to see the data changes
                if (flag == false) {
                    flag = true;
                }
                adapter.add(dataSnapshot.getValue(Museum.class));
                adapter.notifyDataSetChanged();
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

    public void userFavorite(String userId, String favoriteMuseum) {
//        User user = new User(email);
        User.Favorites userFavorite = new User.Favorites(favoriteMuseum);

        //String Key = mDatabase.child("users").push().getKey();
        // mDatabase.child("users").child(Key).setValue(user);
        mDatabase.child("users").child(userId).child("favorites").push().setValue(userFavorite);
    }

    public void getAllFavorites(final String userId,final ObservableUserFavoriteList.ObservableList listfav){
        mDatabase.child("users").child(userId).child("favorites").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                listfav.add(dataSnapshot.getValue(User.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

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
