package com.example.musedroid.musedroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {


    FirebaseHandler firebase = new FirebaseHandler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebase.createMuseum("1",new Museum("museum akropolis", "The Acropolis Museum (Greek: Μουσείο Ακρόπολης, Mouseio Akropolis) is an archaeological museum focused on the findings of the archaeological site of the Acropolis of Athens.", "37.968450", "23.728523"));

        firebase.createMuseum("2",new Museum("museum goulandri", "The Goulandris Museum of Natural History is a museum in Kifisia, a northeastern suburb of Athens, Greece. It was founded by Angelos Goulandris and Niki Goulandris in 1965 in order to promote interest in the natural sciences, to raise the awareness of the public, in general, and in particular to call its attention to the need to protect Greece's natural wildlife habitats and species in the danger of extinction.", "38.074472", "23.814854"));
    }

    // function that creates nosql entries from museum object
    private void getMuseum() {
        ValueEventListener postListener = new ValueEventListener() {
            public static final String TAG = "";

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Museum museum = dataSnapshot.getValue(Museum.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }

        };
    }
}
