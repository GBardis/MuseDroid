package com.example.musedroid.musedroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ExhibitShowActivity extends AppCompatActivity {
    public static Exhibit exhibit;
    TextView exhibitName, exhibitDescription;
    Intent intent;
    Context context;
    String exhibitId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_show);
        context = this;
        intent = getIntent();
        exhibitId = intent.getStringExtra("exhibitId");
        getExhibitById(exhibitId);
    }


    public void getExhibitById(final String id) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = database.getReference();
        mDatabase.child("exhibits").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                exhibit = dataSnapshot.getValue(Exhibit.class);
                try {
                    exhibitDescription = (TextView) findViewById(R.id.exhibitDescription);
                    exhibitDescription.setText(exhibit.description);
                    exhibitName = (TextView) findViewById(R.id.exhibitName);
                    exhibitName.setText(exhibit.name);
                } catch (Exception ex) {

                    intent = getIntent();
                    Exhibit exhibit = intent.getParcelableExtra("Exhibit");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}


